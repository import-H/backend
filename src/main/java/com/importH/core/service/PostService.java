package com.importH.core.service;

import com.importH.core.domain.account.Account;
import com.importH.core.domain.post.Post;
import com.importH.core.domain.post.PostRepository;
import com.importH.core.domain.tag.Tag;
import com.importH.core.dto.post.PostRequestDto;
import com.importH.core.dto.tag.TagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;

    /**
     * 게시글 저장
     */
    @Transactional
    public Long registerPost(Account account, int type, PostRequestDto postRequestDto) {

        //TODO 이미지 파일 저장 
        Set<Tag> tags = postRequestDto.getTags().stream().map(TagDto::toEntity).map(tag -> tagService.getTag(tag)).collect(Collectors.toSet());
        Post post = postRequestDto.toEntity(account, new ArrayList<>(), tags, type);
        return savePost(post);
    }

    private Long savePost(Post post) {
        Post save = postRepository.save(post);

        return save.getId();
    }


    /*    *//**
     *
     * 전체 게시글 조회
     *//*
    public List<PostResponseDto> findAllPost(String boardId) {

    }*/


}
