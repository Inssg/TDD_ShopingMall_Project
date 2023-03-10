package org.inssg.backend.item;

import org.inssg.backend.error.BusinessLogicException;
import org.inssg.backend.error.ExceptionCode;

public class ItemNotFound extends BusinessLogicException {

    public ItemNotFound() {
        super(ExceptionCode.ITEM_NOT_FOUND);
    }
}
