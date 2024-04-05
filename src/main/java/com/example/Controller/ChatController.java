package com.example.Controller;

import com.gearwenxin.client.ernie.ErnieBotClient;
import com.gearwenxin.entity.request.PromptRequest;
import com.gearwenxin.entity.response.ChatResponse;
import com.gearwenxin.entity.response.PromptResponse;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ChatController {

    // 要调用的模型的客户端
    @Resource
    private ErnieBotClient ernieBotClient;

    // 单次对话（阻塞）
    @PostMapping("/chat")
    public BaseRsponse<ChatResponse> chatSingle(String msg) {
        return ernieBotClient.chatSingle(msg).block();
    }

    // 单次对话
    @PostMapping("/chat")
    public Mono<ChatResponse> chatSingle(String msg) {
        return ernieBotClient.chatSingle(msg);
    }

    // 连续对话
    @PostMapping("/chats")
    public Mono<ChatResponse> chatCont(String msg) {
        String chatUID = "test-user-1001";
        return ernieBotClient.chatCont(msg, chatUID);
    }

    // 流式返回，单次对话
    @GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatSingleStream(@RequestParam String msg) {
        Flux<ChatResponse> chatResponse = ernieBotClient.chatSingleOfStream(msg);

        return chatResponse.map(response -> "data: " + response.getResult() + "\n\n");
    }

    // 流式返回，连续对话
    @GetMapping(value = "/stream/chats", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatContStream(@RequestParam String msg, @RequestParam String msgUid) {
        Flux<ChatResponse> chatResponse = ernieBotClient.chatContOfStream(msg, msgUid);

        return chatResponse.map(response -> "data: " + response.getResult() + "\n\n");
    }

    // 模板对话
    @PostMapping("/prompt")
    public Mono<PromptResponse> chatSingle() {
        Map<String, String> map = new HashMap<>();
        map.put("article", "我看见过波澜壮阔的大海，玩赏过水平如镜的西湖，却从没看见过漓江这样的水。漓江的水真静啊，静得让你感觉不到它在流动。");
        map.put("number", "20");
        PromptRequest promptRequest = new PromptRequest();
        promptRequest.setId(1234);
        promptRequest.setParamMap(map);

        return promptBotClient.chatPrompt(promptRequest);
    }

}
