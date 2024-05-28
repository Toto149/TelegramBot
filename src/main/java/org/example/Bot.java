package org.example;


import org.example.chat_gpt_datastructures.chat_gpt_datastructures.ChatGPTService;

import org.example.menu.MainMenu;
import org.example.menu.QuizMenu;

import org.example.quiz.Question;
import org.example.quiz.Quiz;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


public class Bot extends TelegramLongPollingBot {

    private State state = State.DEFAULT;
    private Quiz quiz = new Quiz();
    private Question question = quiz.getRandomQuestion();




    @Override
    public void onUpdateReceived(Update update) {

        long chatId = 0;

        //Different messages for different purposes.

        String answerToQuestion = "";
        String textMessage = "";
        String mainMenuChoice = "";

        boolean hasChosenMenuOptions = update.hasCallbackQuery() && (update.getCallbackQuery().getData().equals("chat-gpt")
                                        || update.getCallbackQuery().getData().equals("quiz"));
        boolean isTextMessage = update.hasMessage() && update.getMessage().hasText();
        boolean isAnswerToQuiz = update.hasCallbackQuery() && state.equals(State.QUIZ);

        if(hasChosenMenuOptions){

            chatId = update.getCallbackQuery().getMessage().getChatId();
            mainMenuChoice = update.getCallbackQuery().getData();
        }
        if(isAnswerToQuiz){

            chatId = update.getCallbackQuery().getMessage().getChatId();
            answerToQuestion = update.getCallbackQuery().getData();
        }

        if(isTextMessage) {

            chatId = update.getMessage().getChatId();
            textMessage = update.getMessage().getText();
        }

        boolean isCallForMainMenu =  (textMessage.toLowerCase().startsWith("hello")
                                        || textMessage.toLowerCase().startsWith("/start"));

        boolean isCallForQuiz = textMessage.toLowerCase().startsWith("quiz") || mainMenuChoice.equals("quiz");

        boolean isCallForChatGpt = textMessage.toLowerCase().startsWith("chatgpt") || mainMenuChoice.equals("chat-gpt");




        quit(textMessage);

        if(state.equals(State.QUIZ)){

            if(hasAnsweredCorrect(answerToQuestion)) {
                onCorrectAnswer(chatId,answerToQuestion);
                return;
            } else{
                onWrongAnswer(chatId,answerToQuestion);
                return;
            }

        }
        if(state.equals(State.CHAT_GPT)){

            chatGPTResponse(chatId, textMessage);
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
        }
        if(state.equals(State.DEFAULT)){
            sendStartMenu(chatId);
        }
        else //If nothing of the above is done, this will get send.
        {
            sendResponse(chatId,"Write 'hello' or '/start' to open the main-menu");
        }
    }

    ////


    boolean hasAnsweredCorrect(String answer) {
        return answer.equals(question.getSolution());
    }


    private void quizStarter(long chatId){
        state = State.QUIZ;
        sendResponse(chatId, question.toString());
        sendInlineQuizMenu(chatId);
    }

    private void sendInlineQuizMenu(long chatId){
        QuizMenu quizMenu= new QuizMenu(chatId, question);

        SendMessage quizMenuMessage = quizMenu.getSendQuizMenu();

        try{
            execute(quizMenuMessage);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

   private void onCorrectAnswer(long chatId,String answerToQuestion){
       quiz.increaseScore();
       String previousQuestionText = question.toString();
       question = quiz.getRandomQuestion();


       sendResponse(chatId, "Glückwunsch du bist bei "+ quiz.getMoneyWon() + "! Auf zur nächsten Frage!");
       chatGPTResponse(chatId, "Versetze dich in der Rolle von Günther Jauch als Moderator der Sendung \"Wer Wird Millionär\" und mache eine der für ihn typische Bemerkungen" +
               "zu der folgenden Frage und der Kandidat hat die korrekte Antwort gegeben. Bitte in seinem typischen Stil einen Kommentar geben " + "Frage :" + previousQuestionText + " Antwort des Kandidaten " + answerToQuestion + " erwähne auch den aktuellen Stand: (e.g. sie sind bei xx€) " + quiz.getMoneyWon());
       sendResponse(chatId,question.toString());
       sendInlineQuizMenu(chatId);
   }

   private void onWrongAnswer(long chatId, String answerToQuestion){
       if (quiz.getScore() > 10) {
           chatGPTResponse(chatId,"Versetze dich in der Rolle von Günther Jauch als Moderator der Sendung \"Wer Wird Millionär\" und mache eine der für ihn typische Bemerkungen" +
                   "das der Kandidat eine falsche Antwort gab und gebe eine Abmoderation für den Kandidaten. Der Kandidat hat 16.000€ eingespielt");
           sendResponse(chatId, "Wenn du das Quiz beenden möchtest tippe 'quit' in den Chat" );
       } else {
           if (quiz.getScore() > 5) {
               chatGPTResponse(chatId,"Versetze dich in der Rolle von Günther Jauch als Moderator der Sendung \"Wer Wird Millionär\" und mache eine der für ihn typische Bemerkungen" +
                       "das der Kandidat eine falsche Antwort gab und gebe eine Abmoderation für den Kandidaten. Der Kandidat hat 500€ eingespielt");
               sendResponse(chatId, "Wenn du das Quiz beenden möchtest tippe 'quit' in den Chat" );
           } else {
               chatGPTResponse(chatId,"Versetze dich in der Rolle von Günther Jauch als Moderator der Sendung \"Wer Wird Millionär\" und mache eine der für ihn typische Bemerkungen" +
                       "das der Kandidat eine falsche Antwort gab und gebe eine Abmoderation für den Kandidaten. Der Kandidat ist bei einen der ersten 5 Fragen gescheitert");
               sendResponse(chatId, "Wenn du das Quiz beenden möchtest tippe 'quit' in den Chat" );
           }
       }
   }

   private void helloResponse(long chatId){
        sendResponse(chatId, """
                    0. You are currently in default mode, apart from messages that start with hello,
                        only the mentioned keyword in the other bullet-points will have an effect on the bot.
                    1. Type a message starting with 'chatgpt' to get into the chatgpt mode,
                        every following inputs will be send to chatgpt.
                    2. If you send a message starting with 'quiz' you will get into the quiz-mode and
                        a question will appear directly after you send the message. If you answer it successfully
                        you will get another question until you leave the mode. After every 5 questions answered the questions
                        get more difficult. Question 11 onwards the questions are always on the hardest difficulty, specified by the API.
                        To leave any mode just type a message starting with 'quit'
                   """ );
        sendStartMenu(chatId);
    }

   private void chatGPTResponse(long chatId, String messageReceived){
        if(messageReceived == null) return;

        quit(messageReceived); //Checks if a quit message was received and quits if true

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
        MainMenu menu = new MainMenu(chatId,List.of("Quiz","ChatGpt"));
        SendMessage message = menu.getSendMenu();

        try{
            execute(message);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    private void sendQuizLoseMenu(long chatId){
        MainMenu menu = new MainMenu(chatId, List.of("yes","no"));
        SendMessage message = menu.getSendMenu();
    }



    //Methods mainly for test-purposes, that's why the visibility is default.

    State getState(){
        return this.state;
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
