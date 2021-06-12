package com.eg.spiderwechatofficialaccounts;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class DownloadHTML {
    @Resource
    private WechatService wechatService;
    @Resource
    private MessageRepository messageRepository;
    @Resource
    private MongoTemplate mongoTemplate;

    @Value("${workdir}")
    private String workdir;

    void handleMessage(Message message) {
        JSONObject info = message.getInfo();
        JSONObject app_msg_ext_info = info.getJSONObject("app_msg_ext_info");
        String content_url = app_msg_ext_info.getString("content_url");
        System.out.println(content_url);
        File file = new File(workdir, "htmls\\"
                + message.getBiz() + "-" + message.getMessageId() + ".html");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        //如果不是文章类型，跳过
        //如果文章已经被封了，跳过
        if (StringUtils.isEmpty(content_url) || app_msg_ext_info.getInteger("del_flag") == 4) {
            return;
        }
        HttpUtil.downloadFile(content_url, file);
        Document document = Jsoup.parse(FileUtil.readUtf8String(file));
        List<Element> imgList = document.getElementsByTag("img").stream()
                .filter(img -> img.hasAttr("data-src"))
                .collect(Collectors.toList());
        for (Element element : imgList) {
            String src = element.attr("data-src");
            System.out.println(src);
            String wx_fmt = "";
            if (src.contains("wx_fmt")) {
                wx_fmt = UrlQuery.of(src, StandardCharsets.UTF_8).get("wx_fmt").toString();
            } else {
                wx_fmt = src.substring(src.lastIndexOf(".") + 1);
            }
            String filename = "tmp-" + IdUtil.objectId() + "." + wx_fmt;
            File imageFile = new File(workdir, "htmls/tmp/" + filename);
            //确保有图片的文件夹
            if (!imageFile.getParentFile().exists())
                imageFile.getParentFile().mkdirs();
            //执行下载
            HttpUtil.downloadFile(src, imageFile);
            //按照md5命名
            String newFilename = SecureUtil.md5(imageFile)
                    + "." + FilenameUtils.getExtension(imageFile.getName());
            File finalImage = new File(imageFile.getParentFile().getParentFile()
                    + "/imgs", newFilename);
            //如果不存在，复制过去
            if (!finalImage.exists()) {
                FileUtil.copyFile(imageFile, finalImage);
            }
            imageFile.delete();
            element.attr("src", "imgs/" + finalImage.getName());
        }
        FileUtil.writeUtf8String(document.html(), file);
    }

    @Test
    void crawl() {
        int skip = 0;
        List<Message> messageList;
        do {
            Query query = new Query();
            query.skip(skip).limit(100);
            skip += 100;
            messageList = mongoTemplate.find(query, Message.class);
            for (Message message : messageList) {
                handleMessage(message);
            }
        } while (CollectionUtil.isNotEmpty(messageList));
    }
}
