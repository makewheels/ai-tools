package com.github.makewheels.aitools.system.response;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(0, "success"),
    ERROR(1, "error"),

    USER_PHONE_VERIFICATION_CODE_WRONG(11, "验证码错误"),
    USER_PHONE_VERIFICATION_CODE_EXPIRED(12, "验证码已过期"),
    USER_TOKEN_WRONG(13, "登陆token校验未通过"),
    USER_NOT_EXIST(14, "用户不存在"),
    USER_NOT_LOGIN(15, "用户未登录"),
    ;

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
