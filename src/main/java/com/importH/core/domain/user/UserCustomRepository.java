package com.importH.core.domain.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface UserCustomRepository  {

    Slice<User> findAllUsers(Pageable pageable);
}
