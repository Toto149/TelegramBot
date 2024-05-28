package org.example;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Conditions {
    private static final String QUIZ = "quiz";
    private static final String CHAT_GPT = "chat-gpt";

    static boolean isTextMessage(Update update){
        return update.hasMessage() && update.getMessage().hasText();
    }
    static boolean isAnswerToQuiz(Update update, State state){
        return update.hasCallbackQuery() && state.equals(State.QUIZ);
    }

    static boolean hasChosenMenuOptions(Update update){
        return update.hasCallbackQuery() && (update.getCallbackQuery().getData().equals(CHAT_GPT))
                || update.getCallbackQuery().getData().equals(QUIZ);
    }
    static boolean isCallForMainMenu(String textMessage){
        return textMessage.toLowerCase().startsWith("hello")
                || textMessage.toLowerCase().startsWith("/start");
    }
    static boolean isCallForQuiz(String textMessage,String mainMenuChoice){
        return textMessage.toLowerCase().startsWith("quiz")
                || mainMenuChoice.equals("quiz");
    }
    static boolean isCallForChatGpt(String testMessage, String mainMenuChoice){
        return testMessage.toLowerCase().startsWith("chatgpt")
                || mainMenuChoice.equals("chat-gpt");
    }

    static boolean isTextMessageEqual(String textMessage,String word){
        return textMessage.toLowerCase().equals(word.toLowerCase());
    }
}
