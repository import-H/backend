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
    public Set<Tag> getTags(List<TagDto> tags) {

        Set<Tag> toEntityTags = tags.stream().map(TagDto::toEntity).collect(Collectors.toSet());
        Set<String> tagNames = toEntityTags.stream().map(Tag::getName).collect(Collectors.toSet());

        return findTags(toEntityTags, tagNames);

    }

    private Set<Tag> findTags(Set<Tag> toEntityTags, Set<String> tagNames) {
        Set<Tag> findTags = getAllByNameIn(tagNames);

        findTags.addAll(saveTagsNotExist(toEntityTags, findTags));

        return findTags;
    }

    private Set<Tag> getAllByNameIn(Set<String> tagNames) {
        return tagRepository.findAllByNameIn(tagNames);
    }

    private Set<Tag> saveTagsNotExist(Set<Tag> collect, Set<Tag> tagList) {
        return collect.stream()
                .filter(tag -> !tagList.contains(tag))
                .map(tagRepository::save)
                .collect(Collectors.toSet());
    }

    public Set<TagDto> getTagDtos(Set<Tag> tags) {
        return tags.stream().map(tag -> TagDto.fromEntity(tag)).collect(Collectors.toSet());
    }

    public Tag findByTitle(String name) {
        return tagRepository.findByName(name).orElse(null);
    }

}
