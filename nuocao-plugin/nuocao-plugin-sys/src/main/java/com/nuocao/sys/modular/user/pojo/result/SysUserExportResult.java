package com.nuocao.sys.modular.user.pojo.result;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户导出结果集
 **/
@Getter
@Setter
public class SysUserExportResult {


    /** 头像 */
    @ColumnWidth(8)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "头像"})
    private String avatar;

    /** 账号 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "账号"})
    private String username;

    /** 姓名 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "姓名"})
    private String name;

    /** 昵称 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "昵称"})
    private String nickname;

    /** 性别 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "性别"})
    private String gender;

    /** 年龄 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "年龄"})
    private String age;

    /** 出生日期 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "出生日期"})
    private String birthday;

    /** 手机 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "手机"})
    private String phone;

    /** 邮箱 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 29)
    @ExcelProperty({"用户信息", "基本信息", "邮箱"})
    private String email;


    /** 组织名称 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"用户信息", "员工信息", "组织名称"})
    private String orgName;
    /** 组织名称 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"用户信息", "员工信息", "用户角色"})
    private String roleName;

    /** 职位名称 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 34)
    @ExcelProperty({"用户信息", "员工信息", "职位名称"})
    private String positionName;


    /** 上次登录ip */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"用户信息", "系统信息", "上次登录ip"})
    private String lastLoginIp;

    /** 上次登录地点 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"用户信息", "系统信息", "上次登录地点"})
    private String lastLoginAddress;

    /** 上次登录时间 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty({"用户信息", "系统信息", "上次登录时间"})
    private Date lastLoginTime;

    /** 上次登录设备 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"用户信息", "系统信息", "上次登录设备"})
    private String lastLoginDevice;

    /** 最新登录ip */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"用户信息", "系统信息", "最新登录ip"})
    private String latestLoginIp;

    /** 最新登录地点 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"用户信息", "系统信息", "最新登录地点"})
    private String latestLoginAddress;

    /** 最新登录时间 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty({"用户信息", "系统信息", "最新登录时间"})
    private Date latestLoginTime;

    /** 最新登录设备 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"用户信息", "系统信息", "最新登录设备"})
    private String latestLoginDevice;

    /** 用户状态 */
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 42)
    @ExcelProperty({"用户信息", "系统信息", "用户状态"})
    private String status;
}
