package com.nuocao.common.handler;

import cn.hutool.core.util.StrUtil;
import com.nuocao.common.utils.CryptUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SM4-CBC 加解密 TypeHandler（String 专用）。
 *
 * <p>写入数据库时自动加密，读取时自动解密；数据库字段类型建议使用 VARCHAR 或 TEXT。</p>
 *
 * <p>说明：原实现带有泛型 {@code <T>} 并额外支持 {@code StringBuilder}/{@code StringBuffer}，
 * 但项目内（SysUser#phone、SysAccount 等）均只用于 {@code String} 字段，泛型从未被使用，
 * 故收敛为 String 专用，减少出错面。若后续确有非 String 需求，再单独扩展。</p>
 */
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CommonSm4CbcTypeHandler extends BaseTypeHandler<String> {

    /**
     * 写入数据库时加密。
     *
     * <p>{@link BaseTypeHandler#setParameter} 仅在参数非 null 时回调本方法，
     * 因此这里无需再做 null 判断。</p>
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, CryptUtils.sm4CbcEncrypt(parameter));
    }

    /**
     * 根据字段名称读取并解密。
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return decrypt(rs.getString(columnName));
    }

    /**
     * 根据字段下标读取并解密。
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return decrypt(rs.getString(columnIndex));
    }

    /**
     * 从存储过程读取并解密。
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return decrypt(cs.getString(columnIndex));
    }

    /**
     * 解密数据库中的密文；空白值（null / ""）原样返回。
     */
    private String decrypt(String cipherText) throws SQLException {
        if (StrUtil.isBlank(cipherText)) {
            return cipherText;
        }
        try {
            return CryptUtils.sm4CbcDecrypt(cipherText);
        } catch (Exception e) {
            throw new SQLException("SM4-CBC decrypt failed", e);
        }
    }
}
