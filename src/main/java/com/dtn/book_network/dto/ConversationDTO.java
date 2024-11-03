package com.dtn.book_network.dto;

import com.dtn.book_network.message.aggregate.Conversation;
import org.jilt.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record ConversationDTO(UUID publicId, String name,
                              List<UserConversationDTO> members,
                              List<MessageDTO> messages) {
    public static ConversationDTO from(Conversation conversation) {
        ConversationDTOBuilder conversationDTOBuilder = ConversationDTOBuilder.conversationDTO()
                .name(conversation.getConversationName().name())
                .publicId(conversation.getConversationPublicId().value())
                .members(conversation.getMembers().stream().map(UserConversationDTO::from).toList());

        if (conversation.getMessages() != null) {
            conversationDTOBuilder.messages(conversation.getMessages().stream().map(MessageDTO::from).toList());
        }
        return conversationDTOBuilder.build();
    }
}
