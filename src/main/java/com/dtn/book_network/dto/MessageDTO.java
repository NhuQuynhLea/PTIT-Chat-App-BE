package com.dtn.book_network.dto;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.aggregate.MessageSendNew;
import com.dtn.book_network.message.aggregate.MessageSendNewBuilder;
import com.dtn.book_network.message.vo.MessageContentBuilder;
import com.dtn.book_network.message.vo.MessageMediaContent;
import com.dtn.book_network.message.vo.MessageSendState;
import com.dtn.book_network.message.vo.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jilt.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageDTO {
    private String textContent;
    private Instant sendDate;
    private MessageSendState state;
    private UUID publicId;
    private UUID conversationId;
    private MessageType type;
    private byte[] mediaContent;
    private String mimeType;
    private UUID senderId;

    public static MessageDTO from(Message message) {
        MessageDTOBuilder messageDTOBuilder = MessageDTOBuilder.messageDTO()
                .textContent(message.getContent().text())
                .sendDate(message.getSentTime().date())
                .state(message.getSendState())
                .publicId(message.getPublicId().value())
                .conversationId(message.getConversationId().value())
                .type(message.getContent().type())
                .senderId(message.getSender().value());

        if (message.getContent().type() != MessageType.TEXT) {
            messageDTOBuilder.mediaContent(message.getContent().media().file())
                    .mimeType(message.getContent().media().mimeType());
        }

        return messageDTOBuilder.build();
    }

    public static List<MessageDTO> from(Set<Message> messages) {
        return messages.stream().map(MessageDTO::from).toList();
    }

    public static MessageSendNew toDomain(MessageDTO restMessage) {
        MessageContentBuilder messageContent = MessageContentBuilder.messageContent()
                .type(restMessage.type)
                .text(restMessage.textContent);

        if (!restMessage.type.equals(MessageType.TEXT)) {
            messageContent.media(new MessageMediaContent(restMessage.mediaContent,
                    restMessage.mimeType));
        }
        return MessageSendNewBuilder.messageSendNew()
                .messageContent(messageContent.build())
                .conversationPublicId(new ConversationPublicId(restMessage.conversationId))
                .build();
    }

    public boolean hasMedia() {
        return !type.equals(MessageType.TEXT);
    }

    public void setAttachment(byte[] file, String contentType) {
        this.mediaContent = file;
        this.mimeType = contentType;
    }
}
