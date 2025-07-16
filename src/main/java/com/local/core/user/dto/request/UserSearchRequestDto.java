package com.local.core.user.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserSearchRequestDto implements BasePagingRequestDto {
    @PositiveOrZero
    private Integer page;
    @Positive
    private Integer pagesize;
    private Boolean isActive;
    private LocalDate joinedSinceStart;
    private LocalDate joinedSinceEnd;
    private String search;
    private String userName;
    private String name;
    private String email;
    private String phone;
}
