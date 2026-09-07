package com.nuocao.core.config.handler;

import cn.dev33.satoken.exception.SaTokenException;
import com.nuocao.common.exception.CommonException;
import com.nuocao.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;
import org.noear.solon.validation.ValidatorException;

/**
 * @author noear 2022/10/4 created
 */
@Slf4j
@Component
public class GlobalErrorFilter implements Filter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        try {
            chain.doFilter(ctx);
        } catch (CommonException e) {
            ctx.render(CommonResult.error(e.getMsg()));
        } catch (ValidatorException e) {
            //验证器异常
            ctx.render(CommonResult.error(e.getMessage()));
        } catch (StatusException e) {
            //4xx 状态类异常
            ctx.status(e.getCode());
            if(e.getCode()==400){
                log.error("400错误：{}", e.getMessage(),e.getCause());
            }
        }catch (SaTokenException e){
            //saToken错误处理，如果是
            ctx.render(CommonResult.error(e.getCode(),e.getMessage()));
        }
        catch (Throwable e) {
            log.error("", e);
                if (e instanceof Exception) {
                CommonResult<String> rst = GlobalExceptionUtil.getCommonResult((Exception) e);
                ctx.render(rst);
            } else {
                ctx.render(CommonResult.error("服务器异常:"+e.getMessage()));
            }
        }
        String str="aaa";
    }
}