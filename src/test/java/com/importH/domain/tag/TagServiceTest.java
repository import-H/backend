package com.importH.domain.tag;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagService tagService;

    @Test
    @DisplayName("데이터베이스에 없는 태그 2개 조회시 2개 모두 저장 안되고 반환")
    void findTags_02() throws Exception {
        // given
        List<TagDto> list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            TagDto tagDto = getTagDto("name"+i);
            list.add(tagDto);
        }
        given(tagRepository.findAllByNameIn(any())).willReturn(list.stream().map(tagDto -> tagDto.toEntity()).collect(Collectors.toSet()));

        // when
        Set<Tag> tags = tagService.getTags(list);

        assertThat(tags).extracting(Tag::getName)
                .containsAnyElementsOf(list.stream().map(TagDto::getName).collect(Collectors.toSet()));

        //then
        verify(tagRepository, never()).save(any());
        verify(tagRepository, times(1)).findAllByNameIn(any());
    }


    @Test
    @DisplayName("데이터베이스에 있는 태그 2개 조회시 2개 모두 저장되고 반환")
    void findTags_01() throws Exception {
        // given
        List<TagDto> list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            TagDto tagDto = getTagDto("name"+i);
            list.add(tagDto);
        }
        given(tagRepository.save(any())).willReturn(Tag.builder().name(any()).build());

        // when
        tagService.getTags(list);

        //then
        verify(tagRepository, times(2)).save(any());
        verify(tagRepository, times(1)).findAllByNameIn(any());
    }

    @Test
    @DisplayName("데이터베이스에 없는 태그 3개 조회시 1개는 저장 되고 2개는 저장 안되고 반환")
    void findTags_03() throws Exception {
        // given
        List<TagDto> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            TagDto tagDto = getTagDto("name"+i);
            list.add(tagDto);
        }
        given(tagRepository.findAllByNameIn(any())).willReturn(list.stream().limit(2).map(tagDto -> tagDto.toEntity()).collect(Collectors.toSet()));
        given(tagRepository.save(any())).willReturn(any());

        // when
        Set<Tag> tags = tagService.getTags(list);

        assertThat(tags.size()).isEqualTo(3);

        //then
        verify(tagRepository, times(1)).save(any());
        verify(tagRepository, times(1)).findAllByNameIn(any());
    }

    private TagDto getTagDto(String name) {
        return TagDto.builder().name(name).build();
    }

}