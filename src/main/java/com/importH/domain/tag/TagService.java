package com.importH.domain.tag;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    // TODO 성능 최적화
    @Transactional
    public Tag getTag(Tag tag) {

        Tag findTag = findByTitle(tag.getName());
        if (findTag == null) {
            return saveTag(tag);
        }
        return findTag;
    }

    @Transactional
    public Set<Tag> getTags(List<TagDto> tags) {

        Set<Tag> collect = tags.stream().map(TagDto::toEntity).collect(Collectors.toSet());
        Set<String> stringSet = collect.stream().map(Tag::getName).collect(Collectors.toSet());

        Set<Tag> tagList = tagRepository.findAllByNameIn(stringSet);

        Set<Tag> tagSet = collect.stream().filter(tag -> !tagList.contains(tag)).map(tagRepository::save).collect(Collectors.toSet());
        tagList.addAll(tagSet);
        return tagList;

    }

    public Set<TagDto> getTagDtos(Set<Tag> tags) {
        return tags.stream().map(tag -> TagDto.fromEntity(tag)).collect(Collectors.toSet());
    }

    private Tag saveTag(Tag tag) {
        Tag save = tagRepository.save(tag);
        return save;
    }

    public Tag findByTitle(String name) {
        return tagRepository.findByName(name).orElse(null);
    }

}
