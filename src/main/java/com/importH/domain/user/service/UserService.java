package com.importH.domain.user.service;

import com.importH.domain.image.FileService;
import com.importH.domain.post.service.PostLikeService;
import com.importH.domain.user.dto.UserPostDto;
import com.importH.domain.post.service.PostScrapService;
import com.importH.domain.user.dto.PasswordDto;
import com.importH.domain.user.dto.SocialDto;
import com.importH.domain.user.dto.UserDto.Request;
import com.importH.domain.user.dto.UserDto.Response;
import com.importH.domain.user.dto.UserDto.Response_findAllUsers;
import com.importH.domain.user.entity.User;
import com.importH.domain.user.repository.UserRepository;
import com.importH.global.error.code.SecurityErrorCode;
import com.importH.global.error.exception.SecurityException;
import com.importH.global.error.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.importH.global.error.code.UserErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final PostScrapService postScrapService;

    private final PostLikeService postLikeService;

    private final FileService fileService;

    /**
     * 유저 프로필 정보 조회
     */
    public Response findUserById(Long userId, User user) {

        User findUser = getValidatedUser(userId, user);

        return Response.fromEntity(findUser);
    }

    private User getValidatedUser(Long userId, User user) {
        User findUser = findById(userId);
        isAuthorization(user, findUser);
        return findUser;
    }

    private void isAuthorization(User user, User findUser) {
        if(!findUser.equals(user)) {
            throw new SecurityException(SecurityErrorCode.ACCESS_DENIED);
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserException(NOT_FOUND_USERID));
    }

    /**
     * 유저 프로필 정보 수정
     */
    @Transactional
    public Response updateUser(Long userId, User user, Request request) {
        User findUser = getValidatedUser(userId, user);
        duplicatedNickname(request.getNickname(), user.getNickname());
        findUser.update(request);
        return Response.fromEntity(findUser);
    }

    private void duplicatedNickname(String changeName, String currentName) {
        if (!currentName.equals(changeName) && userRepository.existsByNickname(changeName)) {
            throw new UserException(USER_NICKNAME_DUPLICATED);
        }
    }

    /**
     * 유저 패스워드 변경
     */
    @Transactional
    public void updatePassword(Long userId, User user, PasswordDto.Request request) {
        User validatedUser = getValidatedUser(userId, user);
        isValidPassword(request);

        validatedUser.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    private void isValidPassword(PasswordDto.Request request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UserException(NOT_PASSWORD_EQUALS);
        }
    }

    /**
     * 유저 탈퇴 (delete 처리)
     */
    @Transactional
    public void deleteUser(Long userId, User user) {

        User findUser = getValidatedUser(userId, user);
        //TODO 프로필 이미지 삭제시 서버에서 삭제
//        deleteProfileImage(user);

        findUser.delete();
    }

    private void deleteProfileImage(User user) {
        if (user.hasProfileImage()) {
            fileService.deleteImage(user.getStoreProfileImage());
        }
    }

    public List<Response_findAllUsers> findAllUsers(Pageable pageable) {
        Slice<User> users = userRepository.findAllUsers(pageable);

        return users.stream()
                .map(user -> Response_findAllUsers.fromEntity(user))
                .collect(Collectors.toList());
    }

    /**
     * 소셜 로그인 게시판 ID 생성
     */
    @Transactional
    public void createPathId(Long userId, SocialDto socialDto) {
        User user = findById(userId);
        if (!isSocialUser(user)) {
            throw new UserException(NOT_CREATE_SOCIAL_PATH_ID);
        }
        if (userRepository.existsByPathId(socialDto.getPathId())) {
            throw new UserException(USER_PATH_ID_DUPLICATED);
        }
        user.setPathId(socialDto.getPathId());
    }


    private boolean isSocialUser(User user) {
        return notHavePathId(user) && user.getOauthId() != null;
    }

    private boolean notHavePathId(User user) {
        return user.getPathId() == null;
    }


    /**
     * 유저 스크랩 가져오기
     */
    public List<UserPostDto.Response> findAllScrap(Long userId, User loginUser, Pageable pageable) {
        User findUser = this.findById(userId);

        isAuthorization(loginUser,findUser);

        return postScrapService.findAllScrap(findUser,pageable);
    }

    /**
     * 유저 좋아요 한 게시글 가져오기
     */
    public List<UserPostDto.Response> findAllPostByLike(Long userId, User loginUser, Pageable pageable) {
        User findUser = this.findById(userId);

        isAuthorization(loginUser,findUser);

        return postLikeService.findAllPostLike(findUser,pageable);
    }
}
