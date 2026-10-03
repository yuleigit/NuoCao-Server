package nuocao.plugin.dev.api.dto.message;


import com.nuocao.common.page.SortPageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class DevMessageQueryParamDTO extends SortPageParam {

    @ApiModelProperty(value = "目录",required = true)
    private String category;

    @ApiModelProperty(value = "接收用户ID",required = true)
    private Long receiverUserId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("发送人用户ID")
    private Long senderUserId;

    @ApiModelProperty("读取状态")
    private Integer read;
}
