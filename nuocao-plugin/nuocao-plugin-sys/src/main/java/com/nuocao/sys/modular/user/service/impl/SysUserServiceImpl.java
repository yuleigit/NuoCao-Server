package com.nuocao.sys.modular.user.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import com.nuocao.auth.api.core.utils.StpLoginUserUtils;
import com.nuocao.common.ObjectUtils;
import com.nuocao.common.cache.CacheUtils;
import com.nuocao.common.enums.CommonEnableStatusEnum;
import com.nuocao.common.excel.CommonExcelCustomMergeStrategy;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.exception.CommonExceptionI18n;
import com.nuocao.common.language.LanguageUtils;
import com.nuocao.common.mapper.LambdaQueryWrapperX;
import com.nuocao.common.page.PageResult;
import com.nuocao.common.utils.*;
import com.nuocao.common.utils.collection.CollectionUtils;
import com.nuocao.common.utils.http.IpUtils;
import com.nuocao.common.utils.http.ResponseUtils;
import com.nuocao.data.access.datascope.annotation.DataScopeIgnore;
import com.nuocao.data.access.datascope.util.DataScopeUtils;
import com.nuocao.data.access.tenant.annotation.TenantIgnore;
import com.nuocao.data.access.tenant.config.TenantConfig;
import com.nuocao.data.access.tenant.util.TenantUtils;
import com.nuocao.sys.api.SysAccountApi;
import com.nuocao.sys.api.SysTenantApi;
import com.nuocao.sys.api.dto.SysAccountDTO;
import com.nuocao.sys.api.dto.SysRoleDTO;
import com.nuocao.sys.api.dto.SysUserDTO;
import com.nuocao.sys.api.dto.SysUserSampleResultDTO;
import com.nuocao.sys.api.enums.SysUserIdentityTypeEnum;
import com.nuocao.sys.core.pass.AccountPasswordUtils;
import com.nuocao.sys.modular.org.entity.SysOrg;
import com.nuocao.sys.modular.org.service.SysOrgService;
import com.nuocao.sys.modular.role.service.SysRoleService;
import com.nuocao.sys.modular.user.convert.SysUserConvert;
import com.nuocao.sys.modular.user.entity.SysUser;
import com.nuocao.sys.modular.user.entity.SysUserIdentity;
import com.nuocao.sys.modular.user.enums.SysUserSourceFromTypeEnum;
import com.nuocao.sys.modular.user.mapper.SysUserMapper;
import com.nuocao.sys.modular.user.pojo.param.*;
import com.nuocao.sys.modular.user.pojo.result.*;
import com.nuocao.sys.modular.user.service.SysUserIdentityService;
import com.nuocao.sys.modular.user.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import com.nuocao.plugin.dev.api.DevFileApi;
import org.apache.poi.ss.usermodel.*;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.data.annotation.Transaction;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户Service接口实现类
 **/
@Slf4j
@Component
public class SysUserServiceImpl implements SysUserService {


    private static final String USER_ROLE_ID_LIST_CACHE_KEY="sys:user:{}:roleIds";
    private static final String USER_DTO_CACHE_KEY="sys:user:{}:pojo";

    private static final String SELECTOR_ALL_PERMISSION_FLAG="sys:user:selector-all";

    private static final String AVATAR_BUSINESS_DIR="avatar";

    @Inject
    private CacheUtils cacheUtils;

    @Inject
    private SysUserMapper mapper;

    @Inject
    private SysAccountApi sysAccountApi;

    @Inject
    private SysRoleService sysRoleService;
    @Inject
    private SysOrgService sysOrgService;
    @Inject
    private SysTenantApi tenantApi;
    @Inject
    private SysUserIdentityService userIdentityService;

    @Inject
    private TenantConfig tenantConfig;


    @Inject
    private DevFileApi fileApi;


    @Override
    public SaSysLoginUser getLoginUserById(Long id) {
        SysUser sysUser = this.getById(id);
        return SysUserConvert.INSTANCE.convertLoginUser(sysUser);
    }

    @Override
    public SaSysLoginUser getLoginUserByUsername(String username) {
        return SysUserConvert.INSTANCE.convertLoginUser(mapper.selectOne(SysUser::getUsername, username));
    }


    @Override
    public SaSysLoginUser getLoginUserByPhone(String phone) {
        return SysUserConvert.INSTANCE.convertLoginUser(mapper.selectOne(SysUser::getPhone,phone));
    }

    @Override
    public SaSysLoginUser getLoginUserByEmail(String email) {
        return SysUserConvert.INSTANCE.convertLoginUser(mapper.selectOne(SysUser::getEmail,email));
    }

