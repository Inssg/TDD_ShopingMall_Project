package org.inssg.backend.member;

import org.inssg.backend.exception.BusinessLogicException;
import org.inssg.backend.exception.ExceptionCode;

public class MemberNotFound extends BusinessLogicException {

    public MemberNotFound() {
        super(ExceptionCode.MEMBER_NOT_FOUND);
    }
}
