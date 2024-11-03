package com.dtn.book_network.dto;

import com.dtn.book_network.conversation.ConversationName;
import com.dtn.book_network.message.aggregate.ConversationToCreate;
import com.dtn.book_network.message.aggregate.ConversationToCreateBuilder;
import com.dtn.book_network.user.vo.UserPublicId;
import org.jilt.Builder;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record ConversationToCreateDTO(Set<UUID> members, String name) {
    public static ConversationToCreate toDomain(ConversationToCreateDTO restConversationToCreate) {
        ConversationToCreateDTOBuilder conversationToCreateDTOBuilder = ConversationToCreateDTOBuilder.conversationToCreateDTO();

        Set<UserPublicId> userUUIDs = restConversationToCreate.members
                .stream()
                .map(UserPublicId::new)
                .collect(Collectors.toSet());

        return ConversationToCreateBuilder.conversationToCreate()
                .name(new ConversationName(restConversationToCreate.name()))
                .members(userUUIDs)
                .build();
    }
}