    @Override
    public PageResult<SysUser> page(SysUserPageParam sysUserPageParam) {
        QueryWrapper query = buildQueryWrapper(sysUserPageParam);
        return mapper.selectPage(sysUserPageParam,query);
    }

    @Override
    public PageResult<SysUserPageResult> pageResult(SysUserPageParam sysUserPageParam) {
        PageResult<SysUser> userList =page(sysUserPageParam);
        PageResult<SysUserPageResult> pageList = SysUserConvert.INSTANCE.convertPage(userList);

        if(pageList.getRecords().isEmpty()){
            return pageList;
        }
        Map<Long,List<String>> userRoleNameMap = getUserRoleNameMap(pageList.getRecords().stream().map(SysUserPageResult::getId).collect(Collectors.toList()));

        Map<Long,String> orgNameMap = CollectionUtils.convertMap(
                sysOrgService.selectListByIds(pageList.getRecords().stream().map(SysUserPageResult::getOrgId).distinct().collect(Collectors.toList())),
                SysOrg::getId,
                SysOrg::getOrgName
        );
        for (SysUserPageResult r:
             pageList.getRecords()) {
            r.setOrgName(orgNameMap.getOrDefault(r.getOrgId(),""));
            r.setRoleNames(userRoleNameMap.getOrDefault(r.getId(), Collections.emptyList()));
            r.setIsTenantAdmin(tenantApi.isTenantAdmin(r.getTenantId(),r.getId()) );
        }
        return pageList;
    }

    @Override
    public SysUser getById(Long userId) {
        return mapper.selectOneById(userId);
    }

    @Override
    public SysUser getByUsername(String account) {
        return mapper.selectOne(SysUser::getUsername,account);
    }

    @Override
    public List<SysUserDTO> getUserDtoList(List<Long> userIds) {
        QueryWrapper query = new QueryWrapper();
        query.in(SysUser::getId,userIds);
        return mapper.selectListByQueryAs(query,SysUserDTO.class);
    }

    @Override
    public SysUserDTO getUserDtoCacheByUserId(Long userId) {
        SysUserDTO user= cacheUtils.getOrSetObject(StrFormatUtils.doFormat(USER_DTO_CACHE_KEY,userId),SysUserDTO.class,()->{
            SysUserDTO userDto = SysUserConvert.INSTANCE.convertDto(getById(userId));
            if(userDto==null){
                userDto = new SysUserDTO();
                userDto.setId(0L);
            }
            return userDto;
        });
        return user==null || user.getId().equals(0L)?null:user;

    }

    @Override
    @TenantIgnore
    @DataScopeIgnore
    public List<SysUserDTO> selectListByUsernameIgnoreTenant(String username) {
        QueryWrapper query =new QueryWrapper();
        query.eq(SysUser::getUsername, username);
        return mapper.selectListByQueryAs(query,SysUserDTO.class);
    }

    @Override
    @Transaction
    public void add(SysUserAddParam sysUserAddParam, String sourceFromType, boolean passwordIsHashValue) {
        SysUser sysUser = SysUserConvert.INSTANCE.convert(sysUserAddParam);
        //验证是否开启多租户模式，多租户模式不可直接添加仅能通过邀请进入
        //未启用时，必须输入密码
        if(!tenantConfig.isEnabled()){
            if(StringUtils.isEmpty(sysUserAddParam.getPassword()) ){
                throw new CommonExceptionI18n("sys.password.notEmpty");
            }
        }

        validInfo(sysUser);

        //账号密码与account相同
        SysAccountDTO sysAccount = sysAccountApi.getOrCrateByUsername(sysUserAddParam.getUsername(),sysUserAddParam.getPassword(),
                sysUserAddParam.getName(),
                sysUserAddParam.getNickname(),
                sysUserAddParam.getPhone(),
                sysUserAddParam.getEmail(),
                "",
                true);

        if (ObjectUtil.isEmpty(sysUser.getAvatar())) {
            // 设置默认头像
            //sysUser.setAvatar(CommonAvatarUtil.(sysUser.getName()));
        }
        //密码与账户密码相同
        sysUser.setPassword(sysAccount.getPassword());
        // 设置状态
        sysUser.setStatus(CommonEnableStatusEnum.ENABLE.getValue());
        mapper.insert(sysUser);
        if(CollectionUtils.isNotEmpty(sysUserAddParam.getRoleIds())){
            this.setUserRoleIds(sysUser.getId(),sysUserAddParam.getRoleIds());
        }
    }

