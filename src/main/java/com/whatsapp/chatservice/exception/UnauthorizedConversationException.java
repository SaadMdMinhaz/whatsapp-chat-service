package com.whatsapp.chatservice.exception;

public class UnauthorizedConversationException extends RuntimeException {
    public UnauthorizedConversationException(String message) {
        super(message);
    }
}
