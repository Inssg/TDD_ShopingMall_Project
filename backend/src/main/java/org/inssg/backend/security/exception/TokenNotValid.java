package org.inssg.backend.security.exception;

import org.inssg.backend.error.BusinessLogicException;
import org.inssg.backend.error.ExceptionCode;

public class TokenNotValid extends BusinessLogicException {
    public TokenNotValid() {
        super(ExceptionCode.TOKEN_NOT_VALID);
    }
}
