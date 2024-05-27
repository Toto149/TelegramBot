package org.example;

import org.example.chat_gpt_datastructures.chat_gpt_datastructures.ChatGPTService;
import org.example.quiz.Question;
import org.example.quiz.Quiz;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.util.ArrayList;
import java.util.List;


public class Bot extends TelegramLongPollingBot {

    private State state = State.DEFAULT;
    private Quiz quiz = new Quiz();
    private Question question = quiz.getRandomQuestion();




    @Override
    public void onUpdateReceived(Update update) {

        long chatId = 0;

        String answerToQuestion = "";

        String messageReceived = "";

        String startUpMessage = "";

        boolean hasChosenMenuOptions = update.hasCallbackQuery() && (update.getCallbackQuery().getData().equals("chat-gpt")
                                                                 || update.getCallbackQuery().getData().equals("quiz"));
        boolean isTextMessage = update.hasMessage() && update.getMessage().hasText();

        boolean isAnswerToQuiz = update.hasCallbackQuery() && state.equals(State.QUIZ);
        
        if(hasChosenMenuOptions){

            chatId = update.getCallbackQuery().getMessage().getChatId();
            startUpMessage = update.getCallbackQuery().getData();
        }
        if(isAnswerToQuiz){

            chatId = update.getCallbackQuery().getMessage().getChatId();
            answerToQuestion = update.getCallbackQuery().getData();
        }

        if(isTextMessage) {

            chatId = update.getMessage().getChatId();
            messageReceived = update.getMessage().getText();
        }

        boolean isCallForMainMenu =  (messageReceived.toLowerCase().startsWith("hello")
                || messageReceived.toLowerCase().startsWith("/start"));

        boolean isCallForQuiz = messageReceived.toLowerCase().startsWith("quiz") || startUpMessage.equals("quiz");

        boolean isCallForChatGpt = messageReceived.toLowerCase().startsWith("chatgpt") || startUpMessage.equals("chat-gpt");

        if(state.equals(State.QUIZ)){

            quit(messageReceived);

            if(hasAnsweredCorrect(answerToQuestion)) {

                question = quiz.getRandomQuestion();

                sendResponse(chatId, "Correct!");
                sendResponse(chatId,question.toString());
                sendInlineQuizMenu(chatId);
                return;
            }
            else{
                sendResponse(chatId,"Not correct unfortunately. Please try again or if you dont want to play anymore " +
                        "quit by sending a message that starts with quit");
                return;
            }

        }
        if(state.equals(State.CHAT_GPT)){

            chatGPTResponse(chatId, messageReceived);
            return;
        }

        // /start to evaluate the messages you received
        // 1. Welcoming text that explains the features in a chat message
        // after a text that starts with hello got send
        if (isCallForMainMenu) {

            helloResponse(chatId);

            return;
        }

        if(isCallForQuiz){

            quizStarter(chatId);
            return;
        }

        if(isCallForChatGpt){

            state = State.CHAT_GPT;
            sendResponse(chatId,"You are now in chatgpt mode");
        } else //If nothing of the above is done, this will get send.
        {
            sendResponse(chatId,"Write 'hello' or '/start' to open the main-menu");
        }
    }

    ////


    boolean hasAnsweredCorrect(String answer) {
        return answer.equals(question.getSolution());
    }


    void quizStarter(long chatId){
        state = State.QUIZ;
        sendResponse(chatId, question.toString());
        sendInlineQuizMenu(chatId);
    }

    private void sendInlineQuizMenu(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose an answer: ");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText(" A ");
        btn1.setCallbackData(question.getOptions().getFirst());
        rowInline.add(btn1);

        InlineKeyboardButton btn2 = new InlineKeyboardButton();
        btn2.setText(" B ");
        btn2.setCallbackData(question.getOptions().get(1));
        rowInline.add(btn2);

        InlineKeyboardButton btn3 = new InlineKeyboardButton();
        btn3.setText(" C ");
        btn3.setCallbackData(question.getOptions().get(2));
        rowInline2.add(btn3);

        InlineKeyboardButton btn4 = new InlineKeyboardButton();
        btn4.setText(" D ");
        btn4.setCallbackData(question.getOptions().get(3));
        rowInline2.add(btn4);

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try{
            execute(message);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    void helloResponse(long chatId){
        sendResponse(chatId, """
                    0. You are currently in default mode, apart from messages that start with hello,
                        only the mentioned keyword in the other bullet-points will have an effect on the bot.
                    1. Type a message starting with 'chatgpt' to get into the chatgpt mode,
                        every following inputs will be send to chatgpt.
                    2. If you send a message starting with 'quiz' you will get into the quiz-mode and
                        a question will appear directly after you send the message. If you answer it successfully
                        you will get another question until you leave the mode.
                        To leave any mode just type a message starting with 'quit'
                   """ );
        sendStartMenu(chatId);
    }

    void chatGPTResponse(long chatId, String messageReceived){
        if(messageReceived == null) return;
        quit(messageReceived);
        ChatGPTService service = new ChatGPTService();
        sendResponse(chatId,service.postPrompt(messageReceived));
    }
    void quit(String messageReceived){
        if(messageReceived == null) return;
        if(!messageReceived.toLowerCase().startsWith("quit")) return;
        this.state = State.DEFAULT;
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

    private void sendStartMenu(long chatId){

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Choose an option: ");

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

        InlineKeyboardButton btn1 = new InlineKeyboardButton();
        btn1.setText("Quiz");
        btn1.setCallbackData("quiz");
        rowInline.add(btn1);

        InlineKeyboardButton btn2 = new InlineKeyboardButton();
        btn2.setText("Chat-Gpt");
        btn2.setCallbackData("chat-gpt");
        rowInline2.add(btn2);

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        try{
            execute(message);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }



    //Methods mainly for test-purposes, that's why the visibility is default.

    State getState(){
        return this.state;
    }

    Quiz getQuiz(){
        return this.quiz;
    }

    Question getQuestion(){
        return this.question;
    }

    void setState(State state){
        this.state = state;
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