    @Transaction
    @Override
    public void edit(SysUserEditParam sysUserEditParam) {
        SysUser sysUser = SysUserConvert.INSTANCE.convert(sysUserEditParam);



        String phone = sysUser.getPhone();
        validInfo(sysUser);

        mapper.updateById(sysUser);

        //如果不是多租户模式，则同步修改账号信息
        if(!tenantConfig.isEnabled()) {
            sysAccountApi.updateByUsername(sysUser.getUsername(),
                    sysUser.getName(),
                    sysUser.getNickname(),
                    sysUser.getPhone(),
                    sysUser.getEmail()
                    );
        }
    }

    @Override
    public void delete(List<SysUserIdParam> sysUserIdParamList) {
        if(sysUserIdParamList==null) return;
        deleteByIds(sysUserIdParamList.stream().map(SysUserIdParam::getId).collect(Collectors.toList()));
    }

    @Override
    public void deleteByIds(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) return;
        List<SysUser> userList = mapper.selectListByIds(userIds);
        List<String> userNames = userList.stream().map(SysUser::getUsername).collect(Collectors.toList());
        for (SysUser user:
             userList) {
            validIsTenantAdmin(user.getTenantId(),user.getId(),user.getUsername());
        }

        List<Long> deleteUserIds = userList.stream().map(SysUser::getId).collect(Collectors.toList());

        mapper.deleteBatchByIds(deleteUserIds);
        //清理用户作为主管的部门主管
        sysOrgService.clearDirectorByUserIds(deleteUserIds);
        //清理用户对应角色配置
        for (SysUser user:
                userList) {
           this.setUserRoleIds(user.getId(),new ArrayList<>());
        }

