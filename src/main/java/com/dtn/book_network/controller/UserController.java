package com.dtn.book_network.controller;

import com.dtn.book_network.dto.UserDTO;
import com.dtn.book_network.dto.UserSearchDTO;
import com.dtn.book_network.service.UserService;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.vo.UserPublicId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/users")
//@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/get-authenticated-user")
    public ResponseEntity<UserDTO> getAuthenticatedUser(@AuthenticationPrincipal Jwt user,
                                                        @RequestParam boolean forceResync) {
        User authenticatedUser = userService.getAuthenticatedUserWithSync(user, forceResync);
        UserDTO userDTO = UserDTO.fromUser(authenticatedUser);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSearchDTO>> search(Pageable pageable, @RequestParam String query) {
        List<UserSearchDTO> results = userService.search(pageable, query)
                .stream().map(UserSearchDTO::from)
                .toList();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/get-last-seen")
    public ResponseEntity<Instant> getLastSeen(@RequestParam UUID publicId) {
        Optional<Instant> lastSeen = userService.getLastSeen(new UserPublicId(publicId));
        if(lastSeen.isPresent()) {
            return ResponseEntity.ok(lastSeen.get());
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Unavailable to fetch the presence of the user " + publicId);
            return ResponseEntity.of(problemDetail).build();
        }
    }
}
