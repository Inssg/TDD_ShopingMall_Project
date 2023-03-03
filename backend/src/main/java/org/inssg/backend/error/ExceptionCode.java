package org.inssg.backend.error;

import lombok.Getter;

public enum ExceptionCode {

    MEMBER_NOT_FOUND(404, "Member Not Found"),
    TOKEN_NOT_EXIST(401, "Token Not Exist"),
    TOKEN_NOT_ACCORD(401, "Token Not Accord");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
