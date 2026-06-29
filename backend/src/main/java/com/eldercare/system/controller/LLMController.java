package com.eldercare.system.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import org.springframework.ai.openai.OpenAiChatModel;

/** LLM对话控制器 */
@RestController
@RequestMapping("/ai")
public class LLMController {
    private final OpenAiChatModel chatModel;

    public LLMController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @PostMapping("/chatStream")
    // 流式聊天接口 - 支持JSON请求体
    public Flux<String> generateStream(@RequestBody String prompt) {
        return chatModel.stream(prompt);
    }
}
