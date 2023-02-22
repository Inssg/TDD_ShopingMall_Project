package org.inssg.backend.security;

import org.inssg.backend.error.BusinessLogicException;
import org.inssg.backend.error.ExceptionCode;

public class TokenNotExist extends BusinessLogicException {

    public TokenNotExist() {
        super(ExceptionCode.TOKEN_NOT_EXIST);
    }
}
