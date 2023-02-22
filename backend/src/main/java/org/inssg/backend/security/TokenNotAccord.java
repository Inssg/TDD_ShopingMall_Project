package org.inssg.backend.security;

import org.inssg.backend.error.BusinessLogicException;
import org.inssg.backend.error.ExceptionCode;

public class TokenNotAccord extends BusinessLogicException {
    public TokenNotAccord() {
        super(ExceptionCode.TOKEN_NOT_ACCORD);
    }
}
