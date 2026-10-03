package nuocao.plugin.dev.api;

import cn.hutool.json.JSONObject;
import com.nuocao.common.page.PageResult;
import nuocao.plugin.dev.api.dto.message.DevMessageQueryParamDTO;
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
    List<DevMessageResultDTO> list(List<Long> receiverIdList, Integer limit);

    /**
     * 获取未读站内信数量
     */
    Long unreadCount(Long userId);

    /**
     * 获取站内信分页
     */
    PageResult<DevMessageResultDTO> page(DevMessageQueryParamDTO pageParam);

    /**
     * 获取站内信详情
     *
     * @param id 站内信id
     */
    DevMessageResultDTO detail(Long id);

    /**
     * 站内信全部标记已读
     */
    void allMessageMarkRead();




}