        //如果不是多租户模式，则同步删除账号信息
        if(!tenantConfig.isEnabled()) {
            sysAccountApi.deleteByUsernames(userNames);
        }
    }

    @Override
    public SysUser detail(SysUserIdParam sysUserIdParam) {

        return queryEntity(sysUserIdParam.getId());
    }

    @Override
    public SysUser queryEntity(Long id) {
        SysUser user = mapper.selectOneById(id);
        CommonExceptionI18n.notNull(user,"sys.user.notExists",id);
        return user;
    }

    @Override
    public void disableUser(SysUserIdParam sysUserIdParam) {
        SysUser user = mapper.selectOneById(sysUserIdParam.getId());
        CommonExceptionI18n.notNull(user,"sys.user.notExists",sysUserIdParam.getId());
        validIsTenantAdmin(user.getTenantId(),user.getId(),StringUtils.firstNotBlank(user.getName(),user.getNickname()));
        UpdateChain.of(SysUser.class).set(SysUser::getStatus,CommonEnableStatusEnum.DISABLE.getValue())
                .eq(SysUser::getId,sysUserIdParam.getId()).update();
    }

    @Override
    public void enableUser(SysUserIdParam sysUserIdParam) {
        SysUser user = mapper.selectOneById(sysUserIdParam.getId());
        CommonExceptionI18n.notNull(user,"sys.user.notExists",sysUserIdParam.getId());
        validIsTenantAdmin(user.getTenantId(),user.getId(),StringUtils.firstNotBlank(user.getName(),user.getNickname()));
        UpdateChain.of(SysUser.class).set(SysUser::getStatus,CommonEnableStatusEnum.ENABLE.getValue())
                .eq(SysUser::getId,sysUserIdParam.getId()).update();
    }

    @Override
    public String updatePassword(Long userId, String newPassword) {
        SysUser user = queryEntity(userId);
        AccountPasswordUtils.validNewPassword(newPassword);
        String newPasswordEn = CryptUtils.hashValue(newPassword);

        syncPasswordByUsername(user.getUsername(),newPasswordEn,true);
        sysAccountApi.syncPassword(user.getUsername(),newPasswordEn,true);
        return newPassword;
    }



    @Override
    public String updateAvatar(UploadedFile file) {
        String suffix = Objects.requireNonNull(FileUtil.getSuffix(file.getName())).toLowerCase();
        byte[] bytes = IoUtil.readBytes(file.getContent());
        return processAndSaveAvatar(bytes, suffix);
    }


    @Override
    public String updateAvatarByBase64(String base64) {
        // 1. 非空与合法性校验
        if (StrUtil.isBlank(base64)) {
            throw new CommonException("头像数据不能为空");
        }
        if (!isValidImageBase64(base64)) {
            throw new CommonException("非法图片格式");
        }

        try {
            // 2. 去除 Base64 前缀（如 "data:image/png;base64,"）并解码为字节数组
            String pureBase64 = StrUtil.subAfter(base64, "base64,", false);
            if (StrUtil.isBlank(pureBase64)) {
                pureBase64 = base64; // 若无前缀则使用原字符串
            }
            byte[] bytes = Base64.decode(pureBase64);

            // 3. 提取图片后缀（若已知格式可写死或从 Base64 头部解析，通常默认 png 或 jpg）
            String suffix = "png";
            if (base64.startsWith("data:image/")) {
                suffix = StrUtil.subBetween(base64, "data:image/", ";base64");
            }

            // 4. 调用公共方法处理图片并存储
            return processAndSaveAvatar(bytes, suffix);

        } catch (Exception e) {
            log.error(">>> Base64 头像解析或修改失败：", e);
            throw new CommonException("头像修改失败");
        }
    }

    private boolean isValidImageBase64(String base64) {
        // 1. 去掉 Data URI 前缀
        String pureBase64 = base64.contains(",") ? base64.split(",")[1] : base64;

        try {
            byte[] bytes = Base64.decode(pureBase64);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            // 2. 使用 ImageIO 读取图片流
            BufferedImage bi = ImageIO.read(bais);

            // 如果 bi 不为空，说明确实是图片格式（且 ImageIO 能解析）
            return bi != null;
        } catch (Exception e) {
            log.warn(">>> Base64 图片校验异常: {}", e.getMessage());
            return false;
        }
    }
    /**
     * 3. 抽离出的公共图片处理与保存逻辑
     */
    private String processAndSaveAvatar(byte[] bytes, String suffix) {
        SysUser sysUser = this.queryEntity(StpLoginUserUtils.getLoginUserId());
        try {
            // 1. 读取并缩放图片
            BufferedImage image = ImgUtil.toImage(bytes);
            InputStream inputStream;
            if (image.getWidth() <= 200 && image.getHeight() <= 200) {
                inputStream = IoUtil.toStream(bytes);
            } else {
                // ImgUtil.scale 缩放后转为流（Hutool 的 scale 返回的是 Image 对象，转为 InputStream 存储）
                // 超过 200x200，进行缩放并写入内存输出流
                Image scaledImg = ImgUtil.scale(image, 200, 200);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImgUtil.write(scaledImg, suffix, out);
                inputStream = new ByteArrayInputStream(out.toByteArray());
            }

            // 2. 存储文件并获取 URL
            String fileName = sysUser.getId() + "_" + System.currentTimeMillis() + "." + suffix;
            String avatarUrl = fileApi.storageFileReturnUrlDynamic(AVATAR_BUSINESS_DIR, fileName, inputStream);

            // 3. 更新数据库中的 Avatar 字段为 URL
            UpdateChain.of(SysUser.class)
                    .eq(SysUser::getId, sysUser.getId())
                    .set(SysUser::getAvatar, avatarUrl)
                    .update();
            //删除旧图
            fileApi.storeDeleteFileByUrl(CollectionUtils.toList(sysUser.getAvatar()));
            return avatarUrl;

        } catch (Exception e) {
            log.error(">>> 头像修改失败，用户id: {}", sysUser.getId(), e);
            throw new CommonException("头像修改失败，用户id值为：{}", sysUser.getId());
        }
    }



    @Override
    @DataScopeIgnore
    public void updateUserLoginInfo(Long userId, String device) {
        SysUser sysUser = this.queryEntity(userId);
        SysUser upUser = new SysUser();
        upUser.setId(userId);
        upUser.setLastLoginTime(sysUser.getLatestLoginTime());
        upUser.setLastLoginIp(sysUser.getLatestLoginIp());
        upUser.setLastLoginAddress(sysUser.getLatestLoginAddress());
        upUser.setLastLoginDevice(sysUser.getLatestLoginDevice());
        upUser.setLatestLoginTime(DateTime.now());
        String ip = IpUtils.getRequestIp();
        upUser.setLatestLoginIp(ip);
        upUser.setLatestLoginAddress(IpUtils.getCityInfo(ip));
        upUser.setLatestLoginDevice(device);
        mapper.updateById(upUser);
    }

    @Override
    public void updateTenantId(Long userId, Long tenantId) {
        mapper.updateUserTenantId(userId,tenantId);
    }

    @Override
    public void updatePasswordByOld(SysUserUpdatePwdByOldParam param) {
        SysUser sysUser = this.queryEntity(StpLoginUserUtils.getLoginUserId());
        String password = param.getPassword();
        String newPassword = param.getNewPassword();
        if (!CryptUtils.hashValue(password).equals(sysUser.getPassword())) {
            throw new CommonExceptionI18n("sys.user.password.oldError");
        }
        String enPassword = CryptUtils.hashValue(newPassword);
        syncPasswordByUsername(sysUser.getUsername(),enPassword,true);
        sysAccountApi.syncPassword(sysUser.getUsername(),enPassword,true);
    }

    @Override
    @DataScopeIgnore
    public Integer getCountByTenant(Long tenantId) {
        return TenantUtils.execute(tenantId,()->{
            return ConvertUtils.toInt(mapper.selectCount(),0);
        });
    }

    @Override
    public Integer getCountByRoleId(Long roleId) {
        return userIdentityService.selectUserCountByRoleId(roleId);
    }

    private SysUser CreateUserWithAccountDo(SysUserAddParam sysUserAddParam,Long defaultRoleId, boolean passwordIsHashValue){
        // 保存用户
        this.add(sysUserAddParam, SysUserSourceFromTypeEnum.SYSTEM_REGISTER.getValue(),passwordIsHashValue);
        // 获取用户信息
        SysUser sysUser = mapper.selectOne(SysUser::getUsername,sysUserAddParam.getUsername());
        // 授权默认角色
        setUserRoleIds(sysUser.getId(),CollectionUtils.toList(defaultRoleId));
        return sysUser;
    }

    @Transaction
    @Override
    public SysUser createUserWithAccount(SysAccountDTO accountDto,Long defaultRoleId) {
        SysUserAddParam sysUserAddParam = new SysUserAddParam();
        sysUserAddParam.setUsername(accountDto.getUsername());
        sysUserAddParam.setName(accountDto.getNickname());
        sysUserAddParam.setNickname(accountDto.getNickname());
        sysUserAddParam.setPassword(accountDto.getPassword());
        sysUserAddParam.setPhone(accountDto.getPhone());
        sysUserAddParam.setOrgId(0L);

        // 返回用户
        return CreateUserWithAccountDo(sysUserAddParam,defaultRoleId,true);
    }

    @Transaction
    @Override
    public SysUser createUserWithAccount(String username, String name, String phone, String password, Long defaultRoleId, boolean passwordIsHashValue) {
        SysUserAddParam sysUserAddParam = new SysUserAddParam();
        sysUserAddParam.setUsername(username);
        sysUserAddParam.setName(name);
        sysUserAddParam.setNickname(name);
        sysUserAddParam.setPassword(password);
        sysUserAddParam.setPhone(phone);
        sysUserAddParam.setGender(0);
        sysUserAddParam.setOrgId(0L);
        return CreateUserWithAccountDo(sysUserAddParam,defaultRoleId,passwordIsHashValue);

    }

    @Override
    public List<Long> getUserRoleIdList(Long userId) {
        return userIdentityService.selectRoleIdListByUserId(userId);
    }

    @Override
    public List<String> getUserRoleCodeList(Long userId) {
        return userIdentityService.selectRoleCodeListByUserId(userId);
    }

    @Override
    public List<Long> getUserRoleIdListByCache(Long userId) {
        return cacheUtils.getOrSetList(StrFormatUtils.doFormat(USER_ROLE_ID_LIST_CACHE_KEY,userId),Long.class,()->{
            return getUserRoleIdList(userId);
        });
    }

    @Override
    public void clearUserCache(Long userId) {
        cacheUtils.removes(StrFormatUtils.doFormat(USER_ROLE_ID_LIST_CACHE_KEY,userId));
    }

    @Override
    public List<Long> getUserIdListByOrgIdList(List<Long> orgIds) {
        QueryWrapper query = new QueryWrapper();
        query.select(SysUser::getId)
                        .in(SysUser::getOrgId,orgIds);
       return mapper.selectObjectListByQueryAs(query,Long.class);
    }

    @Override
    public void setUserRoleIds(Long userId, List<Long> roleIds) {

        if(roleIds==null) return;
        //将不正确的id去掉
        roleIds = roleIds.stream().filter(NumberUtils::isGtZero).collect(Collectors.toList());
        //写入新的
        if(CollectionUtils.isEmpty(roleIds)){
            return;
        }
        //如果得到的名字和id数不一致，则说明选择了不存在的角色
        List<String> names= sysRoleService.getNames(roleIds);
        if(names.size()!=roleIds.size()){
            throw new CommonExceptionI18n("sys.user.role.notExists");
        }
        //删除当前用户的角色
        userIdentityService.deleteRoleByUserId(userId);
        //写入新角色
        userIdentityService.insertRoleList(userId,roleIds);


    }

    @Override
    public List<SysRoleDTO> getUserRoles(Long userId) {
        return sysRoleService.getRoleListByUserId(userId);
    }

    @Transaction
    @Override
    public void importUser(UploadedFile file) {
        throw new CommonException("不支持导入");
    }


    @Override
    public void exportUser(SysUserExportParam sysUserExportParam, Context response) throws IOException {
        File tempFile = null;
        try {
            QueryWrapper queryWrapper = buildQueryWrapper(SysUserConvert.INSTANCE.convertQueryParam(sysUserExportParam));
            if (ObjectUtil.isNotEmpty(sysUserExportParam.getUserIds())) {
                queryWrapper.in(SysUser::getId, sysUserExportParam.getUserIds());
            }
            String fileName = "user_data.xlsx";
            List<SysUser> sysUserList = mapper.selectListByQuery(queryWrapper);
            if (CollectionUtils.isEmpty(sysUserList)) {
                throw new CommonException("无数据可导出");
            }
            sysUserList = CollectionUtil.sort(sysUserList, Comparator.comparing(SysUser::getOrgId));
            List<SysUserExportResult> sysUserExportResultList = sysUserList.stream()
                    .map(sysUser -> {

                        SysUserExportResult sysUserExportResult =  SysUserConvert.INSTANCE.convertExportResult(sysUser);
                        // 将base64转为byte数组
                        if (ObjectUtil.isNotEmpty(sysUser.getAvatar())) {
//                            sysUserExportResult.setAvatar(ImgUtil.toBytes(ImgUtil.toImage(StrUtil
//                                    .split(sysUser.getAvatar(), StrUtil.COMMA).get(1)), ImgUtil.IMAGE_TYPE_PNG));
                        }
                        return sysUserExportResult;
                    }).collect(Collectors.toList());
            // 创建临时文件
            tempFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);

            // 头的策略
            WriteCellStyle headWriteCellStyle = new WriteCellStyle();
            WriteFont headWriteFont = new WriteFont();
            headWriteFont.setFontHeightInPoints((short) 14);
            headWriteCellStyle.setWriteFont(headWriteFont);
            // 水平垂直居中
            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 内容的策略
            WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
            // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
            contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
            // 内容背景白色
            contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            WriteFont contentWriteFont = new WriteFont();

            // 内容字体大小
            contentWriteFont.setFontHeightInPoints((short) 12);
            contentWriteCellStyle.setWriteFont(contentWriteFont);

            //设置边框样式，细实线
            contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
            contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
            contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
            contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);

            // 水平垂直居中
            contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
            contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
            HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle,
                    contentWriteCellStyle);

            // 写excel
            EasyExcel.write(tempFile.getPath(), SysUserExportResult.class)
                    // 自定义样式
                    .registerWriteHandler(horizontalCellStyleStrategy)
                    // 自动列宽
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 机构分组合并单元格
                    .registerWriteHandler(new CommonExcelCustomMergeStrategy(sysUserExportResultList.stream().map(SysUserExportResult::getOrgName)
                            .collect(Collectors.toList()), 0))
                    // 设置第一行字体
                    .registerWriteHandler(new CellWriteHandler() {
                        @Override
                        public void afterCellDispose(CellWriteHandlerContext context) {
                            WriteCellData<?> cellData = context.getFirstCellData();
                            WriteCellStyle writeCellStyle = cellData.getOrCreateStyle();
                            Integer rowIndex = context.getRowIndex();
                            if (rowIndex == 0) {
                                WriteFont headWriteFont = new WriteFont();
                                headWriteFont.setFontName("宋体");
                                headWriteFont.setBold(true);
                                headWriteFont.setFontHeightInPoints((short) 16);
                                writeCellStyle.setWriteFont(headWriteFont);
                            }
                        }
                    })
                    // 设置表头行高
                    .registerWriteHandler(new AbstractRowHeightStyleStrategy() {
                        @Override
                        protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
                            if (relativeRowIndex == 0) {
                                // 表头第一行
                                row.setHeightInPoints(34);
                            } else {
                                // 表头其他行
                                row.setHeightInPoints(30);
                            }
                        }

                        @Override
                        protected void setContentColumnHeight(Row row, int relativeRowIndex) {
                            // 内容行
                            row.setHeightInPoints(20);
                        }
                    })
                    .sheet("user")
                    .doWrite(sysUserExportResultList);
            ResponseUtils.download(tempFile, response);
        } catch (Exception e) {
            log.error(">>> 用户导出失败：", e);
            ResponseUtils.renderError(response, LanguageUtils.getMessage("sys.user.export.failed"));
        } finally {
            FileUtil.del(tempFile);
        }
    }

    private void validIsTenantAdmin(Long tenantId,Long userId,String username){
        if(NumberUtils.isLtZero(userId) || NumberUtils.isLtZero(tenantId)) return;
        if(userId ==1|| tenantApi.isTenantAdmin(tenantId,userId)){
            throw new CommonExceptionI18n("sys.user.edit.tenantAdmin",username);
        }
    }

    @DataScopeIgnore
    public void validInfo(SysUser info) {

        //验证账号是否重复
        if(!StringUtils.isNull(info.getUsername())) {
            SysUser temp = mapper.selectOne(SysUser::getUsername, info.getUsername());
            if (temp != null && !temp.getId().equals(info.getId())) {
                throw new CommonExceptionI18n("sys.user.account.exists", temp.getUsername());
            }
        }
        if(NumberUtils.isGtZero(info.getId())){//修改验证
            //得到老数据
            SysUser oldInfo = queryEntity(info.getId());
            validIsTenantAdmin(info.getTenantId(),oldInfo.getId(),oldInfo.getUsername());
        }


        //需要更新电话时候，则验证电话是否存在
        if(StringUtils.isNotBlank(info.getPhone())){
            String phone = CryptUtils.sm4CbcEncrypt(info.getPhone());
            SysUser temp = mapper.selectOne(SysUser::getPhone,phone);
            if(temp!=null && !temp.getId().equals(info.getId()))
            {
                throw new CommonExceptionI18n("sys.user.phone.exists",temp.getUsername());
            }
        }else{
            info.setPhone(null);
        }

        if (StringUtils.isNotBlank(info.getEmail())) {
            SysUser temp = mapper.selectOne(SysUser::getPhone,info.getPhone());
            if(temp!=null && !temp.getId().equals(info.getId()))
            {
                throw new CommonExceptionI18n("sys.user.email.exists",temp.getUsername());
            }
        }else{
            info.setEmail(null);
        }



    }
    private LambdaQueryWrapperX buildQueryWrapper(SysUserPageParam param){
        LambdaQueryWrapperX<SysUser> query = new LambdaQueryWrapperX<>();
        query.likeIfPresent(SysUser::getUsername,param.getUsername());
        query.eqIfPresent(SysUser::getStatus,param.getStatus())
                .eqIfPresent(SysUser::getEmail,param.getEmail())
                .eqIfPresent(SysUser::getOrgId,param.getOrgId());
        if(StringUtils.isNotBlank(param.getSearchKey())){
            query.and(o->{
                o.like(SysUser::getName,param.getSearchKey()).or(o2->{
                    o2.like(SysUser::getNickname,param.getSearchKey())
                            ;
                });
            });
        }
        if(StringUtils.isNotBlank(param.getPhone())){
            query.eq(SysUser::getPhone, CryptUtils.sm2Encrypt(param.getPhone()));
        }

        if(ObjectUtils.isNotNull(param.getRoleId())){
            query.in(SysUser::getId,new LambdaQueryWrapperX<SysUserIdentity>().selectX(SysUserIdentity::getUserId).eq(SysUserIdentity::getObjectId,param.getRoleId()));
        }

        return query;
    }

    @Override
    public PageResult<UserSelectorResult> selectorPage(SysUserPageParam pageParam) {
        LambdaQueryWrapperX<SysUser> query = buildQueryWrapper(pageParam); //构建基础查询
        //如果可以选择所有人就忽略数据权限执行
        if(StpLoginUserUtils.hasPermission(SELECTOR_ALL_PERMISSION_FLAG))
        {
            return DataScopeUtils.executeIgnore(()->{
                return mapper.selectJoinPage(pageParam,UserSelectorResult.class,query);
            });
        }
        PageResult<UserSelectorResult> page =  mapper.selectJoinPage(pageParam,UserSelectorResult.class,query);
        page.setRecords(buildSelectorResultList(page.getRecords()));
        return page;
    }

    @Override
    public List<UserSelectorResult> getSelectedUserList(List<Long> userIds) {
        List<UserSelectorResult> list = SysUserConvert.INSTANCE.convertSelectorResultList(selectUserListByIds(userIds));
        return buildSelectorResultList(list);
    }

    @Override
    @DataScopeIgnore
    public List<SysUser> selectUserListByIds(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)) return new ArrayList<>();
        return mapper.selectListByIds(ids);
    }


    public List<UserSelectorResult> buildSelectorResultList(List<UserSelectorResult> list){
        if(CollectionUtils.isEmpty(list)) return new ArrayList<>();
        Map<Long,List<String>> userRoleNameMap = getUserRoleNameMap(list.stream().map(UserSelectorResult::getId).collect(Collectors.toList()));

        Map<Long,String> orgNameMap = CollectionUtils.convertMap(
                sysOrgService.selectListByIds(list.stream().map(UserSelectorResult::getOrgId).distinct().collect(Collectors.toList())),
                SysOrg::getId,
                SysOrg::getOrgName
        );

        for (UserSelectorResult result:
           list  ) {
            result.setOrgName(orgNameMap.getOrDefault(result.getId(),""));
            result.setRoleNames(userRoleNameMap.get(result.getId()));
        }
        return list;
    }



    private Map<Long,List<String>> getUserRoleNameMap(List<Long> userIds){
        if(CollectionUtils.isEmpty(userIds)) return new HashMap<>();
        List<SysUserIdentityRoleResult> roleList = userIdentityService.selectRoleInfoByUserIds(userIds);
        List<SysUserIdentity> identityList = userIdentityService.selectListByUserIds(SysUserIdentityTypeEnum.ROLE,userIds);
        Map<Long,String> roleNameMap = CollectionUtils.convertMap(roleList,SysUserIdentityRoleResult::getId,SysUserIdentityRoleResult::getName);

        // 1. 将角色信息预处理为 Map<UserId, List<String>>，方便填充

        return identityList.stream()
                .collect(Collectors.groupingBy(
                        SysUserIdentity::getUserId,
                        Collectors.mapping(
                                idty -> roleNameMap.get(idty.getObjectId()),
                                Collectors.filtering(
                                        StringUtils::isNotBlank, //
                                        Collectors.toList()
                                )
                        )
                ));
    }

    @Override
    @TenantIgnore
    public void syncPasswordByUsername(String username, String password, boolean passwordHashValue) {
        String pwd = passwordHashValue?password: CryptUtils.hashValue(password);
        UpdateChain.of(SysUser.class).eq(SysUser::getUsername,username)
                .set(SysUser::getPassword,pwd).update();
    }

    @Override
    public void updateUserProfileInfo(SysUserUpdateInfoParam sysUserUpdateInfoParam) {
        Long userId = StpLoginUserUtils.getLoginUserId();
        if (!userId.equals(sysUserUpdateInfoParam.getId())) {
            throw new CommonException("禁止修改他人信息");
        }
        SysUser sysUser = this.queryEntity(sysUserUpdateInfoParam.getId());
        // 更新指定字段
        UpdateChain.of(SysUser.class).eq(SysUser::getId, sysUser.getId())
                .set(SysUser::getName, sysUserUpdateInfoParam.getName(), ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getName()))
                .set(SysUser::getNickname, sysUserUpdateInfoParam.getNickname(), ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getNickname()))
                .set(SysUser::getGender, sysUserUpdateInfoParam.getGender(), ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getGender()))
                .set(SysUser::getBirthday, sysUserUpdateInfoParam.getBirthday(), ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getBirthday()))
                .set(SysUser::getEmail, sysUserUpdateInfoParam.getEmail(), ObjectUtil.isNotEmpty(sysUserUpdateInfoParam.getEmail()))
                .update();
        //同步更新自己的登录信息
        StpLoginUserUtils.setLoginUser(getLoginUserById(sysUser.getId()));

    }

    @Override
    public Map<Long, String> getUserNameMapByIds(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) return new HashMap<>();
        LambdaQueryWrapperX<SysUser> query = new LambdaQueryWrapperX<>();
        query.inX(SysUser::getId,userIds).selectX(SysUser::getId,SysUser::getName,SysUser::getNickname,SysUser::getUsername);
        List<SysUser> list = mapper.selectListByQuery(query);
        if(CollectionUtils.isEmpty(list)) return new HashMap<>();
        Map<Long,String> map = new HashMap<>();
        for (SysUser u :list){
            map.put(u.getId(),(StringUtils.firstNotBlank(u.getName(),u.getNickname(),u.getUsername())));
        }
        return map;
    }

    @Override
    public List<SysUserSampleResultDTO> getUserSampleListByIds(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)) return new ArrayList<>();
        LambdaQueryWrapperX<SysUser> query = new LambdaQueryWrapperX<>();
        query.inX(SysUser::getId,userIds).selectX(SysUser::getId,SysUser::getName,
                SysUser::getNickname,SysUser::getUsername,
                SysUser::getAvatar
        );
        return mapper.selectListByQueryAs(query, SysUserSampleResultDTO.class);
    }
}
