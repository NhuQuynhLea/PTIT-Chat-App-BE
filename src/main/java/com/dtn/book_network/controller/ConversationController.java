package com.dtn.book_network.controller;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.dto.ConversationDTO;
import com.dtn.book_network.dto.ConversationToCreateDTO;
import com.dtn.book_network.message.aggregate.Conversation;
import com.dtn.book_network.message.aggregate.ConversationToCreate;
import com.dtn.book_network.service.ConversationService;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.shared.service.StatusNotification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//@RequiredArgsConstructor
@RestController
@RequestMapping("api/conversations")
public class ConversationController {
    private final ConversationService conversationService;
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }
    @GetMapping()
    public ResponseEntity<List<ConversationDTO>> getAll(Pageable pageable) {
        List<ConversationDTO> conversationDTOS = conversationService.getAllByConnectedUser(pageable)
                .stream().map(ConversationDTO::from)
                .toList();
        return ResponseEntity.ok(conversationDTOS);
    }

    @PostMapping
    public ResponseEntity<ConversationDTO> create(@RequestBody ConversationToCreateDTO conversationToCreateDTO) {
        ConversationToCreate newConversation = ConversationToCreateDTO.toDomain(conversationToCreateDTO);
        State<Conversation, String> conversationState = conversationService.create(newConversation);
        if(conversationState.getStatusNotification().equals(StatusNotification.OK)) {
            ConversationDTO conversationDTO = ConversationDTO.from(conversationState.getValue());
            return ResponseEntity.ok(conversationDTO);
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Not allowed to create conversation");
            return ResponseEntity.of(problemDetail).build();
        }
    }

    @DeleteMapping
    ResponseEntity<UUID> delete(@RequestParam UUID conversationId) {
        State<ConversationPublicId, String> result = conversationService.delete(new ConversationPublicId(conversationId));
        if(result.getStatusNotification().equals(StatusNotification.OK)) {
            return ResponseEntity.ok(conversationId);
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Not allowed to delete conversation");
            return ResponseEntity.of(problemDetail).build();
        }
    }

    @GetMapping("/get-one-by-public-id")
    ResponseEntity<ConversationDTO> getOneByPublicId(@RequestParam UUID conversationId) {
        Optional<ConversationDTO> conversationDTO = conversationService.getOneByConversationId(new ConversationPublicId(conversationId))
                .map(ConversationDTO::from);
        if(conversationDTO.isPresent()) {
            return ResponseEntity.ok(conversationDTO.get());
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Can not find this conversation");
            return ResponseEntity.of(problemDetail).build();
        }
    }

    @PostMapping("/marked-as-read")
    ResponseEntity<Integer> markAsRead(@RequestParam UUID conversationId) {
        State<Integer, String> readUpdateState = conversationService.markedConversationAsRead(new ConversationPublicId(conversationId));
        return ResponseEntity.ok(readUpdateState.getValue());
    }
}
