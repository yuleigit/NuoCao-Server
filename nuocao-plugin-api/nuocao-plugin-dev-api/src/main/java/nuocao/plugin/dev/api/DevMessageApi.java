package nuocao.plugin.dev.api;

import com.nuocao.common.page.PageResult;
import nuocao.plugin.dev.api.dto.message.DevMessagePageParamDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageResultDTO;
import nuocao.plugin.dev.api.dto.message.DevMessageSendParamDTO;

import java.util.List;

/**
 * 站内信API接口
 **/
public interface DevMessageApi {

    /**
     * 发送站内信，默认：分类系统通知
     *
     **/
    void sendMessage(DevMessageSendParamDTO paramDTO);


    /**
     * 获取未读站内信列表
     */
    List<DevMessageResultDTO> list(Long receiverIdList, Integer limit);
    /**
     * 获取未读站内信列表
     */
    List<DevMessageResultDTO> unreadList(Long receiverIdList, Integer limit);
    /**
     * 获取未读站内信数量
     */
    Long unreadCount(Long userId);

    Long countByUserId(Long userId);

    /**
     * 获取站内信分页
     */
    PageResult<DevMessageResultDTO> page(DevMessagePageParamDTO pageParam);

    /**
     * 获取站内信详情
     *
     * @param id 站内信id
     */
    DevMessageResultDTO detail(Long id);

    /**
     * 站内信全部标记已读
     */
    void allMessageMarkRead(Long userId);




}
