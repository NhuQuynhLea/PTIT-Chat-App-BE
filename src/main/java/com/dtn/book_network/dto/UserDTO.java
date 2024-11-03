package com.dtn.book_network.dto;

import com.dtn.book_network.user.aggregate.User;
import org.jilt.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserDTO (UUID publicId,
                       String firstName,
                       String lastName,
                       String email,
                       String imageUrl,
                       Set<AuthorityDTO> authorities) {
    public static UserDTO fromUser(User user) {
        UserDTOBuilder userDTOBuilder = UserDTOBuilder.userDTO();

        if(user.getImageUrl() != null) {
            userDTOBuilder.imageUrl(user.getImageUrl().value());
        }
        return userDTOBuilder
                .firstName(user.getFirstname().value())
                .lastName(user.getLastName().value())
                .publicId(user.getUserPublicId().value())
                .authorities(AuthorityDTO.fromSet(user.getAuthorities()))
                .build();
    }
}
