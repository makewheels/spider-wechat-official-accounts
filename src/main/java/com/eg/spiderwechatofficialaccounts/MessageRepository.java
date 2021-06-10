package com.eg.spiderwechatofficialaccounts;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    Message findByBizAndMessageId(String biz, Long messageId);
}
