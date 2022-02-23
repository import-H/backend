package com.importH.domain.post;

import com.importH.domain.comment.CommentDto;
import com.importH.domain.image.FileService;
import com.importH.domain.tag.Tag;
import com.importH.domain.tag.TagDto;
import com.importH.domain.tag.TagService;
import com.importH.domain.user.entity.User;
import com.importH.global.error.code.PostErrorCode;
import com.importH.global.error.exception.PostException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.importH.global.error.code.PostErrorCode.NOT_FOUND_POST;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final PostLikeRepository postLikeRepository;

    private final FileService fileService;

    /**
     * 게시글 저장
     */
    @Transactional
    public Post registerPost(User user, PostDto.Request postRequestDto) {

        Post post = postRequestDto.toEntity();

        setPostRelation(user, postRequestDto, post);

        return savePost(post);
    }

    private void setPostRelation(User user, PostDto.Request postRequestDto, Post post) {
        post.setTags(tagService.getTags(postRequestDto.getTags()));
        post.setUser(user);
        post.addImage(fileService.getPostImages(postRequestDto.getImages()));
    }


    private Post savePost(Post post) {
        Post save = postRepository.save(post);

        return save;
    }

    /**
     * 게시글 조회
     */
    @Transactional
    public PostDto.Response getPost(User user, Long postId) {

        Post post = findByPostId(postId);

        increaseViewCount(user, post);

        Set<TagDto> tags = tagService.getTagDtos(post.getTags());
        List<CommentDto.Response> comments = getCommentDtos(post);

        boolean isLike = havePostLike(user, post);

        return PostDto.Response.fromEntity(post, tags, comments, isLike);
    }

    private boolean havePostLike(User user, Post post) {
        return postLikeRepository.existsByUserAndPost(user, post);
    }

    private void increaseViewCount(User user, Post post) {
        if (isNotAuthor(user, post)) {
            post.increaseView();
        }
    }

    private boolean isNotAuthor(User user, Post post) {
        return user == null || user.getNickname() != post.getUser().getNickname();
    }

    public List<CommentDto.Response> getCommentDtos(Post post) {
        return post.getComments().stream().map(comment -> CommentDto.Response.fromEntity(comment)).collect(Collectors.toList());
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Long updatePost(User user, Long postId, PostDto.Request postRequestDto) {

        Post findPost = findByPostId(postId);

        Set<Tag> tags = tagService.getTags(postRequestDto.getTags());

        validateAccount(user, findPost);

        return findPost.updatePost(postRequestDto, tags);
    }

    private void validateAccount(User user, Post findPost) {
        if (!isEqualsAccount(user, findPost)) {
            throw new PostException(PostErrorCode.NOT_ACCORD_ACCOUNT);
        }
    }

    private boolean isEqualsAccount(User user, Post findPost) {
        return findPost.getUser().equals(user);
    }


    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(User user, Long postId) {
        Post findPost = findByPostId(postId);
        validateAccount(user, findPost);
        postRepository.delete(findPost);

        //TODO 이미지 서버에서 삭제
    }


    /**
     * 전체 게시글 조회
     */
    public List<PostDto.ResponseAll> findAllPost(String type, Pageable pageable) {

        Slice<Post> postSlice = postRepository.findAllPostsByType(type,pageable);

        List<Post> content = postSlice.getContent();

        return content.stream()
                .map(PostDto.ResponseAll::fromEntity)
                .collect(Collectors.toList());
    }

    public Post findByPostId(Long postsId) {
        return postRepository.findById(postsId).orElseThrow(() -> new PostException(NOT_FOUND_POST));
    }
}
