package com.dtn.book_network.dto;

import com.dtn.book_network.user.aggregate.Authority;
import org.jilt.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record AuthorityDTO(String name) {
    public static Set<AuthorityDTO> fromSet(Set<Authority> authorities) {
        return authorities.stream()
                .map(authority -> AuthorityDTOBuilder.authorityDTO().name(authority.getName().name()).build())
                .collect(Collectors.toSet());
    }
}
