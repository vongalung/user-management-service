package com.local.core.user.controller;

import com.local.core.user.dto.request.NewUserRequestDto;
import com.local.core.user.dto.response.NewUserResponseDto;
import com.local.core.user.dto.response.UserInactivationResponseDto;
import com.local.core.user.dto.response.UserInfoResponseDto;
import com.local.core.user.exception.BaseApplicationException;
import com.local.core.user.service.UserControllerService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Log4j2
public class UserController {
    final UserControllerService userControllerService;

    @PostMapping
    @Transactional
    public NewUserResponseDto signup(@RequestBody @NotNull @Valid NewUserRequestDto request)
            throws BaseApplicationException {
        log.debug("INCOMING REQUEST to User.signup");
        return userControllerService.signup(request);
    }

    @GetMapping
    public UserInfoResponseDto userInfo() throws BaseApplicationException {
        log.debug("INCOMING REQUEST to User.userInfo");
        return userControllerService.userInfo();
    }

    @DeleteMapping
    @Transactional
    public UserInactivationResponseDto inactivateUser(
            @RequestParam(defaultValue = "false") boolean permanently)
            throws BaseApplicationException {
        log.debug("INCOMING REQUEST to User.inactivateUser with permanent={}", permanently);
        return userControllerService.inactivateUser(permanently);
    }
}
