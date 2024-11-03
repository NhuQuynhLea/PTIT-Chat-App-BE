package com.dtn.book_network.user.repository;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.vo.UserEmail;
import com.dtn.book_network.user.vo.UserPublicId;
import org.jilt.Opt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    void save(User user);
    Optional<User> getOneByEmail(UserEmail email);
    Optional<User> get(UserPublicId publicId);
    List<User> getByPublicIds(Set<UserPublicId> publicIds);
    Optional<User> getOneByPublicId(UserPublicId publicId);
    Page<User> search(Pageable pageable, String query);
    int updateLastSeenByPublicId(UserPublicId publicId, Instant lastSeen);
    List<User> getRecipientByConversationIdExcludingReader(ConversationPublicId conversationPublicId, UserPublicId readerPublicId);
}
