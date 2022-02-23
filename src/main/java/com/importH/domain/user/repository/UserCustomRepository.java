package com.importH.domain.user.repository;

import com.importH.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface UserCustomRepository  {

    Slice<User> findAllUsers(Pageable pageable);
}
