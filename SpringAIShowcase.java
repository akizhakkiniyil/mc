package com.hackathon.accessguardian.mcp.client;

import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringAIShowcase {

    public static void main(String[] args) {
         SpringApplication.run(SpringAIShowcase.class, args);
    }
    @Bean
    public ChatClient chatClient(AzureOpenAiChatModel az) {
        return ChatClient.create(az);
    }

}
