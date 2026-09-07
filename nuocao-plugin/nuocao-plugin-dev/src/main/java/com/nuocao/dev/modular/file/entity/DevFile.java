package com.nuocao.dev.modular.file.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.nuocao.common.pojo.CommonEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 文件实体
 **/
@Getter
@Setter
@Table(value = "dev_file")
public class DevFile extends CommonEntity {

    /** id，系统生成的文件唯一ID */
    @ApiModelProperty(value = "id", position = 1)
    @Id
    private Long id;
    /** 文件ID用于数据处理 */
    @ApiModelProperty(value = "fileId", position = 1)
    private String fileId;

    /** 存储引擎 */
    @ApiModelProperty(value = "存储引擎", position = 2)
    private String engine;

    /** 存储桶 */
    @ApiModelProperty(value = "存储桶", position = 3)
    private String bucket;

    /** 文件名称 */
    @ApiModelProperty(value = "文件名称", position = 4)
    private String name;

    /** 文件后缀 */
    @ApiModelProperty(value = "文件后缀", position = 5)
    private String suffix;

    /** 文件大小kb */
    @ApiModelProperty(value = "文件大小kb", position = 6)
    private Long sizeB;

    /** 文件大小（格式化后） */
    @ApiModelProperty(value = "文件大小（格式化后）", position = 7)
    private String sizeInfo;

    /** 文件的对象名（唯一名称） */
    @ApiModelProperty(value = "文件的对象名（唯一名称）", position = 8)
    private String objName;

    /** 文件存储路径 */
    @ApiModelProperty(value = "文件存储路径", position = 9)
    private String storagePath;

    /** 文件下载路径 */
    @ApiModelProperty(value = "文件下载路径", position = 10)
    private String fileUrl;

    /** 图片缩略图 */
    @ApiModelProperty(value = "图片缩略图", position = 11)
    private String thumbnail;

    /** 图片所属租户 */
    @ApiModelProperty(value = "图片所属租户", position = 12)
    private Long tenantId;



}
