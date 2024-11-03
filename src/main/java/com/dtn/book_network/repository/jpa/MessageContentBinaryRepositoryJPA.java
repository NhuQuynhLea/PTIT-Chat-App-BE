package com.dtn.book_network.repository.jpa;

import com.dtn.book_network.entity.MessageContentBinaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MessageContentBinaryRepositoryJPA extends JpaRepository<MessageContentBinaryEntity, Long> {
}
