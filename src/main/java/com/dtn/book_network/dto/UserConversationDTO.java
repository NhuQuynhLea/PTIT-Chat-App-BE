package com.dtn.book_network.dto;

import com.dtn.book_network.user.aggregate.User;
import org.jilt.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserConversationDTO (String lastName, String firstName,
                                   UUID publicId, String imageUrl,
                                   Instant lastSeen) {
    public static UserConversationDTO from(User user) {
        UserConversationDTOBuilder userConversationDTOBuilder = UserConversationDTOBuilder.userConversationDTO()
                .firstName(user.getFirstname().value())
                .publicId(user.getUserPublicId().value())
                .lastName(user.getLastName().value())
                .lastSeen(user.getLastSeen());
        if(user.getImageUrl() != null) {
            userConversationDTOBuilder.imageUrl(user.getImageUrl().value());
        }
        return userConversationDTOBuilder.build();
    }

}
