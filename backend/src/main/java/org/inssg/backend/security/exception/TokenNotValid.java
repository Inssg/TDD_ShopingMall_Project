package org.inssg.backend.security.exception;

import org.inssg.backend.exception.BusinessLogicException;
import org.inssg.backend.exception.ExceptionCode;

public class TokenNotValid extends BusinessLogicException {
    public TokenNotValid() {
        super(ExceptionCode.TOKEN_NOT_VALID);
    }
}
