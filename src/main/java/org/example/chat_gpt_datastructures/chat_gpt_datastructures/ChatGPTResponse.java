package org.example.chat_gpt_datastructures.chat_gpt_datastructures;

import java.util.List;

public record ChatGPTResponse(List<ChatGPTResponseChoice> choices) {
}
