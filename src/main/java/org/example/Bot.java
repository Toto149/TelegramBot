package org.example;

import org.example.chat_gpt_datastructures.chat_gpt_datastructures.ChatGPTService;
import org.example.quiz.Question;
import org.example.quiz.Quiz;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



public class Bot extends TelegramLongPollingBot {
    private State state = State.DEFAULT;
    private Quiz quiz = new Quiz();
    private Question question = quiz.getRandomQuestion();
    @Override
    public void onUpdateReceived(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageReceived = update.getMessage().getText();
        System.out.println(messageReceived);



        if(state.equals(State.QUIZ)){
            quit(messageReceived);
            if(hasAnsweredCorrect(messageReceived)) {
                question = quiz.getRandomQuestion();
                return;
            }
            else{
                sendResponse(chatId,"Not correct unfortunately. Please try again or quit by sending a message that starts with quit");
                return;
            }

        }
        if(state.equals(State.CHAT_GPT)){
            chatGPTResponse(chatId, messageReceived);
            return;
        }
        // start to evaluate the messages you received
        // 1. Welcoming text that explains the features in a chat message
        // after a text that starts with hello was send
        if (state.equals(State.DEFAULT) && messageReceived.toLowerCase().startsWith("hello")) {
            helloResponse(chatId);
            return;
        }
        if(state.equals(State.DEFAULT) && messageReceived.toLowerCase().startsWith("quiz")){
            quizStarter(chatId);
            return;
        }
        if(messageReceived.toLowerCase().startsWith("chatgpt")){
            state = State.CHAT_GPT;
            sendResponse(chatId,"You are now in chatgpt mode");
        }
        else //If nothing of the above is done, this will be send.
        {
            sendResponse(chatId,"");
        }
    }

    private boolean hasAnsweredCorrect(String messageReceived) {
        return messageReceived.toLowerCase().startsWith(question.getSolution());
    }


    private void quizStarter(long chatId){
        state = State.QUIZ;
        sendResponse(chatId, question.toString());


    }
    private void helloResponse(long chatId){
        sendResponse(chatId, "Welcome to the ChatGPT telegramm integration");
        sendResponse(chatId, "Feel free to ask ChatGPT anything");
        sendResponse(chatId, """
                    0. You are currently in default mode, apart from messages that start with hello,
                        only the mentioned keyword in the other bulletpoints will have an effect on the bot.
                    1. Type a message starting with 'chatgpt' to get into the chatgpt mode,
                        every following inputs will be send to chatgpt.
                    2. If you send a message starting with 'quiz' you will get into the quiz-mode and
                        a question will appear directly after you send the message. If you answer it successfully
                        you will get another question until you leave the mode.
                        To leave any mode just type a message starting with 'quit'
                   """ );
    }

    private void chatGPTResponse(long chatId, String messageReceived){
        quit(messageReceived);
        ChatGPTService service = new ChatGPTService();
        sendResponse(chatId,service.postPrompt(messageReceived));
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

    private void quit(String messageReceived){
        if(!messageReceived.toLowerCase().startsWith("quit")) return;
        this.state = State.DEFAULT;
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
