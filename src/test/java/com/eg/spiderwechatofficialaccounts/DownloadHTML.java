package com.eg.spiderwechatofficialaccounts;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@SpringBootTest
@Slf4j
public class DownloadHTML {
    @Resource
    private WechatService wechatService;
    @Resource
    private MessageRepository messageRepository;
    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    void crawl() throws Exception {
        int skip = 0;
        List<Message> messageList;
        do {
            Query query = new Query();
            query.skip(skip).limit(100);
            skip += 100;
            messageList = mongoTemplate.find(query, Message.class);
            for (Message message : messageList) {
                JSONObject info = message.getInfo();
                String content_url = info.getJSONObject("app_msg_ext_info")
                        .getString("content_url");
                File file = new File("D:\\2345Downloads\\htmls"
                        + message.getBiz() + "-" + message.getMessageId() + ".html");
                FileUtils.copyURLToFile(new URL(content_url), file);
                System.out.println(content_url);
            }
        } while (CollectionUtil.isNotEmpty(messageList));
    }
}
