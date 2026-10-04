package com.nuocao.sys.modular.user.pojo.param;

import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotEmpty;

import java.util.List;

@Data
public class SysUserMessageSendParam  {
    /** 主题 */
    @ApiModelProperty(value = "主题", required = true, position = 1)
    @NotBlank(message = "title不能为空")
    private String title;

    /** 接收人id集合 */
    @ApiModelProperty(value = "接收人id集合", required = true, position = 2)
    @NotEmpty(message = "receiverIdList不能为空")
    private List<Long> receiverIdList;

    /** 正文 */
    @ApiModelProperty(value = "正文", position = 3)
    private String content;

}
