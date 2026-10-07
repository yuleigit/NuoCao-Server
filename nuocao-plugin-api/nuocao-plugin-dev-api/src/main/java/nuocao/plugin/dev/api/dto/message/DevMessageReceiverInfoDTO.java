package nuocao.plugin.dev.api.dto.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import nuocao.plugin.dev.api.enums.message.DevMessageCategoryEnum;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DevMessageReceiverInfoDTO {
    /** 接收人ID */
    @ApiModelProperty(value = "接收人ID", position = 1)
    private Long receiverUserId;

    /** 接收人姓名 */
    @ApiModelProperty(value = "接收人", position = 2)
    private DevMessageUserResultDTO receiver;

    /** 是否已读 */
    @ApiModelProperty(value = "是否已读", position = 3)
    private Boolean read;

    /** 是否已读 */
    @ApiModelProperty(value = "读取时间", position = 4)
    private LocalDateTime readTime;
}
