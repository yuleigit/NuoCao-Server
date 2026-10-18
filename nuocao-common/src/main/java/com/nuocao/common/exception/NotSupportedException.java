package com.nuocao.common.exception;

import com.nuocao.common.language.LanguageUtils;

public class NotSupportedException extends CommonException {
    public NotSupportedException() {
        super(LanguageUtils.getMessage("common.notSupported"));
        super.setCode(530555);
        super.setMsg(super.getMessage());
    }
}
