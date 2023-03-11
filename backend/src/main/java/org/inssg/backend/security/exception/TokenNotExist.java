package org.inssg.backend.security.exception;

import org.inssg.backend.exception.BusinessLogicException;
import org.inssg.backend.exception.ExceptionCode;

public class TokenNotExist extends BusinessLogicException {

    public TokenNotExist() {
        super(ExceptionCode.TOKEN_NOT_EXIST);
    }
}
