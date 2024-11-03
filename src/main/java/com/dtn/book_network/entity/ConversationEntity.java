package com.dtn.book_network.entity;

import com.dtn.book_network.entity.MessageEntity;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.ConversationBuilder;
import com.dtn.book_network.entity.UserEntity;
import com.dtn.book_network.message.aggregate.ConversationToCreate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.jilt.Builder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "conversation")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conversationSequenceGenerator")
    @SequenceGenerator(name = "conversationSequenceGenerator", sequenceName = "conversation_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @UuidGenerator
    @Column(name = "public_id", nullable = false, updatable = false)
    private UUID publicId;

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "conversation")
    private Set<MessageEntity> messages = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_conversation",
            joinColumns = {@JoinColumn(name = "conversation_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}
    )
    private Set<UserEntity> users = new HashSet<>();

    public static ConversationEntity from(Conversation conversation) {
        ConversationEntityBuilder conversationEntityBuilder = ConversationEntityBuilder.conversationEntity();

        if (conversation.getDbId() != null) {
            conversationEntityBuilder.id(conversation.getDbId());
        }

        if (conversation.getConversationName() != null) {
            conversationEntityBuilder.name(conversation.getConversationName().name());
        }

        if (conversation.getConversationPublicId() != null) {
            conversationEntityBuilder.publicId(conversation.getConversationPublicId().value());
        }

        if (conversation.getMessages() != null) {
            Set<MessageEntity> messages = conversation.getMessages()
                    .stream().map(MessageEntity::from).collect(Collectors.toSet());
            conversationEntityBuilder.messages(messages);
        }

        conversationEntityBuilder.users(UserEntity.from(conversation.getMembers().stream().toList()));

        return conversationEntityBuilder.build();
    }

    public static Conversation toDomain(ConversationEntity conversation) {
        ConversationBuilder conversationEntityBuilder = ConversationBuilder
                .conversation()
                .conversationPublicId(new com.dtn.book_network.conversation.ConversationPublicId(conversation.getPublicId()))
                .conversationName(new com.dtn.book_network.conversation.ConversationName(conversation.getName()))
                .members(UserEntity.toDomain(conversation.getUsers()))
                .dbId(conversation.getId());

        if (conversation.getMessages() != null) {
            conversationEntityBuilder.messages(MessageEntity.toDomain(conversation.getMessages()));
        }

        return conversationEntityBuilder.build();
    }

    public static ConversationEntity from(ConversationToCreate conversation) {
        ConversationEntityBuilder conversationEntityBuilder = ConversationEntityBuilder.conversationEntity();

        if (conversation.getName() != null) {
            conversationEntityBuilder.name(conversation.getName().name());
        }

        return conversationEntityBuilder.build();
    }

}
