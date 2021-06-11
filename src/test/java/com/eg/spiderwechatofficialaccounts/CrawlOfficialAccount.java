package com.eg.spiderwechatofficialaccounts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class CrawlOfficialAccount {
    @Resource
    private WechatService wechatService;
    @Resource
    private MessageRepository messageRepository;

    void handleMessage(String biz, JSONObject jsonObject) {
        JSONObject comm_msg_info = jsonObject.getJSONObject("comm_msg_info");
        Long messageId = comm_msg_info.getLong("id");

        Message findMessage = messageRepository.findByBizAndMessageId(biz, messageId);
        if (findMessage != null) {
            log.info("skip: biz = {}, messageId = {}, message = {}",
                    biz, messageId, JSON.toJSONString(findMessage));
            return;
        }

        JSONObject app_msg_ext_info = jsonObject.getJSONObject("app_msg_ext_info");
        String title = app_msg_ext_info.getString("title");
        String content_url = app_msg_ext_info.getString("content_url");
        String cover = app_msg_ext_info.getString("cover");

        System.out.println(title);
        System.out.println("content_url = " + content_url);
        System.out.println("cover = " + cover);

        Message message = new Message();
        message.setBiz(biz);
        message.setMessageId(messageId);
        message.setInfo(jsonObject);
        messageRepository.save(message);
    }

    @Test
    void crawl() {
        String biz = "MjM5MjAwODM4MA==";
        String key = "2a2066ee7128965bd897901503e3a95344789eba9be06b27b75f749d5b35decf98d13a1d35751b329e81e45f03bb80b33c01241dbae41b107d1e403a4f6e84f18e23ccdf26479562a3fe5e02b7b09582a2c74f4a20760bbaac32704082077dd0aad17fa42babc7e1f8e605e9efb84cfa33dc03c8b4d2c5274116297e8d03c494";

        JSONObject accountMessages;
        int offset = 0;
        do {
            accountMessages = wechatService.getAccountMessages(biz, offset, key);
            offset += 10;
            JSONArray general_msg_list = accountMessages.getJSONObject("general_msg_list")
                    .getJSONArray("list");
            for (int i = 0; i < general_msg_list.size(); i++) {
                JSONObject jsonObject = general_msg_list.getJSONObject(i);
                handleMessage(biz, jsonObject);
            }
        } while (accountMessages.getInteger("can_msg_continue") == 1);
    }
}
