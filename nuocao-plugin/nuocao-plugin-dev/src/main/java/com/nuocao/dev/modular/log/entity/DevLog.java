package com.nuocao.dev.modular.log.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 日志实体
 */
@Getter
@Setter
@Table(value = "dev_log")
public class DevLog {

    /** id */
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;

    /** 日志分类
     * {@link com.nuocao.dev.modular.log.enums.DevLogCategoryEnum}
     * */
    @ApiModelProperty(value = "日志分类", position = 2)
    private String category;

    /** 日志标题 */
    @ApiModelProperty(value = "日志标题", position = 3)
    private String title;
    /**
     * 日志模块
     */
    @ApiModelProperty(value = "日志模块",notes = "比如sys，biz等", position = 3)
    private String modular;
    /**
     * 日志业务类型
     */
    @ApiModelProperty(value = "日志操作类型",notes = "比如delete等", position = 3)
    private String operationType;
    /**
     * 请求方法
     */
    @ApiModelProperty(value = "请求方法", position = 3)
    private String reqMethod;

    /**
     * 请求URL
     */
    @ApiModelProperty(value = "请求URL", position = 3)
    private String reqUrl;

    /**
     * 请求参数集合字符串
     */
    @ApiModelProperty(value = "请求参数集合字符串", position = 3)
    private String reqParams;

    /**
     * 操作人类型
     * {@link  com.nuocao.dev.modular.log.enums.DevLogOperatorTypeEnum}
     */
    @ApiModelProperty(value = "操作人类型",notes = "方便后期扩展，admin,member等，目前只有admin", position = 3)
    private String operatorType;

    /**
     * 操作人 浏览器
     */
    @ApiModelProperty(value = "操作人 浏览器", position = 3)
    private String operatorBrowser;

    /** 操作人IP地址 */
    @ApiModelProperty(value = "操作人IP地址", position = 17)
    private String operatorIp;

    /** 操作人地址 */
    @ApiModelProperty(value = "操作人地址", position = 17)
    private String operatorAddress;
    /**
     * 操作人 操作系统
     */
    @ApiModelProperty(value = "操作人 操作系统", position = 3)
    private String operatorOs;


    /**
     * java类名
     */
    @ApiModelProperty(value = "java类名", position = 3)
    private String className;

    /**
     * java 方法名
     */
    @ApiModelProperty(value = "java方法名", position = 3)
    private String funName;

    /**
     * java 方法名参数
     */
    @ApiModelProperty(value = "java方参数", position = 3)
    private String funArgs;
    /**
     * 开始执行时间
     */
    @ApiModelProperty(value = "开始执行时间", position = 3)
    private Long beginTime;
    /**
     * 结束执行时间
     */
    @ApiModelProperty(value = "开始执行时间", position = 3)
    private Long endTime;
    /**
     * 执行时间（毫秒）
     */
    @ApiModelProperty(value = "执行时间（毫秒）", position = 3)
    private Integer duration;


    /** 执行状态
     * {@link com.nuocao.dev.modular.log.enums.DevLogExeStatusEnum}
     * */
    @ApiModelProperty(value = "执行状态", position = 4)
    private String exeStatus;

    /** 具体消息 */
    @ApiModelProperty(value = "具体消息", position = 5)
    private String exeMessage;

    /** 结果数据 */
    @ApiModelProperty(value = "结果数据", position = 15)
    private String resultData;

    /** 操作人用户ID */
    @ApiModelProperty(value = "操作人用户ID", position = 17)
    private Long operatorUserId;

    /** 操作人用户ID */
    @ApiModelProperty(value = "操作人用户ID", position = 17)
    private String operatorUserName;

    /** 创建时间 */
    @ApiModelProperty(value = "创建时间", position = 19)
    private Date createTime;

    /** 创建人 */
    @ApiModelProperty(value = "创建人", position = 20)
    private Long createUser;

    @ApiModelProperty(value ="租户ID")
    @Column(tenantId = true)
    private Long tenantId;

    @ApiModelProperty(value ="日志签名",notes = "防篡改")
    private String signData;

}
