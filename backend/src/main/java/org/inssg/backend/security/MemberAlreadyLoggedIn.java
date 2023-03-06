package org.inssg.backend.security;

import org.inssg.backend.error.BusinessLogicException;
import org.inssg.backend.error.ExceptionCode;

public class MemberAlreadyLoggedIn extends BusinessLogicException {
    public MemberAlreadyLoggedIn() {
        super(ExceptionCode.MEMBER_ALREADY_LOGGED_IN);
    }
}
