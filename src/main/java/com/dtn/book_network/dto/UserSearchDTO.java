package com.dtn.book_network.dto;

import com.dtn.book_network.user.aggregate.User;
import org.jilt.Builder;

import java.util.UUID;

@Builder
public record UserSearchDTO(UUID publicId,
                            String firstName,
                            String lastName,
                            String email,
                            String imageUrl) {
    public static UserSearchDTO from(User user) {
        UserSearchDTOBuilder userSearchDTOBuilder = UserSearchDTOBuilder.userSearchDTO();
        if (user.getLastName() != null) {
            userSearchDTOBuilder.lastName(user.getLastName().value());
        }

        if (user.getFirstname() != null) {
            userSearchDTOBuilder.firstName(user.getFirstname().value());
        }

        if (user.getImageUrl() != null) {
            userSearchDTOBuilder.imageUrl(user.getImageUrl().value());
        }

        return userSearchDTOBuilder.email(user.getEmail().value())
                .publicId(user.getUserPublicId().value())
                .build();
    }
}
