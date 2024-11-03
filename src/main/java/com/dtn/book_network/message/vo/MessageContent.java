package com.dtn.book_network.message.vo;

import org.jilt.Builder;

@Builder
public record MessageContent(String text, MessageType type, MessageMediaContent media) {
}