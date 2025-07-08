package com.example.chatbot;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Route("")
public class MainView extends VerticalLayout {

    private final ChatBotService chatBotService;
    private final MessageList messageList = new MessageList();
    private final TextField messageInput = new TextField();
    private final List<MessageListItem> messages = new ArrayList<>();


    public MainView(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;

        // Setup the message input field
        messageInput.setPlaceholder("Type your message here...");
        messageInput.setWidth("100%");

        // Setup the send button
        Button sendButton = new Button("Send", event -> sendMessage());
        sendButton.addClickShortcut(Key.ENTER);

        // Add components to the layout
        add(messageList, messageInput, sendButton);

        // Configure layout
        setSizeFull();
        setFlexGrow(1, messageList);
    }

    private void sendMessage() {
        String userMessageText = messageInput.getValue();
        if (userMessageText != null && !userMessageText.isBlank()) {
            // User message
            MessageListItem userMessage = new MessageListItem(
                    userMessageText, Instant.now(), "You");
            userMessage.setUserColorIndex(1);
            messages.add(userMessage);


            // Bot response
            String botResponseText = chatBotService.getResponse(userMessageText);
            MessageListItem botMessage = new MessageListItem(
                    botResponseText, Instant.now(), "ChatBot");
            botMessage.setUserColorIndex(2);
            messages.add(botMessage);

            // Update message list
            messageList.setItems(messages);


            // Clear the input field
            messageInput.clear();
        }
    }
}
