package com.local.core.user.repository;

import com.local.core.user.common.PaginationFactory;
import com.local.core.user.dto.request.NewUserRequestDto;
import com.local.core.user.dto.request.UserSearchRequestDto;
import com.local.core.user.model.User;
import java.util.Optional;

public interface UserRepoExtension {
    Optional<User> findByUniqueIdentifier(NewUserRequestDto request);
    PaginationFactory<User> findAllWithParameters(UserSearchRequestDto parameters);
}
