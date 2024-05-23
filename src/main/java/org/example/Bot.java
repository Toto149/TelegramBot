package org.example;

import org.example.chat_gpt_datastructures.chat_gpt_datastructures.ChatGPTService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageReceived = update.getMessage().getText();
        System.out.println(messageReceived);

        // start to evaluate the messages you received
        // 1. Main menu
        if (messageReceived.toLowerCase().startsWith("hello")) {
            sendResponse(chatId, "Welcome to the ChatGPT telegramm integration");
            sendResponse(chatId, "Feel free to ask ChatGPT anything");
            sendResponse(chatId, """
                    1. Begin the question/message with 'chatgpt' to let it get answered by ChatGPT
                    For Example: chatgpt How do I write a Hello World program in Java?
                    Alternatively end the message with a question mark '?'
                   """ );
        }

        if(messageReceived.toLowerCase().startsWith("chatgpt") || messageReceived.endsWith("?")){
            ChatGPTService service = new ChatGPTService();
            sendResponse(chatId,service.postPrompt(messageReceived));


        }
        else

        // 4. analyze the text
        {
            sendResponse(chatId,"");
        }
    }



    private void sendResponse(long chatId, String s) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(s);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken(){
        return System.getenv("BOT_TOKEN");
    }
    @Override
    public String getBotUsername() {
        return System.getenv("BOT_NAME");
    }

}
