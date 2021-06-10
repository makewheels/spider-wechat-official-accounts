package com.eg.spiderwechatofficialaccounts;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class WechatService {
    public JSONObject getAccountMessages(String biz, int offset, String key) {
        String json = HttpUtil.get("https://mp.weixin.qq.com/mp/profile_ext" +
                "?action=getmsg" +
                "&__biz=" + biz + "&f=json&" +
                "offset=" + offset + "&count=10&uin=MTkxMDY3MDExMA%3D%3D" +
                "&key=" + key);
        return JSONObject.parseObject(json);
    }

}
