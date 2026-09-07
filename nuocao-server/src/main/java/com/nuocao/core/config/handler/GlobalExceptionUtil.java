package com.nuocao.core.config.handler;

import cn.dev33.satoken.exception.SaTokenException;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.language.LanguageUtils;
import com.nuocao.common.pojo.CommonResult;
import com.nuocao.common.utils.http.RequestUtils;
import com.nuocao.common.utils.StrFormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;

/**
 * 全局异常处理工具类，将异常转为通用结果
 */
@Slf4j
public class GlobalExceptionUtil {

    /**
     * 根据错误类型获取对应的CommonResult
     **/
    public static CommonResult<String> getCommonResult(Exception e) {
        CommonResult<String> commonResult;
        if (e instanceof SaTokenException) {
            // 如果是SaToken相关异常，则由AuthExceptionUtil处理
            return CommonResult.error(LanguageUtils.getMessage("common.auth.permission.error",e.getMessage()));  //AuthExceptionUtil.getCommonResult(e);
        }  else if (e instanceof CommonException) {
            // 通用业务异常，直接返回给前端
            CommonException commonException = (CommonException) e;
            commonResult = CommonResult.get(commonException.getCode(), commonException.getMsg(), null);
        }  else {
            // 未知异常打印详情
            log.error(">>> 服务器未知异常，请求地址：{}，具体信息：", RequestUtils.getCurrentRequest().url(), e);
            // 未知异常返回服务器异常
            if("pro".equals(Solon.cfg().env())) {
                commonResult = CommonResult.error(LanguageUtils.getMessage("common.request.error"));
            }else
            {
                commonResult = CommonResult.error(StrFormatUtils.doFormat("{}===>{}", LanguageUtils.getMessage("common.request.error"),e.getMessage()));
            }
        }
        return commonResult;
    }
}
