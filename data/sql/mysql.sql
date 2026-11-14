/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50737
 Source Host           : 127.0.0.1:3306
 Source Schema         : nuocao

 Target Server Type    : MySQL
 Target Server Version : 50737
 File Encoding         : 65001

 Date: 06/11/2026 13:19:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dev_config
-- ----------------------------
DROP TABLE IF EXISTS `dev_config`;
CREATE TABLE `dev_config`  (
  `id` bigint(20) NOT NULL COMMENT '配置ID',
  `config_group` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务功能分组;可为空，如录入了根据实际情况配置，比如短信配置类的，都属于sms组',
  `config_key` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置KEY;key不可重复一般以sms_aliyun_*为前缀',
  `config_value` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '配置的值;可以是文本，可以使对象json，这个使用时自己确认',
  `config_category` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目录，配置对象;比如sms_aliyun代表阿里云通道的配置',
  `remark` varchar(900) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `sort_code` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dev_config
-- ----------------------------
INSERT INTO `dev_config` VALUES (1, 'system', 'sys_title', '糯草管理系统', 'sys_config', NULL, 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (2, 'system', 'sys_tel', '15530699611', 'sys_config', NULL, 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (3, 'system', 'sys_email', 'yulei6@gmail.com', 'sys_config', NULL, 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (4, 'other', 'test', '测试', 'other', NULL, 0, 0, NULL, NULL, '1', '2026-09-18 10:08:09');
INSERT INTO `dev_config` VALUES (5, 'system', 'file_local_folder', '/home/nuocao/upload', 'file_lcoal', NULL, 0, 0, NULL, NULL, '1', '2026-09-18 20:17:50');
INSERT INTO `dev_config` VALUES (6, 'system', 'sys_default_file_engine', 'local', 'sys_config', NULL, 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (7, 'system', 'sys_default_sms_channel_code', 'aliyun', 'sys_config', NULL, 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (8, 'system', 'auth_login_captcha_open', 'true', 'sys_config', '登录是否开启验验证码', 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (9, 'system', 'auth_login_pwd_error_count_limit', '4', 'sys_config', '密码错误次数', 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (10, 'system', 'auth_login_pwd_error_count_duration', '33', 'sys_config', '密码错误累计时间间隔配置key，取值为分钟', 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (11, 'system', 'auth_login_pwd_error_disable_time', '30', 'sys_config', '密码错误次数到达上限后的间隔时间时间时间（分钟）', 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (12, 'file', 'file_aliyun_access_key_id', '', 'file_aliyun', '阿里云key', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (13, 'file', 'file_aliyun_access_key_secret', '', 'file_aliyun', '阿里云secret', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (14, 'file', 'file_aliyun_end_point', '', 'file_aliyun', '阿里云存储节点', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (15, 'file', 'file_aliyun_bucket_name', '', 'file_aliyun', '阿里云默认BacketName', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (16, 'file', 'file_aliyun_bind_domain_url', '', 'file_aliyun', '阿里云映射域名', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (17, 'file', 'file_tencent_secret_id', '', 'file_tencent', '腾讯云secret_id', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (18, 'file', 'file_tencent_secret_key', '', 'file_tencent', '腾讯云secret_key', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (19, 'file', 'file_tencent_region_id', '', 'file_tencent', '腾讯云存储节点ID', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (20, 'file', 'file_tencent_bucket_name', '', 'file_tencent', '腾讯云默认BucketName', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (21, 'file', 'file_tencent_bind_domain_url', '', 'file_tencent', '腾讯云绑定域名', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (22, 'file', 'file_minio_access_key', ' ', 'file_minio', 'minio key', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (23, 'file', 'file_minio_secret', '', 'file_minio', 'minio secret', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (24, 'file', 'file_minio_end_point', '', 'file_minio', 'minio 存储节点', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (25, 'file', 'file_minio_bucket_name', '', 'file_minio', 'minio 默认BucketName', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (26, 'file', 'file_minio_bind_domain_url', '', 'file_minio', 'minio 绑定域名', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (27, 'sms', 'sms_aliyun_access_key_id', '****', 'sms_aliyun', '阿里云短信key', 0, 0, NULL, NULL, '1', '2026-10-16 21:07:56');
INSERT INTO `dev_config` VALUES (28, 'sms', 'sms_aliyun_access_key_secret', '****', 'sms_aliyun', '阿里云短信secret', 0, 0, NULL, NULL, '1', '2026-10-16 21:07:56');
INSERT INTO `dev_config` VALUES (29, 'sms', 'sms_aliyujn_default_sign_name', '糯草', 'sms_aliyun', '阿里云短信签名', 0, 0, NULL, NULL, '1', '2026-10-16 21:07:56');
INSERT INTO `dev_config` VALUES (30, 'sms', 'sms_tencent_secret_id', '', 'sms_tencent', '腾讯云短信secret id', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (31, 'sms', 'sms_tencent_secret_key', '', 'sms_tencent', '腾讯云短信i secret key', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (32, 'sms', 'sms_tencent_default_sign_name', '', 'sms_tencent', '腾讯云短信签名', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (33, 'sms', 'sms_tencent_default_sdk_app_id', ' ', 'sms_tencent', '腾讯云默认appid', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_config` VALUES (34, 'sms', 'sms_scene_account_reg_code', '466778687204466688', 'sms_scene', '账户注册验证码', 0, 0, NULL, NULL, '1', '2026-10-12 20:53:34');
INSERT INTO `dev_config` VALUES (35, 'sms', 'sms_scene_account_login_code', '466778687204466688', 'sms_scene', '账户登录验证码', 0, 0, NULL, NULL, '1', '2026-10-12 20:53:34');
INSERT INTO `dev_config` VALUES (36, 'sms', 'sms_scene_password_find_code', '466778687204466688', 'sms_scene', '找回密码验证码', 0, 0, NULL, NULL, '1', '2026-10-12 20:53:34');
INSERT INTO `dev_config` VALUES (37, 'sms', 'sms_scene_password_update_code', '466778687204466688', 'sms_scene', '修改密码验证码', 0, 0, NULL, NULL, '1', '2026-10-12 20:53:34');
INSERT INTO `dev_config` VALUES (38, 'sms', 'sms_scene_bind_update_code', '466778687204466688', 'sms_scene', '更改绑定手机验证码', 0, 0, NULL, NULL, '1', '2026-10-12 20:53:34');
INSERT INTO `dev_config` VALUES (39, 'system', 'account_register_enable', 'true', 'sys_config', '开启账号注册', 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (40, 'system', 'account_register_sms_code_enable', 'true', 'sys_config', '开启注册开启短信验证码', 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (41, 'system', 'account_login_methods', 'password', 'sys_config', '开启登录方式', 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (42, 'system', 'account_find_password_enable', 'true', 'sys_config', '密码找回开启', 0, 0, NULL, NULL, '1', '2026-10-17 09:50:04');
INSERT INTO `dev_config` VALUES (463461539912581120, 'other', 'tt', '123123', 'other', '12312', 0, 0, '1', '2026-10-02 10:51:42', NULL, NULL);
INSERT INTO `dev_config` VALUES (463461539912581121, 'system', 'account_login_2fa_verify', '0', 'sys_config', '登录2FA强制级别：0=关闭，1=可选，2=强制', 0, 0, NULL, NULL, '1', '2026-11-14 10:00:00');

-- ----------------------------
-- Table structure for dev_dict
-- ----------------------------
DROP TABLE IF EXISTS `dev_dict`;
CREATE TABLE `dev_dict`  (
  `id` bigint(20) NOT NULL COMMENT '字典ID',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '上级ID',
  `i18n_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多语言key',
  `dict_label` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字典名',
  `dict_value` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字典值',
  `category` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目录;sys:系统，biz=业务，自己定义',
  `sort_code` int(11) NOT NULL DEFAULT 0 COMMENT '排序吗',
  `value_type` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 's' COMMENT '数据类型',
  `color` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '颜色',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`, `sort_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统字典库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dev_dict
-- ----------------------------
INSERT INTO `dev_dict` VALUES (1, 0, NULL, '是否', 'common_yes_no', 'sys', 0, 'n', '', 0, NULL, NULL, '1', '2026-04-11 23:23:23');
INSERT INTO `dev_dict` VALUES (2, 1, NULL, '是', '1', 'sys', 0, 'n', '#009900', 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES (3, 1, NULL, '否', '0', 'sys', 0, 'n', '#999999', 0, NULL, NULL, NULL, NULL);
INSERT INTO `dev_dict` VALUES (400584698571132928, 0, NULL, '通用可用状态', 'common_enable_status', 'sys', 0, 'n', '', 0, '1', '2026-04-11 22:41:35', '1', '2026-10-17 20:29:45');
INSERT INTO `dev_dict` VALUES (400584859372359680, 400584698571132928, NULL, '可用', '1', 'sys', 1, 's', '#6bd311', 0, '1', '2026-04-11 22:42:13', '1', '2026-04-11 23:00:00');
INSERT INTO `dev_dict` VALUES (400588760679133184, 400584698571132928, NULL, '禁用', '0', 'sys', 2, 's', '#d3290f', 0, '1', '2026-04-11 22:57:43', '1', '2026-04-11 23:00:29');
INSERT INTO `dev_dict` VALUES (400594080369422336, 400584698571132928, NULL, '测试', '121', 'sys', 0, 's', '#6a4444', 1, '1', '2026-04-11 23:18:51', NULL, NULL);
INSERT INTO `dev_dict` VALUES (404534049400119296, 0, NULL, '性别', 'sys_gender', 'sys', 0, 'n', '', 0, '1', '2026-04-22 20:14:53', '1', '2026-04-22 20:20:59');
INSERT INTO `dev_dict` VALUES (404534404250820608, 404534049400119296, NULL, '男', '1', 'sys', 0, 's', '#0b48e3', 0, '1', '2026-04-22 20:16:18', NULL, NULL);
INSERT INTO `dev_dict` VALUES (404534464833347584, 404534049400119296, NULL, '女', '2', 'sys', 0, 's', '#e60edb', 0, '1', '2026-04-22 20:16:32', NULL, NULL);
INSERT INTO `dev_dict` VALUES (455287285228417024, 0, NULL, '数据权限类型', 'data_scope_type', 'sys', 0, 's', '', 0, '1', '2026-09-09 21:30:08', NULL, NULL);
INSERT INTO `dev_dict` VALUES (455287395253399552, 455287285228417024, NULL, '全部数据', 'all', 'sys', 0, 's', '#0838c9', 0, '1', '2026-09-09 21:30:34', '1', '2026-09-09 21:32:55');
INSERT INTO `dev_dict` VALUES (455288144003772416, 455287285228417024, NULL, '自定义数据', 'custom', 'sys', 0, 's', '#5908aa', 0, '1', '2026-09-09 21:33:32', NULL, NULL);
INSERT INTO `dev_dict` VALUES (455288281245593600, 455287285228417024, NULL, '本部门数据', 'only', 'sys', 0, 's', '#ea7e0b', 0, '1', '2026-09-09 21:34:05', NULL, NULL);
INSERT INTO `dev_dict` VALUES (455288358143963136, 455287285228417024, NULL, '本部门级子部门数据', 'child', 'sys', 0, 's', '#9c2811', 0, '1', '2026-09-09 21:34:23', NULL, NULL);
INSERT INTO `dev_dict` VALUES (455288463844618240, 455287285228417024, NULL, '仅本人自己数据', 'self', 'sys', 0, 's', '#045b2e', 0, '1', '2026-09-09 21:34:49', NULL, NULL);

-- ----------------------------
-- Table structure for dev_file
-- ----------------------------
DROP TABLE IF EXISTS `dev_file`;
CREATE TABLE `dev_file`  (
  `id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件ID;为了方便兼容使用字符串存储',
  `file_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件ID',
  `engine` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储引擎',
  `bucket` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储桶',
  `file_key` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件key',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名;上传的文件名',
  `suffix` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件后缀名',
  `size_b` bigint(20) NOT NULL COMMENT '文件大小;单位b',
  `size_info` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件大小格式化后信息;*kb*m*g等',
  `obj_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件存储生成的唯一文件名',
  `storage_path` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '存储位置',
  `file_url` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件URL',
  `thumbnail` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '缩略图;缩小后尺寸为80*80，仅限图片文件，base64',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统文件日志' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for dev_job
-- ----------------------------
DROP TABLE IF EXISTS `dev_job`;
CREATE TABLE `dev_job`  (
  `id` bigint(20) NOT NULL COMMENT '任务ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务名',
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务编号',
  `action_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务class',
  `cron` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'cron表达式',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `params_json` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '参数json',
  `delete_flag` int(11) NULL DEFAULT NULL COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '定时任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dev_job
-- ----------------------------

-- ----------------------------
-- Table structure for dev_log
-- ----------------------------
DROP TABLE IF EXISTS `dev_log`;
CREATE TABLE `dev_log`  (
  `id` bigint(20) NOT NULL COMMENT '日志ID',
  `category` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '日志类型',
  `modular` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模块',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作标题',
  `operation_type` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作类型;CommonOperationTypeEnum',
  `req_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求方法名',
  `req_url` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求地址',
  `req_params` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '请求参数;json',
  `operator_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人角色',
  `operator_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人IP',
  `operator_browser` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '擦做人浏览器UA',
  `operator_os` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人操作系统',
  `operator_address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人地址',
  `class_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'java类',
  `fun_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '方法名',
  `fun_args` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '方法参数',
  `begin_time` bigint(20) NULL DEFAULT NULL COMMENT '操作时间',
  `end_time` bigint(20) NULL DEFAULT NULL COMMENT '操作结束时间',
  `duration` int(11) NULL DEFAULT NULL COMMENT '执行时长',
  `exe_status` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行结果编码',
  `exe_message` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '执行失败的错误提示',
  `result_data` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '结果数据;json',
  `operator_user_id` bigint(20) NULL DEFAULT NULL COMMENT '操作人用户ID',
  `operator_user_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人名字',
  `sign_data` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '操作日志签名',
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dev_message
-- ----------------------------
DROP TABLE IF EXISTS `dev_message`;
CREATE TABLE `dev_message`  (
  `id` bigint(20) NOT NULL COMMENT '消息ID',
  `category` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '目录;sys',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息主题',
  `content` varchar(900) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '消息内容',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  `sender_user_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '发送用户ID，0=系统角色发送',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '站内信' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dev_message
-- ----------------------------
INSERT INTO `dev_message` VALUES (464331450020999168, 'msg', '测试', '测试', '0', 1, 0, '1', '2026-10-04 20:28:25', NULL, NULL);

-- ----------------------------
-- Table structure for dev_message_receiver
-- ----------------------------
DROP TABLE IF EXISTS `dev_message_receiver`;
CREATE TABLE `dev_message_receiver`  (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `message_id` bigint(20) NOT NULL COMMENT '消息ID;sys',
  `receiver_user_id` bigint(20) NOT NULL COMMENT '接收人用户ID',
  `read` tinyint(4) NOT NULL DEFAULT 0 COMMENT '消息已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '读取时间',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '站内信接收人' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dev_message_receiver
-- ----------------------------
INSERT INTO `dev_message_receiver` VALUES (464331450134245376, 464331450020999168, 1, 1, '2026-10-04 21:19:43', '0');
INSERT INTO `dev_message_receiver` VALUES (464331450138439680, 464331450020999168, 462496545230266368, 0, NULL, '0');

-- ----------------------------
-- Table structure for dev_sms_log
-- ----------------------------
DROP TABLE IF EXISTS `dev_sms_log`;
CREATE TABLE `dev_sms_log`  (
  `id` bigint(20) NOT NULL COMMENT '短信日志ID',
  `channel_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '通道编码',
  `template_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板编码',
  `type` tinyint(4) NOT NULL COMMENT '短信类型',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '手机号码',
  `sign_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '签名',
  `content` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息内容',
  `template_param` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板参数',
  `api_template_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '接口端模板ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '发送用户ID',
  `user_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户类型，默认为1;留用，后续用与扩展会员账号登',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `api_result_code` bigint(20) NULL DEFAULT NULL COMMENT 'api结果码，200成功',
  `api_result_msg` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'api返回消息',
  `api_request_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'api请求ID',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '短信发送记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dev_sms_template
-- ----------------------------
DROP TABLE IF EXISTS `dev_sms_template`;
CREATE TABLE `dev_sms_template`  (
  `id` bigint(20) NOT NULL COMMENT '短信模板ID',
  `type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '短信类型;1=验证码，2=通知类,3=营销类',
  `is_sys` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否系统内置;系统内置无法删除，不可改为非内置',
  `code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板码，内部调用使用',
  `content` varchar(4000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  `variables` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '短信内容参数;json字符串',
  `name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '短信模板名称',
  `channel_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '短信通道编码;使用哪个通道发送短信（这个通道单独配置到config中）',
  `api_template_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '通道端模板ID',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '是否可用',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `scene` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用场景，配合DevConfigSceneEnum联动dev_config',
  `sign_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '签名',
  `delete_flag` tinyint(4) NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` bigint(20) NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint(20) NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '短信模板;config中需配置默认通道编码，如未设置channel_code则默认使用对应通道，这个也是为了方便以后扩展短信发送方法，预留通道策略' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dev_sms_template
-- ----------------------------
INSERT INTO `dev_sms_template` VALUES (466778687204466688, 1, 1, 'sms_login_code', '您的验证码为：${code}，验证码${time}分钟内有效，请勿泄露！', '[{\"name\":\"code\",\"type\":2},{\"name\":\"time\",\"type\":1}]', '短信登录验证码', 'aliyun', 'SMS_512671091', 1, '短信登录模板', NULL, NULL, 0, 1, '2026-10-11 14:32:51', 1, '2026-10-13 14:28:03');

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `id` bigint(20) NOT NULL COMMENT '表ID',
  `table_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据表名',
  `pk_column_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据表主键名',
  `plugin_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '插件名;如nuocao-plugin-sys',
  `module_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模块名;如sys',
  `remove_table_prefix` tinyint(4) NOT NULL COMMENT '是否移除表前缀',
  `generate_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'zip' COMMENT '生成方式',
  `module_id` bigint(20) NOT NULL COMMENT '模块ID',
  `menu_parent_id` bigint(20) NOT NULL COMMENT '上级菜单ID',
  `function_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '功能名;如用户信息',
  `business_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '业务名;如user',
  `class_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类名',
  `package_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '包名;com.nuocao.sys.user',
  `author_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '作者名',
  `sort_code` int(11) NOT NULL DEFAULT 0 COMMENT '排序编码',
  `form_layout` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'vertical' COMMENT '表单布局',
  `is_grid` tinyint(4) NOT NULL DEFAULT 0 COMMENT '使用格栅布局',
  `delete_flag` tinyint(4) NOT NULL COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '生成代码的表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `id` bigint(20) NOT NULL COMMENT '数据表字段ID',
  `table_id` bigint(20) NOT NULL COMMENT '所属表格ID',
  `is_pk` tinyint(4) NOT NULL COMMENT '是否数据表的主键',
  `column_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段名',
  `column_remark` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段描述',
  `column_type` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段数据类型',
  `java_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段java变量名',
  `java_type` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段java数据类型',
  `html_type` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'html组件类型;input,select等组件',
  `dict_type` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字典类型',
  `is_list` tinyint(4) NOT NULL COMMENT '是否列表',
  `is_ignore` tinyint(4) NOT NULL COMMENT '是否列表忽略;作用是vue端columns不显示',
  `is_order` tinyint(4) NOT NULL COMMENT '是否可按此字段排序;作用是vue端columns不显示',
  `is_view` tinyint(4) NOT NULL COMMENT '是否详情',
  `is_add` tinyint(4) NOT NULL COMMENT '是否添加',
  `is_edit` tinyint(4) NOT NULL COMMENT '是否编辑',
  `is_required` tinyint(4) NOT NULL COMMENT '是否必填',
  `is_only` tinyint(4) NOT NULL COMMENT '是否唯一',
  `is_query` tinyint(4) NOT NULL COMMENT '是否查询',
  `query_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '查询类型',
  `value_size_min` decimal(24, 6) NULL DEFAULT NULL COMMENT '最小',
  `value_size_max` decimal(24, 6) NULL DEFAULT NULL COMMENT '最大',
  `valid_annotation` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '验证注解',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for sys_account
-- ----------------------------
DROP TABLE IF EXISTS `sys_account`;
CREATE TABLE `sys_account`  (
  `id` bigint(20) NOT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户账号',
  `password` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `nickname` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话号码',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '用户状态',
  `from_source` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '来源',
  `last_login_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上一次登录IP',
  `last_login_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上一次登录地址',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '上一次登录时间',
  `last_login_device` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上一次登录设备',
  `latest_login_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本次登录IP',
  `latest_login_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本次登录地址',
  `latest_login_time` datetime NULL DEFAULT NULL COMMENT '本次登录时间',
  `latest_login_device` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本次登录设备',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `inx_sys_user_username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统账号;仅用于登录鉴权使用' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_account
-- ----------------------------
INSERT INTO `sys_account` VALUES (1, 'superAdmin', '207cf410532f92a47dee245ce9b11ff71f578ebd763eb3bbea44ebd043d018fb', '糯草', '糯草', '15831699533', 'yulei6@gmail.com', 1, 'reg', '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-11-06 10:19:56', 'PC', '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-11-06 10:20:14', 'PC', 0, NULL, NULL, '1', '2026-11-06 10:20:14');
INSERT INTO `sys_account` VALUES (462437351131164672, 'test', '79d442f9b511f7fb6f1249eaee7c1d25715f41d12e3fc00adf6675f69e8578cc', NULL, 'test', '504443e05ca5c3f7f3246f33a936d2ae', 'saf@123.com', 1, 'sys_add', '127.0.0.1', '内网IP|内网IP', '2026-10-02 21:58:31', 'PC', '127.0.0.1', '内网IP|内网IP', '2026-10-02 21:59:23', 'PC', 0, '1', '2026-09-29 15:01:56', '463621835922841600', '2026-10-02 21:59:23');
INSERT INTO `sys_account` VALUES (468333779980357632, 'yulei', '627b86efdcc07ad37f9c2497712e3521b206f3a3147b9fca3195621d0661a99a', NULL, 'yulei', '1ba70d9c64c374354558d470842ea879', '', 1, 'user_reg', '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-10-17 10:49:22', 'PC', '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-10-17 10:50:38', 'PC', 0, '1', '2026-10-15 21:32:14', '468691845170999296', '2026-10-17 10:50:38');

-- ----------------------------
-- Table structure for sys_data_scope
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_scope`;
CREATE TABLE `sys_data_scope`  (
  `id` bigint(20) NOT NULL COMMENT '授权ID',
  `category` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限类型;目前只有ORG',
  `object_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '授权对象类型',
  `object_id` bigint(20) NOT NULL COMMENT '授权对象ID',
  `scope_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '授权形式',
  `ids` json NOT NULL COMMENT '授权数据ID集合',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据授权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_data_scope
-- ----------------------------
INSERT INTO `sys_data_scope` VALUES (455179686873083904, 'ORG', 'role', 403838071521120256, 'only', '[]', '1', 0, '1', '2026-09-09 14:22:34', '1', '2026-09-09 14:22:53');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint(20) NOT NULL COMMENT '菜单ID',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '上级菜单ID',
  `ancestors` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '祖级列表;以逗号分隔每个上级的id，id是唯一键，所以可以直接link',
  `top_id` bigint(20) NOT NULL COMMENT '顶级菜单ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单路由名称;英文，以_分隔',
  `path` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '路由路径',
  `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限编码;编码不能重复且只有菜单、按钮及才可用',
  `type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单类型',
  `component` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单标题',
  `i18n_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '多语言KEY;如果没有则以title显示，有的话就去找语言包',
  `sort_code` int(11) NULL DEFAULT NULL COMMENT '排序;排序编码',
  `icon` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `icon_type` tinyint(4) NULL DEFAULT 0 COMMENT '图片类型;0=iconify图标,1=本地图标',
  `href` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外链地址',
  `hide_in_menu` tinyint(4) NULL DEFAULT NULL COMMENT '是否在菜单中隐藏路由',
  `keep_alive` tinyint(4) NULL DEFAULT NULL COMMENT '路由是否缓存',
  `active_menu` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前路由对应的激活状态菜单',
  `constant` tinyint(4) NULL DEFAULT NULL COMMENT '是否常量;1是，0否',
  `multi_tab` tinyint(4) NULL DEFAULT NULL COMMENT '是否支持多页签Tab',
  `fixed_index_in_tab` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '固定在页签中的路由索引',
  `query` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单额外携带的参数',
  `enable` tinyint(4) NULL DEFAULT NULL COMMENT '是否启用',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统菜单、功能资源' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (378675820795498490, 0, '0', 0, 'sys', '/sys', '', 'dir', 'layout.base', '系统维护', 'route.sys', 3, 'system', 1, '', 0, 0, '', 0, 0, NULL, '[]', 1, 0, NULL, NULL, '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (378675820795498491, 378675820795498490, '0,378675820795498490', 0, 'sys_menu', '/sys/menu', '', 'menu', 'view.sys_menu_index', '菜单设置', 'route.sys_menu', 1000, 'list', 1, '', 0, 0, '', 0, 0, NULL, '[]', 1, 0, NULL, NULL, '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (378675820795498492, 0, '0', 0, 'home', '/home', '', 'menu', 'layout.base$view.home', '桌面', 'route.home', 0, 'dashboard', 1, '', 0, 1, '', 0, 0, '1', '[]', 1, 0, NULL, NULL, '1', '2026-04-11 22:32:15');
INSERT INTO `sys_menu` VALUES (378675820795498496, 0, '0', 0, 'danye', '/danye', '', 'menu', 'layout.base$view.home', '单页', 'route.danye', 0, 'cast', 1, NULL, 0, 0, '', 0, 0, '123', '[{\"key\":\"a\",\"value\":\"12\"},{\"key\":\"b\",\"value\":\"c\"}]', 1, 1, '1', '2026-02-10 11:43:31', '1', '2026-02-10 13:34:39');
INSERT INTO `sys_menu` VALUES (378698987572191232, 378675820795498490, '0,378675820795498490', 0, 'sys_user', '/sys/user', '', 'menu', 'view.sys_user_index', '用户管理', 'route.sys_user', 2, 'user', 1, '', 0, 1, '', 0, 1, NULL, '[]', 1, 0, '1', '2026-02-10 13:15:35', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (378735074210410496, 378675820795498491, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:menu:query', 'button', '', '菜单查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-10 15:38:58', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (378736886325923840, 378675820795498491, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:menu:add', 'button', '', '菜单添加', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-10 15:46:10', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (378736886384644096, 378675820795498491, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:menu:edit', 'button', '', '菜单编辑', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-10 15:46:10', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (378736886405615616, 378675820795498491, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:menu:delete', 'button', '', '菜单删除', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-10 15:46:10', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (378736886426587136, 378675820795498491, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:menu:batchDelete', 'button', '', '菜单批量删除', '', 1004, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-10 15:46:10', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (378737168699052032, 378675820795498492, '0,378675820795498492', 0, '', '', 'home:view', 'button', '', '首页查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-10 15:47:18', '1', '2026-04-11 22:32:15');
INSERT INTO `sys_menu` VALUES (378737168720023552, 378675820795498492, '0,378675820795498492', 0, '', '', 'home:view:statis', 'button', '', '首页统计查询', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-10 15:47:18', '1', '2026-04-11 22:32:15');
INSERT INTO `sys_menu` VALUES (378763572719341568, 378675820795498491, '0,378675820795498490,378675820795498491', 0, '123', '/123', '', 'menu', 'view.sys', '测试', 'route.123', 0, 'wind', 1, NULL, 0, 0, '', 0, 0, NULL, '[]', 1, 1, '1', '2026-02-10 17:32:13', NULL, NULL);
INSERT INTO `sys_menu` VALUES (378763754596945920, 0, '0', 0, 'dev', '/dev', '', 'dir', 'layout.base', '开发设置', 'route.dev', 0, 'example', 1, '', 0, 0, '', 0, 0, NULL, '[]', 1, 0, '1', '2026-02-10 17:32:56', '1', '2026-09-09 21:43:17');
INSERT INTO `sys_menu` VALUES (379177154451845120, 378675820795498490, '0,378675820795498490', 0, 'sys_org', '/sys/org', '', 'menu', 'view.sys_org_index', '组织结构', 'route.sys_org', 4, 'tree-table', 1, '', 0, 0, '', 0, 0, NULL, '[]', 1, 0, '1', '2026-02-11 20:55:38', '1', '2026-09-29 15:07:24');
INSERT INTO `sys_menu` VALUES (379177285523845120, 378675820795498490, '0,378675820795498490', 0, 'sys_role', '/sys/role', '', 'menu', 'view.sys_role_index', '角色管理', 'route.sys_role', 5, 'role', 1, '', 0, 0, '', 0, 0, NULL, '[]', 1, 0, '1', '2026-02-11 20:56:10', '1', '2026-09-29 15:07:31');
INSERT INTO `sys_menu` VALUES (379178044499931136, 379177285523845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:role:query', 'button', '', '角色查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 20:59:11', '1', '2026-09-29 15:07:31');
INSERT INTO `sys_menu` VALUES (379178044516708352, 379177285523845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:role:add', 'button', '', '角色创建', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 20:59:11', '1', '2026-09-29 15:07:31');
INSERT INTO `sys_menu` VALUES (379178044533485568, 379177285523845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:role:edit', 'button', '', '角儿编辑', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 20:59:11', '1', '2026-09-29 15:07:32');
INSERT INTO `sys_menu` VALUES (379178044546068480, 379177285523845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:role:delete', 'button', '', '角色删除', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 20:59:11', '1', '2026-09-29 15:07:32');
INSERT INTO `sys_menu` VALUES (379178044562845696, 379177285523845120, '0,378675820795498490,379177285523845120', 0, '', '', 'sys:role:batchDelete', 'button', '', '角色删除', '', 1004, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 1, '1', '2026-02-11 20:59:11', '1', '2026-04-20 20:43:33');
INSERT INTO `sys_menu` VALUES (379178044575428608, 379177285523845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:role:permission', 'button', '', '角色权限授权', '', 1004, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 20:59:11', '1', '2026-09-29 15:07:32');
INSERT INTO `sys_menu` VALUES (379178325316972544, 378698987572191232, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:user:query', 'button', '', '用户查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 21:00:18', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (379178325329555456, 378698987572191232, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:user:add', 'button', '', '用户添加', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 21:00:18', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (379178325342138368, 378698987572191232, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:user:edit', 'button', '', '用户编辑', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 21:00:18', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (379178325354721280, 378698987572191232, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:user:delete', 'button', '', '用户删除', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 21:00:18', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (379178325367304192, 378698987572191232, '0,378675820795498490,378698987572191232', 0, '', '', 'sys:user:batchDelete', 'button', '', '用户批量删除', '', 1004, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, NULL, 1, '1', '2026-02-11 21:00:18', '1', '2026-02-11 21:00:18');
INSERT INTO `sys_menu` VALUES (379178325379887104, 378698987572191232, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:user:permission', 'button', '', '用户授权', '', 1004, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 21:00:18', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (379178325392470016, 378698987572191232, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:user:data-space', 'button', '', '数据权限配置', '', 1005, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 21:00:18', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (379178415574200320, 379177285523845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:role:data-space', 'button', '', '数据权限配置', '', 1005, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-02-11 21:00:39', '1', '2026-09-29 15:07:32');
INSERT INTO `sys_menu` VALUES (400480712690221056, 0, '0', 0, 'admin-demo', '/admin-demo', '', 'dir', 'layout.base', '组件实例', '', 0, 'component copy', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-04-11 15:48:22', '1', '2026-04-11 15:53:49');
INSERT INTO `sys_menu` VALUES (400481116136128512, 400480712690221056, '0,400480712690221056', 0, 'admin-demo_dialog', '/admin-demo/dialog', '', 'menu', 'view.admin-dialog_index', '弹窗', '', 1000, 'documentation', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-04-11 15:49:59', '1', '2026-04-11 15:55:46');
INSERT INTO `sys_menu` VALUES (400481325461258240, 400481116136128512, '0,400480712690221056,400480712690221056', 0, '', '', 'test-menu:dialog:query', 'button', '', '查看', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-11 15:50:49', '1', '2026-04-11 15:55:46');
INSERT INTO `sys_menu` VALUES (400482452860813312, 400480712690221056, '0,400480712690221056', 0, 'admin-demo_dict-selet', '/admin-demo/dict-selet', '', 'menu', 'view.select-dict_index', '字典选择器', '', 0, 'color', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-04-11 15:55:17', '1', '2026-04-11 16:07:17');
INSERT INTO `sys_menu` VALUES (400556688791027712, 378675820795498490, '0,378675820795498490', 0, 'dev_dict', '/dev/dict', '', 'menu', 'view.dev_dict_index', '字典维护', '', 12, 'excel', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-04-11 20:50:17', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (400556688866525184, 400556688791027712, '0,378675820795498490,378675820795498490', 0, '', '', 'dev:dict:query', 'button', '', '字典查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-11 20:50:17', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (400556688883302400, 400556688791027712, '0,378675820795498490,378675820795498490', 0, '', '', 'dev:dict:add', 'button', '', '字典添加', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-11 20:50:17', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (400556688891691008, 400556688791027712, '0,378675820795498490,378675820795498490', 0, '', '', 'dev:dict:edit', 'button', '', '字典编辑', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-11 20:50:17', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (400556688904273920, 400556688791027712, '0,378675820795498490,378675820795498490', 0, '', '', 'dev:dict:delete', 'button', '', '字典删除', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-11 20:50:17', '1', '2026-09-29 15:07:18');
INSERT INTO `sys_menu` VALUES (400580380031844352, 0, '0', 0, 'home2', '/home2', '', 'menu', 'layout.base$view.home', 'home', '', 0, 'clipboard', 1, '', 0, 0, NULL, 0, 1, '2', '[]', 1, 1, '1', '2026-04-11 22:24:25', NULL, NULL);
INSERT INTO `sys_menu` VALUES (404039455621140480, 379177154451845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:org:query', 'button', '', '组织查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-21 11:29:33', '1', '2026-09-29 15:07:24');
INSERT INTO `sys_menu` VALUES (404039455654694912, 379177154451845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:org:add', 'button', '', '组织添加', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-21 11:29:33', '1', '2026-09-29 15:07:24');
INSERT INTO `sys_menu` VALUES (404039455667277824, 379177154451845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:org:edit', 'button', '', '组织编辑', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-21 11:29:33', '1', '2026-09-29 15:07:24');
INSERT INTO `sys_menu` VALUES (404039455684055040, 379177154451845120, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:org:delete', 'button', '', '组织删除', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-21 11:29:33', '1', '2026-09-29 15:07:24');
INSERT INTO `sys_menu` VALUES (404387341160644608, 378698987572191232, '0,378675820795498490,378698987572191232', 0, '', '', 'sys:user:batch-delete', 'button', '', '用户批量删除', '', 1004, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 1, '1', '2026-04-22 10:31:55', '1', '2026-04-22 10:31:55');
INSERT INTO `sys_menu` VALUES (404536063869808640, 378698987572191232, '0,378675820795498490,378698987572191232', 0, '', '', 'sys:user:enabel', 'button', '', '用户启用禁用', '', 1006, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 1, '1', '2026-04-22 20:22:54', '1', '2026-04-22 20:22:54');
INSERT INTO `sys_menu` VALUES (404536192299397120, 378698987572191232, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:user:enable', 'button', '', '用户启用禁用', '', 1006, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-04-22 20:23:24', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (455504055889522688, 378763754596945920, '0,378763754596945920', 0, 'dev_config', '/dev/config', '', 'menu', 'view.dev_config_index', '参数设置', '', 0, 'component copy', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-09-10 11:51:30', '1', '2026-10-12 20:48:38');
INSERT INTO `sys_menu` VALUES (455504367241097216, 455504055889522688, '0,378763754596945920,455504055889522688', 0, '', '', 'dev:config:query', 'button', '', '查询参数', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-10 11:52:44', '1', '2026-10-12 20:48:38');
INSERT INTO `sys_menu` VALUES (456674868227448832, 378763754596945920, '0,378763754596945920', 0, 'dev_log', '/dev/log', '', 'menu', 'view.dev_log_index', '系统日志', '', 0, 'logininfor', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-09-13 17:23:53', NULL, NULL);
INSERT INTO `sys_menu` VALUES (456674868479107072, 456674868227448832, '0,378763754596945920,456674868227448832', 0, '', '', 'dev:log:query', 'button', '', '日志查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-13 17:23:53', NULL, NULL);
INSERT INTO `sys_menu` VALUES (456674868504272896, 456674868227448832, '0,378763754596945920,456674868227448832', 0, '', '', 'dev:log:delete', 'button', '', '日志删除', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-13 17:23:53', NULL, NULL);
INSERT INTO `sys_menu` VALUES (457822891724763136, 457823214174466048, '0,378763754596945920,457823214174466048', 0, 'dev_sms_log', '/dev/sms/log', '', 'menu', 'view.dev_sms_log_index', '短信日志', '', 0, '', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-09-16 21:25:43', '1', '2026-10-12 21:00:51');
INSERT INTO `sys_menu` VALUES (457822892009975808, 457822891724763136, '0,378763754596945920,457823214174466048,457823214174466048', 0, '', '', 'dev:sms:log:query', 'button', '', '短信日志查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-16 21:25:43', '1', '2026-10-12 21:00:51');
INSERT INTO `sys_menu` VALUES (457822892035141632, 457822891724763136, '0,378763754596945920,457823214174466048,457823214174466048', 0, '', '', 'dev:sms:log:delete', 'button', '', '短信日志删除', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-16 21:25:43', '1', '2026-10-12 21:00:51');
INSERT INTO `sys_menu` VALUES (457823214174466048, 378763754596945920, '0,378763754596945920', 0, 'dev_sms', '/dev/sms', '', 'dir', 'layout.base', '短信配置', '', 0, 'education', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-09-16 21:27:00', '1', '2026-09-16 21:42:13');
INSERT INTO `sys_menu` VALUES (457823401206870016, 457823214174466048, '0,378763754596945920,457823214174466048', 0, 'dev_sms_template', '/dev/sms/template', '', 'menu', 'view.dev_sms_template_index', '短信模板', '', 0, '', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-09-16 21:27:45', '1', '2026-09-16 21:42:13');
INSERT INTO `sys_menu` VALUES (457823401269784576, 457823401206870016, '0,378763754596945920,457823214174466048,457823401206870016', 0, '', '', 'dev:sms:template:', 'button', '', '', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 1, '1', '2026-09-16 21:27:45', '1', '2026-09-16 21:29:28');
INSERT INTO `sys_menu` VALUES (457824431197908992, 457823401206870016, '0,378763754596945920,457823214174466048,457823214174466048', 0, '', '', 'dev:sms:template:query', 'button', '', '短信模板查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-16 21:31:50', '1', '2026-09-16 21:42:13');
INSERT INTO `sys_menu` VALUES (457824431214686208, 457823401206870016, '0,378763754596945920,457823214174466048,457823214174466048', 0, '', '', 'dev:sms:template:add', 'button', '', '短信模板添加', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-16 21:31:50', '1', '2026-09-16 21:42:13');
INSERT INTO `sys_menu` VALUES (457824431235657728, 457823401206870016, '0,378763754596945920,457823214174466048,457823214174466048', 0, '', '', 'dev:sms:template:edit', 'button', '', '短信模板编辑', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-16 21:31:50', '1', '2026-09-16 21:42:13');
INSERT INTO `sys_menu` VALUES (457824431252434944, 457823401206870016, '0,378763754596945920,457823214174466048,457823214174466048', 0, '', '', 'dev:sms:template:delete', 'button', '', '短信模板删除', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-16 21:31:50', '1', '2026-09-16 21:42:13');
INSERT INTO `sys_menu` VALUES (458169495971348480, 378763754596945920, '0,378763754596945920', 0, 'dev_file', '/dev/file', '', 'menu', 'view.dev_file_index', '文件管理', '', 0, 'clipboard', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-09-17 20:23:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (458169496285921280, 458169495971348480, '0,378763754596945920,458169495971348480', 0, '', '', 'dev:file:query', 'button', '', '文件查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-17 20:23:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (458169496302698496, 458169495971348480, '0,378763754596945920,458169495971348480', 0, '', '', 'dev:file:delete', 'button', '', '文件删除', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-17 20:23:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (462413489207218176, 378675820795498490, '0,378675820795498490', 0, 'sys_account', '/sys/account', '', 'menu', 'view.sys_account_index', '账号维护', '', 1, 'account', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-09-29 13:27:07', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (462413489349824512, 462413489207218176, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:account:add', 'button', '', '添加账号', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 13:27:07', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (462413489370796032, 462413489207218176, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:account:edit', 'button', '', '编辑账号', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 13:27:07', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (462413489391767552, 462413489207218176, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:account:delete', 'button', '', '删除账号', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 13:27:07', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (462413489408544768, 462413489207218176, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:account:enable', 'button', '', '启用账号', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 13:27:07', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (462413489425321984, 462413489207218176, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:account:resetPwd', 'button', '', '重置账号密码', '', 1004, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 13:27:07', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (462413489442099200, 462413489207218176, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:account:export', 'button', '', '导出账号', '', 1005, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 13:27:07', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (462413619994005504, 462413489207218176, '0,378675820795498490,378675820795498490', 0, '', '', 'sys:account:query', 'button', '', '账号查询', '', 1006, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 13:27:38', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (462456800563363840, 378698987572191232, '0,378675820795498490,378698987572191232', 0, '', '', 'sys:user:roleSet', 'button', '', '分配角色', '', 1007, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 16:19:13', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (462456800601112576, 378698987572191232, '0,378675820795498490,378698987572191232', 0, '', '', 'sys:user:resetPwd', 'button', '', '重置密码', '', 1008, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 16:19:13', '1', '2026-09-29 16:19:13');
INSERT INTO `sys_menu` VALUES (462490678258712576, 462413489207218176, '0,378675820795498490,462413489207218176', 0, '', '', 'sys:account:disable', 'button', '', '停用账号', '', 1007, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-09-29 18:33:50', '1', '2026-10-02 16:30:27');
INSERT INTO `sys_menu` VALUES (463546688209625088, 378675820795498490, '0,378675820795498490', 0, 'sys_temant_manage', '/sys/temant/manage', '', 'dir', 'layout.base', '租户管理', '', 8, '', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-10-02 16:30:03', '1', '2026-10-02 16:30:43');
INSERT INTO `sys_menu` VALUES (463547221913837568, 463546688209625088, '0,378675820795498490,463546688209625088', 0, 'sys_tenant', '/sys/tenant', '', 'menu', 'view.sys_tenant_index', '租户信息', '', 0, 'gonsi', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-10-02 16:32:10', '1', '2026-10-02 16:37:57');
INSERT INTO `sys_menu` VALUES (463548301993209856, 463546688209625088, '0,378675820795498490,463546688209625088', 0, 'sys_tenant_package', '/sys/tenant/package', '', 'menu', 'view.sys_tenant_package_index', '租户功能套餐', '', 0, 'skill', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-10-02 16:36:28', NULL, NULL);
INSERT INTO `sys_menu` VALUES (463548302362308608, 463548301993209856, '0,378675820795498490,463546688209625088,463548301993209856', 0, '', '', 'sys:tenant:package:query', 'button', '', '租户套餐查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-02 16:36:28', NULL, NULL);
INSERT INTO `sys_menu` VALUES (463548302387474432, 463548301993209856, '0,378675820795498490,463546688209625088,463548301993209856', 0, '', '', 'sys:tenant:package:add', 'button', '', '', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-02 16:36:28', NULL, NULL);
INSERT INTO `sys_menu` VALUES (463548302421028864, 463548301993209856, '0,378675820795498490,463546688209625088,463548301993209856', 0, '', '', 'sys:tenant:package:edit', 'button', '', '', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-02 16:36:28', NULL, NULL);
INSERT INTO `sys_menu` VALUES (463548302454583296, 463548301993209856, '0,378675820795498490,463546688209625088,463548301993209856', 0, '', '', 'sys:tenant:package:delete', 'button', '', '', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-02 16:36:28', NULL, NULL);
INSERT INTO `sys_menu` VALUES (463548679417700352, 463547221913837568, '0,378675820795498490,463546688209625088,463547221913837568', 0, '', '', 'sys:tenant:query', 'button', '', '租户查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-02 16:37:57', '1', '2026-10-02 16:37:58');
INSERT INTO `sys_menu` VALUES (463548679472226304, 463547221913837568, '0,378675820795498490,463546688209625088,463547221913837568', 0, '', '', 'sys:tenant:add', 'button', '', '租户添加', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-02 16:37:58', '1', '2026-10-02 16:37:58');
INSERT INTO `sys_menu` VALUES (463548679497392128, 463547221913837568, '0,378675820795498490,463546688209625088,463547221913837568', 0, '', '', 'sys:tenant:edit', 'button', '', '租户编辑', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-02 16:37:58', '1', '2026-10-02 16:37:58');
INSERT INTO `sys_menu` VALUES (463548679518363648, 463547221913837568, '0,378675820795498490,463546688209625088,463547221913837568', 0, '', '', 'sys:tenant:delete', 'button', '', '租户删除', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-02 16:37:58', '1', '2026-10-02 16:37:58');
INSERT INTO `sys_menu` VALUES (467235642180046848, 455504055889522688, '0,378763754596945920,455504055889522688', 0, '', '', 'dev:config:edit', 'button', '', '编辑参数', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-12 20:48:38', '1', '2026-10-12 20:48:38');
INSERT INTO `sys_menu` VALUES (467238718609813504, 457822891724763136, '0,378763754596945920,457823214174466048,457822891724763136', 0, '', '', 'dev:sms:send', 'button', '', '发送新短信', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-10-12 21:00:51', '1', '2026-10-12 21:00:51');
INSERT INTO `sys_menu` VALUES (470000000000000101, 378763754596945920, '0,378763754596945920', 0, 'dev_mfa', '/dev/mfa', '', 'menu', 'view.dev_mfa_index', '2FA绑定管理', '', 10, 'lock', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-11-14 10:00:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (470000000000000102, 470000000000000101, '0,378763754596945920,470000000000000101', 0, '', '', 'dev:mfa:query', 'button', '', '2FA绑定查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-11-14 10:00:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (470000000000000103, 470000000000000101, '0,378763754596945920,470000000000000101', 0, '', '', 'dev:mfa:reset', 'button', '', '重置2FA失败次数', '', 1001, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-11-14 10:00:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (470000000000000104, 470000000000000101, '0,378763754596945920,470000000000000101', 0, '', '', 'dev:mfa:disable', 'button', '', '失效2FA绑定', '', 1002, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-11-14 10:00:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (470000000000000105, 470000000000000101, '0,378763754596945920,470000000000000101', 0, '', '', 'dev:mfa:delete', 'button', '', '删除2FA绑定', '', 1003, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-11-14 10:00:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (470000000000000106, 378763754596945920, '0,378763754596945920', 0, 'dev_mfa_event', '/dev/mfa/event', '', 'menu', 'view.dev_mfa_event_index', '2FA验证事件', '', 11, 'list', 1, '', 0, 0, NULL, 0, 1, NULL, '[]', 1, 0, '1', '2026-11-14 10:00:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES (470000000000000107, 470000000000000106, '0,378763754596945920,470000000000000106', 0, '', '', 'dev:mfaEvent:query', 'button', '', '2FA验证事件查询', '', 1000, NULL, NULL, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, 1, 0, '1', '2026-11-14 10:00:00', NULL, NULL);

-- ----------------------------
-- Table structure for sys_org
-- ----------------------------
DROP TABLE IF EXISTS `sys_org`;
CREATE TABLE `sys_org`  (
  `id` bigint(20) NOT NULL COMMENT '组织ID',
  `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '上级ID;默认为0',
  `ancestors` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '祖级列表;以逗号分隔每个上级的id，id是唯一键，所以可以直接link',
  `director_user_id` bigint(20) NULL DEFAULT NULL COMMENT '主管用户ID',
  `org_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组织名称',
  `org_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织编码',
  `sort_code` int(11) NOT NULL DEFAULT 0 COMMENT '排序字段',
  `status` tinyint(4) NOT NULL COMMENT '组织状态',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统组织结构' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_org
-- ----------------------------
INSERT INTO `sys_org` VALUES (1, 0, '0', 1, '糯草管理系统', 'nc-admin', 0, 1, '1', 0, NULL, NULL, NULL, NULL);
INSERT INTO `sys_org` VALUES (404198709019766784, 0, '0', 1, '组织测试', '123123', 0, 1, '1', 0, '1', '2026-04-21 22:02:22', NULL, NULL);
INSERT INTO `sys_org` VALUES (404202072415363072, 0, '0', NULL, '测试组织2', '是多少', 0, 1, '1', 1, '1', '2026-04-21 22:15:44', NULL, NULL);
INSERT INTO `sys_org` VALUES (404203006818234368, 404198709019766784, '0,404198709019766784', NULL, '测试组织0202', '谁单飞双', 0, 1, '1', 1, '1', '2026-04-21 22:19:27', NULL, NULL);
INSERT INTO `sys_org` VALUES (404204236240044032, 404198709019766784, '0,404198709019766784', 1, '阿道夫', '123', 0, 1, '1', 0, '1', '2026-04-21 22:24:20', NULL, NULL);
INSERT INTO `sys_org` VALUES (463621836619096064, 0, '0', 463621835922841600, '测试', 'testa', 99, 1, '463621811239362560', 0, '1', '2026-10-02 21:28:40', NULL, NULL);
INSERT INTO `sys_org` VALUES (468691845334577152, 0, '0', 468691845170999296, '糯草测试', 'test2', 99, 1, '468691844747374592', 0, '1', '2026-10-16 21:15:04', NULL, NULL);

-- ----------------------------
-- Table structure for sys_position
-- ----------------------------
DROP TABLE IF EXISTS `sys_position`;
CREATE TABLE `sys_position`  (
  `id` bigint(20) NOT NULL COMMENT '角色ID',
  `org_id` bigint(20) NOT NULL COMMENT '组织ID;只有这个组织可以用，不限制为0',
  `name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组织名称',
  `code` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色编码',
  `category` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色分类;这个留用，默认default',
  `level` int(11) NULL DEFAULT NULL COMMENT '职级;职级越高权限越高，自己扩展，这个留用',
  `sort_code` int(11) NULL DEFAULT NULL COMMENT '排序',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '岗位' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_position
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL COMMENT '角色ID',
  `org_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '组织ID;只有这个组织可以用，不限制为0',
  `name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '组织名称',
  `code` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色编码',
  `category` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色分类;这个留用，默认default',
  `sort_code` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `weight` int(11) NOT NULL DEFAULT 0 COMMENT '角色权重;权重高的角色可分配权重低的角色,只能0-99',
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (403838071521120255, 0, '超级管理员', 'superAdmin', 'default', 0, 99, 1, 0, '1', '2026-04-20 22:06:16', NULL, NULL);
INSERT INTO `sys_role` VALUES (403838071521120256, 0, '管理员2', 'admin', 'default', 0, 99, 1, 0, '1', '2026-04-20 22:09:19', '1', '2026-10-12 20:47:40');
INSERT INTO `sys_role` VALUES (403838250794061824, 0, '财务', 'finance', 'default', 0, 0, 1, 1, '1', '2026-04-20 22:10:02', NULL, NULL);
INSERT INTO `sys_role` VALUES (463621811461660672, 0, '系统管理员', 'superAdmin', 'default', 0, 99, 463621811239362560, 0, '1', '2026-10-02 21:28:34', NULL, NULL);
INSERT INTO `sys_role` VALUES (468691844856426496, 0, '系统管理员', 'superAdmin', 'default', 0, 99, 468691844747374592, 0, '1', '2026-10-16 21:15:04', NULL, NULL);

-- ----------------------------
-- Table structure for sys_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户ID',
  `org_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '组织ID',
  `tenant_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户编号',
  `tenant_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户名',
  `contact_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '联系人',
  `contact_tel` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '联系电话',
  `contact_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '联系地址',
  `sys_name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户系统名称',
  `expire_time` datetime NOT NULL COMMENT '到期时间',
  `tenant_admin_id` bigint(20) NOT NULL COMMENT '管理员userid',
  `enable_status` tinyint(4) NOT NULL COMMENT '是否可用',
  `data_rule` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '数据规则形式;目前只支持filed隔离',
  `package_id` bigint(20) NULL DEFAULT NULL COMMENT '租户套餐ID',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant
-- ----------------------------
INSERT INTO `sys_tenant` VALUES ('1', 0, 'sys_tenant', '糯草管理系统', '糯草', '15831699533', 'xxxxx', '糯草管理系统', '2030-12-31 17:41:32', 1, 1, 'field', 0, 0, NULL, NULL, NULL, NULL);
INSERT INTO `sys_tenant` VALUES ('463621811239362560', 1, 'testa', '测试', '仨', '15515551555', '1231', '测试系统', '2027-07-02 00:00:00', 463621835922841600, 1, '', 463574668294172672, 0, '1', '2026-10-02 21:28:33', '1', '2026-10-08 14:27:17');
INSERT INTO `sys_tenant` VALUES ('468691844747374592', 404198709019766784, 'test2', '糯草测试', '于雷', '15831699533', '无', '糯草测试', '2030-11-09 00:00:00', 468691845170999296, 1, '', 463574668294172672, 0, '1', '2026-10-16 21:15:04', '1', '2026-10-16 21:15:04');

-- ----------------------------
-- Table structure for sys_tenant_package
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant_package`;
CREATE TABLE `sys_tenant_package`  (
  `id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '租户套餐ID',
  `name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '套餐名',
  `enable_status` tinyint(4) NOT NULL COMMENT '是否启用',
  `menu_ids` json NOT NULL COMMENT '可用功能菜单ID集合',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '租户套餐' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant_package
-- ----------------------------
INSERT INTO `sys_tenant_package` VALUES ('463574668294172672', '基础套餐', 1, '[378675820795498492, 378763754596945920, 455504055889522688, 456674868227448832, 457823214174466048, 457822891724763136, 457823401206870016, 458169495971348480, 400482452860813312, 455504367241097216, 467235642180046848, 456674868479107072, 457822892009975808, 457824431197908992, 457824431252434944, 457824431214686208, 457824431235657728]', '测试', 0, '1', '2026-10-02 18:21:14', '1', '2026-10-17 10:48:50');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL COMMENT '用户ID',
  `org_id` bigint(20) NULL DEFAULT NULL COMMENT '用户所属组织',
  `avatar` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户头像',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户账号',
  `password` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码;如果是单租户系统，可以考虑去掉account层',
  `name` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `nickname` varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
  `gender` tinyint(4) NULL DEFAULT NULL COMMENT '性别',
  `phone` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话号码',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `birthday` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生日',
  `status` tinyint(4) NOT NULL COMMENT '用户状态',
  `last_login_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上一次登录IP',
  `last_login_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上一次登录地址',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '上一次登录时间',
  `last_login_device` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上一次登录设备',
  `latest_login_ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本次登录IP',
  `latest_login_address` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本次登录地址',
  `latest_login_time` datetime NULL DEFAULT NULL COMMENT '本次登录时间',
  `latest_login_device` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '本次登录设备',
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统用户;对应租户的账户，每个account对应着sys_account' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 1, 'http://localhost:8098/dev/file/download?id=2111296547143700480', 'superAdmin', '207cf410532f92a47dee245ce9b11ff71f578ebd763eb3bbea44ebd043d018fb', '糯草', 'admin', 1, '15831699533', 'yulei6@gmail.com', '2026-10-31', 1, '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-11-06 10:19:56', 'PC', '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-11-06 10:20:14', 'PC', 1, 0, NULL, NULL, '1', '2026-11-06 10:20:14');
INSERT INTO `sys_user` VALUES (462448218828693504, 1, NULL, 'test', 'bbd55134538a22b36d77e0fd1e951b0962032414234ed2463575be9207d4d4e2', 'test2', 'test', 1, '504443e05ca5c3f7f3246f33a936d2ae', 'sdfsf@1231.com', '', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1, '1', '2026-09-29 15:45:07', '1', '2026-09-29 16:27:13');
INSERT INTO `sys_user` VALUES (462496545230266368, 404198709019766784, NULL, 'test', '79d442f9b511f7fb6f1249eaee7c1d25715f41d12e3fc00adf6675f69e8578cc', 'test', '123', NULL, '504443e05ca5c3f7f3246f33a936d2ae', '123@1231.com', '', 1, NULL, NULL, NULL, NULL, '127.0.0.1', '内网IP|内网IP', '2026-10-02 21:45:51', 'PC', 1, 0, '1', '2026-09-29 18:57:09', '1', '2026-10-09 22:54:48');
INSERT INTO `sys_user` VALUES (463621835922841600, 0, NULL, 'test', '79d442f9b511f7fb6f1249eaee7c1d25715f41d12e3fc00adf6675f69e8578cc', 'test', 'test', 0, '504443e05ca5c3f7f3246f33a936d2ae', NULL, NULL, 1, '127.0.0.1', '内网IP|内网IP', '2026-10-02 21:58:31', 'PC', '127.0.0.1', '内网IP|内网IP', '2026-10-02 21:59:23', 'PC', 463621811239362560, 0, '1', '2026-10-02 21:28:39', '463621835922841600', '2026-10-02 21:59:23');
INSERT INTO `sys_user` VALUES (468691845170999296, 0, NULL, 'yulei', '627b86efdcc07ad37f9c2497712e3521b206f3a3147b9fca3195621d0661a99a', 'yulei', 'yulei', 0, '1ba70d9c64c374354558d470842ea879', NULL, NULL, 1, '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-10-16 21:59:47', 'PC', '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-10-17 10:49:22', 'PC', 468691844747374592, 0, '1', '2026-10-16 21:15:04', '468691845170999296', '2026-10-17 10:49:22');
INSERT INTO `sys_user` VALUES (468692975145852928, 404198709019766784, NULL, 'yulei', '627b86efdcc07ad37f9c2497712e3521b206f3a3147b9fca3195621d0661a99a', 'yulei', '123', 1, 'e8c42f17f66fca0e8ad0e58f7a2546ee', '1cx231@12.com', '', 1, '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-10-16 21:59:37', NULL, '127.0.0.1', 'Reserved|Reserved|Reserved', '2026-10-17 10:50:38', NULL, 1, 0, '1', '2026-10-16 21:19:33', '468691845170999296', '2026-10-17 10:50:38');

-- ----------------------------
-- Table structure for sys_user_identity
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_identity`;
CREATE TABLE `sys_user_identity`  (
  `id` bigint(20) NOT NULL COMMENT '用户权限ID',
  `type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '角色类型',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `object_id` bigint(20) NOT NULL COMMENT '对应信息ID;如角色ID或职位ID',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户身份对照表;这个表是为了将角色对应、岗位对应，甚至后续的组织对应都统一管理起来' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_identity
-- ----------------------------
INSERT INTO `sys_user_identity` VALUES (1, 1, 1, 403838071521120255, '1');
INSERT INTO `sys_user_identity` VALUES (462457719778689024, 1, 462448218828693504, 403838071521120256, '1');
INSERT INTO `sys_user_identity` VALUES (468693158243999744, 1, 468692975145852928, 403838071521120256, '1');

-- ----------------------------
-- Table structure for sys_user_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_permission`;
CREATE TABLE `sys_user_permission`  (
  `id` bigint(20) NOT NULL COMMENT '用户权限ID',
  `object_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '授权对象类型',
  `object_id` bigint(20) NOT NULL COMMENT '对应信息ID;user_id或role_id',
  `menu_ids` json NOT NULL COMMENT '可用的菜单或按钮ID集合',
  `tenant_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '租户号',
  `delete_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标识',
  `create_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户菜单授权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_permission
-- ----------------------------
INSERT INTO `sys_user_permission` VALUES (455168398222675968, 'role', 403838071521120256, '[378675820795498492, 378737168699052032, 378737168720023552]', '1', 0, '1', '2026-09-09 13:37:43', '1', '2026-10-09 16:13:05');

SET FOREIGN_KEY_CHECKS = 1;
