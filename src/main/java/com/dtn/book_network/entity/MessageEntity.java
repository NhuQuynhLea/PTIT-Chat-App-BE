package com.dtn.book_network.entity;

import com.dtn.book_network.entity.ConversationEntity;
import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.aggregate.MessageBuilder;
import com.dtn.book_network.message.vo.*;
import com.dtn.book_network.shared.jpa.AbstractAuditingEntity;
import com.dtn.book_network.entity.UserEntity;
import com.dtn.book_network.user.vo.UserPublicId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jilt.Builder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Table(name = "message")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messageSequenceGenerator")
    @SequenceGenerator(name = "messageSequenceGenerator", sequenceName = "message_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, updatable = false)
    private UUID publicId;

    @Column(name = "send_time", nullable = false)
    private Instant sendTime;

    @Column(name = "text", nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MessageType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "send_state")
    private MessageSendState sendState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_fk_sender", nullable = false)
    private UserEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_fk", nullable = false)
    private ConversationEntity conversation;

    @OneToOne
    @JoinColumn(name = "message_binary_content_fk", referencedColumnName = "id")
    private MessageContentBinaryEntity contentBinary;

    public static MessageEntity from(Message message) {
        MessageEntityBuilder messageEntityBuilder = MessageEntityBuilder.messageEntity();

        if (message.getContent().type().equals(MessageType.TEXT)) {
            messageEntityBuilder.text(message.getContent().text());
        } else {
            messageEntityBuilder
                    .contentBinary(MessageContentBinaryEntity.from(message.getContent()));
            if (message.getContent().text() != null) {
                messageEntityBuilder.text(message.getContent().text());
            }
        }

        UserEntity user = UserEntityBuilder.userEntity()
                .publicId(message.getSender().value()).build();

        messageEntityBuilder
                .type(message.getContent().type())
                .publicId(message.getPublicId().value())
                .sendTime(message.getSentTime().date())
                .sendState(message.getSendState())
                .sender(user);

        return messageEntityBuilder.build();
    }

    public static Message toDomain(MessageEntity messageEntity) {
        MessageBuilder messageBuilder = MessageBuilder.message()
                .conversationId(new ConversationPublicId(messageEntity.getPublicId()))
                .sendState(messageEntity.getSendState())
                .sentTime(new MessageSentTime(messageEntity.getSendTime()))
                .sender(new UserPublicId(messageEntity.getSender().getPublicId()))
                .publicId(new MessagePublicId(messageEntity.getPublicId()));

        if (messageEntity.getType().equals(MessageType.TEXT)) {
            MessageContent content = new MessageContent(messageEntity.getText(), MessageType.TEXT, null);
            messageBuilder.content(content);
        } else {
            MessageMediaContent messageMediaContent = new
                    MessageMediaContent(messageEntity.getContentBinary().getFile(),
                    messageEntity.getContentBinary().getFileContentType());
            MessageContent content = new MessageContent(messageEntity.getText(), messageEntity.getType(), messageMediaContent);
            messageBuilder.content(content);
        }

        return messageBuilder.build();
    }

    public static Set<Message> toDomain(Set<MessageEntity> messages) {
        return messages.stream().map(MessageEntity::toDomain).collect(Collectors.toSet());
    }
    @Override
    public Long getId() {
        return this.id;
    }
}
