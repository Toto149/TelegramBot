package org.example.chat_gpt_datastructures.chat_gpt_datastructures;


import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ChatGPTService {

    private final String chatGPTApiKey = System.getenv("OPEN_AI_API_KEY");
    public String postPrompt(String prompt) {
        ChatGPTResponse response = requireNonNull(
                WebClient.create()
                        .post()
                        .uri("https://api.openai.com/v1/chat/completions")
                        .header("Authorization", "Bearer " + chatGPTApiKey)
                        .bodyValue(new ChatGPTRequest("gpt-3.5-turbo",
                                        List.of(new ChatRequestMessage(
                                                        "user",
                                                        prompt
                                                )
                                        )
                                )
                        )
                        .retrieve()
                        .toEntity(ChatGPTResponse.class)
                        .block()
        ).getBody();

        if(!requireNonNull(response).choices().isEmpty()){
            return response.choices().getFirst().message().content();
        }
        else{
            return "Error: No Response from ChatGPT";
        }
    }




}
