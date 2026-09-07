package com.nuocao.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 接收key value对应关系的通用实例
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueData {
    String key;

    Object value;
}
