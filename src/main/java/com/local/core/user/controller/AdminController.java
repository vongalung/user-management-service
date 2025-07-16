package com.local.core.user.controller;

import com.local.core.user.dto.request.UserSearchRequestDto;
import com.local.core.user.dto.response.UserActivationResponseDto;
import com.local.core.user.dto.response.UserInactivationResponseDto;
import com.local.core.user.dto.response.UserInfoResponseDto;
import com.local.core.user.dto.response.UserSearchResponseDto;
import com.local.core.user.exception.BaseApplicationException;
import com.local.core.user.service.AdminControllerService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
@Log4j2
public class AdminController {
    final AdminControllerService adminControllerService;

    @GetMapping("/user")
    public Page<UserSearchResponseDto> search(UserSearchRequestDto request) {
        log.debug("INCOMING REQUEST to Admin.search search={}", request);
        return adminControllerService.search(request);
    }

    @GetMapping("/user/{userId}")
    public UserInfoResponseDto findById(@PathVariable @NotNull UUID userId)
            throws BaseApplicationException {
        log.debug("INCOMING REQUEST to Admin.findById with userId={}", userId);
        return adminControllerService.findById(userId);
    }

    @PostMapping("/user/{userId}/activate")
    @Transactional
    public UserActivationResponseDto activateUser(@PathVariable @NotNull UUID userId)
            throws BaseApplicationException {
        log.debug("INCOMING REQUEST to Admin.activateUser with userId={}", userId);
        return adminControllerService.activateUser(userId);
    }

    @DeleteMapping("/user/{userId}")
    @Transactional
    public UserInactivationResponseDto inactivateUser(
            @PathVariable @NotNull UUID userId,
            @RequestParam(defaultValue = "false") boolean permanently)
            throws BaseApplicationException {
        log.debug("INCOMING REQUEST to Admin.inactivateUser with userId={} and permanent={}",
                userId, permanently);
        return adminControllerService.inactivateUser(userId, permanently);
    }
}
