package com.nuocao.core.config.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;


/**
 * 全局异常页面处理器，覆盖默认的Whitelabel Error Page
 **/
@Slf4j
@Controller
public class GlobalErrorViewController {

    /**
     * Error页面视图，直接响应JSON
     **/
    @Mapping("/errorView")
    public CommonResult<String> globalError(Context ctx) {
        CommonResult<String> commonResult = new CommonResult<>(404, "路径不存在", null);
        Object model = ctx.attr("model");
        if(ObjectUtil.isNotEmpty(model)) {
            if(model instanceof Exception) {
                if (model instanceof CommonException) {
                    JSONObject errorObj = JSONUtil.parseObj(model);
                    Integer code = errorObj.getInt("code");
                    String msg = errorObj.getStr("msg");
                    if(ObjectUtil.isAllNotEmpty(code, msg)) {
                        commonResult.setCode(code).setMsg(msg);
                    } else if(ObjectUtil.isNotEmpty(msg)) {
                        commonResult = CommonResult.error(msg);
                    } else {
                        commonResult = CommonResult.error();
                    }
                } else {
                    commonResult = CommonResult.error();
                    log.error(">>> 服务器未知异常，具体信息：", (Exception) model);
                }
            }
        }
        return commonResult;
    }
}
