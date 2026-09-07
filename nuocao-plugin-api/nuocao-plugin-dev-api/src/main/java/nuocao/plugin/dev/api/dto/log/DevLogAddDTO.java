package nuocao.plugin.dev.api.dto.log;

import com.nuocao.common.utils.LocalDateTimeUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 日志添加 dto
 *
 */
@Getter
@Setter
@Builder
public class DevLogAddDTO {

    /**
     * 用户类型
     */
    private String userType;


    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户账号，用户名
     */
    private String userName;

    /**
     * 请求方法名
     */
    private String reqMethod;

    /**
     * 请求URL
     */
    private String reqUrl;

    /**
     * 请求参数
     */
    private String reqParams;

    /**
     * 开始请求时间
     */
    private LocalDateTime beginTime = LocalDateTimeUtils.now();
    /**
     * 结束请求时间
     */
    private LocalDateTime endTime = LocalDateTimeUtils.now();
    /**
     * 执行时长，单位：毫秒
     */
    private Integer duration=0;

}
