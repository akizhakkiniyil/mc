package com.example.chatbot;

import org.springframework.stereotype.Service;

@Service
public class ChatBotService {

    public String getResponse(String message) {
        if (message == null || message.isBlank()) {
            return "I can't hear you, please say something.";
        }

        String lowerCaseMessage = message.toLowerCase();

        if (lowerCaseMessage.contains("hello") || lowerCaseMessage.contains("hi")) {
            return "Hello there! How can I help you today?";
        } else if (lowerCaseMessage.contains("how are you")) {
            return "I'm just a bot, but I'm doing great! Thanks for asking.";
        } else if (lowerCaseMessage.contains("bye")) {
            return "Goodbye! Have a great day.";
        } else if (lowerCaseMessage.contains("help")) {
            return "You can ask me about the weather, or just chat with me.";
        } else {
            return "I'm not sure how to respond to that. Can you ask me something else?";
        }
    }
}
