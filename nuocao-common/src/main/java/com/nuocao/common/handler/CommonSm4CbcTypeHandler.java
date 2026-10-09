package com.nuocao.common.handler;

import cn.hutool.core.util.StrUtil;
import com.nuocao.common.utils.CryptUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SM4-CBC 加解密 TypeHandler。
 *
 * <p>
 * 写入数据库时自动加密，读取数据库时自动解密。
 * 数据库字段类型建议使用 VARCHAR 或 TEXT。
 * </p>
 *
 * <p>
 * 默认适用于 String 类型。如果需要支持其他类型，
 * 需要确保该类型可以通过字符串构造方法还原。
 * </p>
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class CommonSm4CbcTypeHandler<T> extends BaseTypeHandler<T> {

    /**
     * Java 字段类型。
     */
    private final Class<T> javaType;

    /**
     * MyBatis 默认构造方法。
     *
     * <p>
     * 默认按照 String 类型处理。
     * </p>
     */
    @SuppressWarnings("unchecked")
    public CommonSm4CbcTypeHandler() {
        this.javaType = (Class<T>) String.class;
    }

    /**
     * 指定 Java 类型。
     *
     * @param javaType Java 字段类型
     */
    public CommonSm4CbcTypeHandler(Class<T> javaType) {
        if (javaType == null) {
            throw new IllegalArgumentException("javaType cannot be null");
        }

        this.javaType = javaType;
    }

    /**
     * 写入数据库时加密。
     */
    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            T parameter,
            JdbcType jdbcType) throws SQLException {

        if (parameter == null) {
            ps.setNull(i, JdbcType.VARCHAR.TYPE_CODE);
            return;
        }

        String plainText = convertToString(parameter);
        String cipherText = CryptUtils.sm4CbcEncrypt(plainText);

        ps.setString(i, cipherText);
    }

    /**
     * 根据字段名称读取并解密。
     */
    @Override
    public T getNullableResult(
            ResultSet rs,
            String columnName) throws SQLException {

        String columnValue = rs.getString(columnName);
        return decryptAndConvert(columnValue);
    }

    /**
     * 根据字段下标读取并解密。
     */
    @Override
    public T getNullableResult(
            ResultSet rs,
            int columnIndex) throws SQLException {

        String columnValue = rs.getString(columnIndex);
        return decryptAndConvert(columnValue);
    }

    /**
     * 从存储过程读取并解密。
     */
    @Override
    public T getNullableResult(
            CallableStatement cs,
            int columnIndex) throws SQLException {

        String columnValue = cs.getString(columnIndex);
        return decryptAndConvert(columnValue);
    }

    /**
     * 将 Java 对象转换为待加密字符串。
     */
    protected String convertToString(T value) throws SQLException {
        if (value instanceof CharSequence) {
            return value.toString();
        }

        throw new SQLException(
                "CommonSm4CbcTypeHandler only supports String or CharSequence by default. "
                        + "Actual type: " + value.getClass().getName()
        );
    }

    /**
     * 解密并转换为 Java 类型。
     */
    protected T decryptAndConvert(String cipherText) throws SQLException {
        if (StrUtil.isBlank(cipherText)) {
            return convertFromString(cipherText);
        }

        try {
            String plainText = CryptUtils.sm4CbcDecrypt(cipherText);
            return convertFromString(plainText);
        } catch (Exception e) {
            throw new SQLException("SM4-CBC decrypt failed", e);
        }
    }

    /**
     * 将字符串转换为目标 Java 类型。
     */
    @SuppressWarnings("unchecked")
    protected T convertFromString(String value) throws SQLException {
        if (javaType == String.class) {
            return (T) value;
        }

        if (CharSequence.class.isAssignableFrom(javaType)) {
            return createCharSequence(value);
        }

        throw new SQLException(
                "Cannot convert decrypted text to type: "
                        + javaType.getName()
        );
    }

    /**
     * 创建 CharSequence 类型对象。
     */
    @SuppressWarnings("unchecked")
    private T createCharSequence(String value) throws SQLException {
        if (javaType == StringBuilder.class) {
            return (T) new StringBuilder(value);
        }

        if (javaType == StringBuffer.class) {
            return (T) new StringBuffer(value);
        }

        throw new SQLException(
                "Unsupported CharSequence type: " + javaType.getName()
        );
    }
}