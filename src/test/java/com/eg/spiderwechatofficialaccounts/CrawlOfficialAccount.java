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
        message.setInfo(jsonObject);
        messageRepository.save(message);
    }

    @Test
    void crawl() {
        String biz = "MjM5MjAwODM4MA==";
        String key = "2a2066ee7128965b57f54f726da9da8e7265ab6fd9486c247267624bceef3cadd4f0d5cf7299726220e51b9e699913545f1b322ce9705fe4692c3bdf79d3213563ee9be17c7ecb3efda68e9f4ae7c8519a982c26edabc08a70e5cba370a718b7df320c371066a5c34a067242e4635acf8e7af0a14ac3f073662d52309e0e1f87";

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
