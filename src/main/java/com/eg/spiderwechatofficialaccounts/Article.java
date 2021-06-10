package com.eg.spiderwechatofficialaccounts;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Article {
    @Id
    private String _id;

}
