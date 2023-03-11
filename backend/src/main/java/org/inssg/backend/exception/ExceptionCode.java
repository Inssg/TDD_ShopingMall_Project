package org.inssg.backend.exception;

import lombok.Getter;

public enum ExceptionCode {

    MEMBER_NOT_FOUND(404, "Member Not Found"),
    TOKEN_NOT_EXIST(401, "Token Not Exist"),
    TOKEN_NOT_VALID(401, "Token Not VALID"),
    TOKEN_NOT_ACCORD(401, "Token Not Accord"),
    MEMBER_ALREADY_LOGGED_IN(403, "Member Already Logged In"),
    ITEM_NOT_FOUND(400, "Item Not Found" );

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
