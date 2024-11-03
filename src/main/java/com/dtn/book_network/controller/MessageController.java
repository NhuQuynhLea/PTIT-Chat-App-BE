package com.dtn.book_network.controller;

import com.dtn.book_network.dto.MessageDTO;
import com.dtn.book_network.message.aggregate.Message;
import com.dtn.book_network.message.aggregate.MessageSendNew;
import com.dtn.book_network.service.MessageService;
import com.dtn.book_network.shared.service.State;
import com.dtn.book_network.shared.service.StatusNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/messages")
//@RequiredArgsConstructor
public class MessageController {
    private ObjectMapper mapper = new ObjectMapper();
    private final MessageService messageService;
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDTO> send(@RequestPart(value = "file", required = false)MultipartFile file,
                                           @RequestPart("dto") String messageRaw) throws IOException {
        MessageDTO messageDTO = mapper.readValue(messageRaw, MessageDTO.class);
        if(messageDTO.hasMedia()) {
            messageDTO.setAttachment(file.getBytes(), file.getContentType());
        }
        MessageSendNew messageSendNew = MessageDTO.toDomain(messageDTO);
        State<Message, String> sendState = messageService.send(messageSendNew);
        if(sendState.getStatusNotification().equals(StatusNotification.OK)) {
            return ResponseEntity.ok(MessageDTO.from(sendState.getValue()));
        } else {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, sendState.getError());
            return ResponseEntity.status(problemDetail.getStatus()).body(messageDTO);
        }
    }

}
