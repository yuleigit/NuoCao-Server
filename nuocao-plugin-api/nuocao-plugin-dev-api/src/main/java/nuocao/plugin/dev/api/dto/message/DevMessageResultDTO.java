package nuocao.plugin.dev.api.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 消息返回结果DTO
 */
@Getter
@Setter
public class DevMessageResultDTO {

    @ApiModelProperty("消息ID")
    private Long id;
    @ApiModelProperty("目录")
    private String category;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("所属租户")
    @JsonIgnore
    private Integer tenantId;

    @ApiModelProperty("发送人用户ID")
    private Long senderUserId;

    @ApiModelProperty("发送人信息")
    private DevMessageUserResultDTO sender;

    @ApiModelProperty("读取状态")
    private Integer read;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间", position = 7)
    private LocalDateTime createTime;

}
