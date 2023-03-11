package org.inssg.backend.security.exception;

import org.inssg.backend.exception.BusinessLogicException;
import org.inssg.backend.exception.ExceptionCode;

public class MemberAlreadyLoggedIn extends BusinessLogicException {
    public MemberAlreadyLoggedIn() {
        super(ExceptionCode.MEMBER_ALREADY_LOGGED_IN);
    }
}
