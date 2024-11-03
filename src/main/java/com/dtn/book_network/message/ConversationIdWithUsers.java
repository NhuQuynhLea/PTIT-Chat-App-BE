package com.dtn.book_network.message;

import com.dtn.book_network.conversation.ConversationPublicId;
import com.dtn.book_network.user.vo.UserPublicId;

import java.util.List;
import java.util.UUID;

public record ConversationIdWithUsers (ConversationPublicId conversationId,
                                       List<UserPublicId> users){
}
