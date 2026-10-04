package com.nuocao.common.sse;

import com.nuocao.common.utils.ConvertUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * 通用SSE参数
 */
@Getter
@Setter
public class CommonSseParam {

    private String clientId;

    /**
     * 关联通用忽的ID，统一使用string存储方便扩展
     * 请使用是根据模块情况自行进行转换
     */
    private String userId;


    public Long getUserIdToLong(){
        if(userId==null) return null;
        return ConvertUtils.toLong(userId);
    }
}
