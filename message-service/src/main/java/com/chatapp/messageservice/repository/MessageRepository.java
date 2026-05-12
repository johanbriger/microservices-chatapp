package com.chatapp.messageservice.repository;

import com.chatapp.messageservice.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    // Vi lägger till en extra hjälpreda för att kunna hämta meddelanden i kronologisk ordning senare!
    List<MessageEntity> findAllByOrderByTimestampAsc();
}