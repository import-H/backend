package com.importH.core.service;


import com.importH.core.domain.tag.Tag;
import com.importH.core.domain.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private Tag saveTag(Tag tag) {
        Tag save = tagRepository.save(tag);
        return save;
    }

    public Tag findByTitle(String name) {
        return tagRepository.findByName(name).orElse(null);
    }
}
