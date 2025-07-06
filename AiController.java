package com.hackathon.accessguardian.mcp.client.controller;

import com.hackathon.accessguardian.mcp.client.AiAssistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class AiController {

    @Autowired
    private AiAssistanceService aiAssistanceService;


    @GetMapping("/chat")
    String chatWithAi(@RequestParam String input) {
        System.out.println("input : " + input);
        try {
            return aiAssistanceService.chatWithAi(input);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"error processing chat respinse",e);
        }
    }
}
