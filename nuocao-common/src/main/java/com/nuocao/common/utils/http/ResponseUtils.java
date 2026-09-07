package com.nuocao.common.utils.http;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.nuocao.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.Context;

import java.io.File;
import java.io.IOException;

/**
 * 通用响应工具类
 * 参考snown CommonDownloadUtil和CommonResponseUtil
 **/
@Slf4j
public class ResponseUtils {

    /**
     * 以流的方式响应错误信息，默认错误消息
     **/
    public static void renderError(Context ctx) throws IOException {
        renderError(ctx, null);
    }

    /**
     * 以流的方式响应错误信息，指定错误消息
     **/
    public static void renderError(Context ctx, String msg) throws IOException {
        renderError(ctx,CommonResult.CODE_ERROR,msg);
    }

    /**
     * 以流的方式响应错误信息，指定错误码和错误消息
     **/
    public static void renderError(Context ctx, Integer code, String msg) throws IOException {
        ctx.charset(CharsetUtil.UTF_8);
        ctx.contentType(ContentType.JSON.toString());
        String json = JSONUtil.toJsonStr(CommonResult.get(code, msg, null));
        ctx.outputAsJson(json);
    }

    /**
     * 下载文件
     *
     * @param file 要下载的文件
     * @param ctx  请求上下文
     */
    public static void download(File file, Context ctx) {
        download(file.getName(), FileUtil.readBytes(file), ctx);
    }

    /**
     * 下载文件
     */
    public static void download(String fileName, byte[] fileBytes, Context ctx) {
        try {
            ctx.headerSet("Content-Disposition", "attachment;filename=" + URLUtil.encode(fileName));
            ctx.headerSet("Content-Length", "" + fileBytes.length);
            ctx.headerSet("Access-Control-Allow-Origin", "*");
            ctx.headerSet("Access-Control-Expose-Headers", "Content-Disposition");
            ctx.contentType("application/octet-stream;charset=UTF-8");
            IoUtil.write(ctx.outputStream(), true, fileBytes);
        } catch (IOException e) {
            log.error(">>> 文件下载异常：", e);
        }
    }
}
