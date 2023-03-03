package org.inssg.backend.member;

import org.inssg.backend.error.BusinessLogicException;
import org.inssg.backend.error.ExceptionCode;

public class MemberNotFound extends BusinessLogicException {

    public MemberNotFound() {
        super(ExceptionCode.MEMBER_NOT_FOUND);
    }
}
