package com.nuocao.dev.modular.email.provider;

import cn.hutool.json.JSONObject;
import nuocao.plugin.dev.api.DevEmailApi;
import org.noear.solon.annotation.Component;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class DevEmailApiProvider implements DevEmailApi {
    @Override
    public void sendDynamicTxtEmail(String tos, String subject, String content) {

    }

    @Override
    public void sendDynamicHtmlEmail(String tos, String subject, String content) {

    }

    @Override
    public void sendTextEmailLocal(String tos, String subject, String content, List<File> files) {

    }

    @Override
    public void sendHtmlEmailLocal(String tos, String subject, String content, Map<String, InputStream> imageMap, List<File> files) {

    }

    @Override
    public void sendTextEmailAliyun(String from, String user, String tos, String subject, String content) {

    }

    @Override
    public void sendHtmlEmailAliyun(String from, String user, String tos, String subject, String content) {

    }

    @Override
    public void sendEmailWithTemplateAliyun(String from, String tagName, String toName, String templateName) {

    }

    @Override
    public void sendTextEmailTencent(String from, String user, String tos, String subject, String content, List<JSONObject> attachmentList) {

    }

    @Override
    public void sendHtmlEmailTencent(String from, String user, String tos, String subject, String content, List<JSONObject> attachmentList) {

    }

    @Override
    public void sendEmailWithTemplateTencent(String from, String user, String toId, String templateId, String templateParam, String subject, List<JSONObject> attachmentList) {

    }
}
