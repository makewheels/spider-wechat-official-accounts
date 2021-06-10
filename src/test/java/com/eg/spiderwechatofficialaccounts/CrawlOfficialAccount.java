package com.eg.spiderwechatofficialaccounts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class CrawlOfficialAccount {
    @Resource
    private WechatService wechatService;

    @Test
    void crawl() {
        String biz = "MjM5MjAwODM4MA==";
        String key = "f86c80e86b7be45ec2330d9327bcda9ac8d36adfcdf1345b2e14b332e1843d28a9c4a1c6605cb0d442edc1ff04d27a01a1efe3017d12be675d49822860f059f0e883317d9fe76ae78e8d73e7d85d70acc64e3dd98172eaa5f80744a77ae79cfc61e9494442365a890f5715f999d144e6ae521990eea4018741b234029db27567";

        JSONObject accountMessages;
        int offset = 0;
        do {
            accountMessages = wechatService.getAccountMessages(biz, offset, key);
            offset += 10;
            JSONArray general_msg_list = accountMessages.getJSONObject("general_msg_list")
                    .getJSONArray("list");
            for (int i = 0; i < general_msg_list.size(); i++) {
                JSONObject jsonObject = general_msg_list.getJSONObject(i);

                JSONObject comm_msg_info = jsonObject.getJSONObject("comm_msg_info");
                Long id = comm_msg_info.getLong("id");

                JSONObject app_msg_ext_info = jsonObject.getJSONObject("app_msg_ext_info");
                String title = app_msg_ext_info.getString("title");
                String content_url = app_msg_ext_info.getString("content_url");
                String cover = app_msg_ext_info.getString("cover");

                System.out.println(id + " " + title);
                System.out.println("content_url = " + content_url);
                System.out.println("cover = " + cover);

            }
        } while (accountMessages.getInteger("can_msg_continue") == 1);
    }
}
