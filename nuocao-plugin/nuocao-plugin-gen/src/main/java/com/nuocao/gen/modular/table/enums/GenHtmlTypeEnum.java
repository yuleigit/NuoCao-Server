package com.nuocao.gen.modular.table.enums;

import com.nuocao.common.enums.StrArrayValuable;
import lombok.Getter;

import java.util.Arrays;


/**
 * html组件类型
 */
@Getter
public enum GenHtmlTypeEnum implements StrArrayValuable {

    INPUT("input","单行文本"),
    TEXTAREA("textarea","多行文本"),
    SELECT("select","下拉框"),
    RADIO("radio","单选框"),
    CHECKBOX("checkbox","复选框"),
    DATE_PICKER("date_picker","日期选择"),
    TIME_PICKER("time_picker","时间选择器"),
    INPUT_NUMBER("input_number","数字输入框"),
    SLIDER("slider","滑动数字条"),
    SWITCH("switch","开关"),
    UPLOAD_IMAGE("upload_image","图片上传"),
    UPLOAD_FILE("upload_file","文件上传"),
    EDITOR("editor","富文本编辑器");

    public static final String[] ARRAYS = Arrays.stream(values())
            .map(GenHtmlTypeEnum::getValue)
            .toArray(String[]::new);

    private final String value;

    private final String name;
    GenHtmlTypeEnum(String value,String name) {
        this.value = value;
        this.name=name;
    }

    @Override
    public String[] array() {
        return ARRAYS;

    }
}
