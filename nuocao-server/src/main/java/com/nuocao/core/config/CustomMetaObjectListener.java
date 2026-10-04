package com.nuocao.core.config;

import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import com.nuocao.common.enums.CommonDeleteFlagEnum;
import com.nuocao.common.pojo.CommonEntity;
import com.nuocao.common.utils.LocalDateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Supplier;

/**
 * 用于监听 Entity 实体类数据数据新增更新时间，注入默认数据
 */
@Slf4j
public class CustomMetaObjectListener implements UpdateListener, InsertListener {

    /**
     * 自定义新增注入
     */
    @Override
    public void onInsert(Object entity) {
        try {
            if (ObjectUtil.isNotNull(entity) && entity instanceof CommonEntity) {
                CommonEntity commonEntity = (CommonEntity) entity;
                LocalDateTime current = ObjectUtil.isNotNull(commonEntity.getCreateTime())
                        ? commonEntity.getCreateTime() : LocalDateTimeUtils.now();
                commonEntity.setCreateTime(current);
                // 当前已登录 且 创建人为空 则填充
                if (ObjectUtil.isNull(commonEntity.getCreateUser())) {
                    commonEntity.setCreateUser(this.getUserId());
                }
                commonEntity.setDeleteFlag(CommonDeleteFlagEnum.NOT_DELETE.getValue());
            }
        } catch (Exception e) {
            printException("新增注入失败", e);
        }
    }

    /**
     * 自定义修改注入
     */
    @Override
    public void onUpdate(Object entity) {
        try {
            if (ObjectUtil.isNotNull(entity) && entity instanceof CommonEntity) {
                CommonEntity commonEntity = (CommonEntity) entity;
                LocalDateTime current = ObjectUtil.isNotNull(commonEntity.getUpdateTime())
                        ? commonEntity.getUpdateTime() : LocalDateTimeUtils.now();
                commonEntity.setUpdateTime(current);

                // 当前已登录 且 更新人为空 则填充
                if (ObjectUtil.isNull(commonEntity.getUpdateUser())) {
                    commonEntity.setUpdateUser(this.getUserId());
                }
            }
        } catch (Exception e) {
            printException("修改注入失败", e);
        }
    }

    /**
     * 打印异常信息
     */
    private void printException(String msg, Exception e) {
        log.error("{} => 自动注入异常 => {}", msg, e.getMessage());
    }

    /**
     * 获取用户id
     */
    private String getUserId() {
        Supplier<String> getter = Solon.context().getBean("user_id_bean");
        try {
            if (ObjectUtil.isNotEmpty(getter.get())) {
                return getter.get();
            } else {
                return "-1";
            }
        } catch (Exception e) {
            return "-1";
        }
    }
}
