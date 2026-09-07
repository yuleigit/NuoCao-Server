package com.nuocao.common.utils.imageload;

/**
 * 图片处理器函数式接口
 */
@FunctionalInterface
public interface ImageProcessor {
    /**
     * 处理图片结果
     *
     * @param imageResult 图片加载结果
     * @throws Exception 处理过程中可能抛出异常
     */
    void process(ImageLoadResult imageResult) throws Exception;
}