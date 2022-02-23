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

    @Transactional
    public Tag getTag(Tag tag) {

        Tag findTag = findByTitle(tag.getName());
        if (findTag == null) {
            return saveTag(tag);
        }
        return findTag;
    }

    public Set<Tag> getTags(List<TagDto> tags) {
        return tags.stream()
                .map(TagDto::toEntity)
                .map(tag -> getTag(tag))
                .collect(Collectors.toSet());
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
