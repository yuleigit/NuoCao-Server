package com.nuocao.common.handler;

import cn.hutool.core.util.StrUtil;
import com.nuocao.common.utils.CommonCryptogramUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Sm4Cbc加解密
 *
 * @author wanglei
 * @date 2022/9/30 15:24
 **/
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CommonSm4CbcTypeHandler<T> extends BaseTypeHandler<T> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, CommonCryptogramUtils.doSm4CbcEncrypt((String) parameter));
    }

    @SuppressWarnings("ALL")
    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String columnValue = rs.getString(columnName);
        //有一些可能是空字符
        return StrUtil.isBlank(columnValue) ? (T) columnValue : (T) CommonCryptogramUtils.doSm4CbcDecrypt(columnValue);
    }

    @SuppressWarnings("ALL")
    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String columnValue = rs.getString(columnIndex);
        return StrUtil.isBlank(columnValue) ? (T) columnValue : (T) CommonCryptogramUtils.doSm4CbcDecrypt(columnValue);
    }

    @SuppressWarnings("ALL")
    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String columnValue = cs.getString(columnIndex);
        return StrUtil.isBlank(columnValue) ? (T) columnValue : (T) CommonCryptogramUtils.doSm4CbcDecrypt(columnValue);
    }
}
