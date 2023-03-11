package org.inssg.backend.item;

import org.inssg.backend.exception.BusinessLogicException;
import org.inssg.backend.exception.ExceptionCode;

public class ItemNotFound extends BusinessLogicException {

    public ItemNotFound() {
        super(ExceptionCode.ITEM_NOT_FOUND);
    }
}
