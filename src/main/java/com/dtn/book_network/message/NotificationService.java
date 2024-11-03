package com.dtn.book_network.message;

import com.dtn.book_network.service.UserService;
import com.dtn.book_network.user.aggregate.User;
import com.dtn.book_network.user.vo.UserEmail;
import com.dtn.book_network.user.vo.UserPublicId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;

@Service
public class NotificationService {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private Map<String, SseEmitter> emitters = new HashMap<>();
    public NotificationService(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedRate = 5000)
    public void heartBeat() throws IOException {
        for(Map.Entry<String, SseEmitter> sseEmitter : emitters.entrySet()) {
            try {
                sseEmitter.getValue().send(SseEmitter.event()
                        .name("heartbeat")
                        .id(sseEmitter.getKey())
                        .data("Check heartbeat..."));
                this.userService.updatePresence(new UserPublicId(UUID.fromString(sseEmitter.getKey())));
            } catch (IllegalStateException e) {
                logger.info("remove this one from the map {}", sseEmitter.getKey());
                this.emitters.remove(sseEmitter.getKey());
            }
        }
    }

    public SseEmitter addEmitter(UserEmail userEmail) {
        Optional<User> userByEmail = userService.getOneByEmail(userEmail.value());
        if (userByEmail.isPresent()) {
            logger.info("new emitter added to list {}", userEmail.value());
            SseEmitter newEmitter = new SseEmitter(60000L);
            try {
                UUID userId = userByEmail.get().getUserPublicId().value();
                newEmitter.send(SseEmitter.event().id(userId.toString()).data("Starting connection..."));
                this.emitters.put(userId.toString(), newEmitter);
                return newEmitter;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void sendMessage(Object message,
                            List<UserPublicId> usersToNotify,
                            NotificationEventName notificationEventName) {
        for(UserPublicId userId : usersToNotify) {
            String userUUIDRaw = userId.value().toString();
            if(this.emitters.containsKey(userUUIDRaw)) {
                logger.info("located userpublicid, let send him message : {}", userUUIDRaw);
                SseEmitter sseEmitter = this.emitters.get(userUUIDRaw);
                try {
                    sseEmitter.send(SseEmitter.event()
                            .name(notificationEventName.value)
                            .id(userUUIDRaw)
                            .data(message));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
