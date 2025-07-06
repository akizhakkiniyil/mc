package com.hackathon.accessguardian.mcp.client;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiAssistanceService {
    private final ChatClient chatClient;
    private  final ToolCallbackProvider mcpToolCallbackProvider;

    public String chatWithAi(String input) throws Exception {
        String response = chatClient.prompt().user(input).toolCallbacks(mcpToolCallbackProvider.getToolCallbacks()).call().content();
        System.out.println("AI response : " + response);
        return response;
    }
}
