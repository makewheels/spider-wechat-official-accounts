package com.eg.spiderwechatofficialaccounts;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Message {
    @Id
    private String _id;

    @Indexed
    private String biz;
    @Indexed
    private Long messageId;
    private JSONObject info;
}
