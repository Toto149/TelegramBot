package org.example.menu;

import org.example.quiz.Question;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class QuizMenu {
    private SendMessage sendQuizMenu = new SendMessage();
    private InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
    private List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    private List<InlineKeyboardButton> inlineRow1 = new ArrayList<>();
    private List<InlineKeyboardButton> inlineRow2 = new ArrayList<>();

    public QuizMenu(long chatId, Question question){
        sendQuizMenu.setChatId(chatId);
        sendQuizMenu.setText("Choose an answer: ");

        List<InlineKeyboardButton> buttons = new ArrayList<>();

        for(String option : question.getOptions()){

            InlineKeyboardButton btn = new InlineKeyboardButton();

            btn.setText(String.valueOf((char)(65+buttons.size())));
            btn.setCallbackData(option);

            buttons.add(btn);
        }

        int i = 0;
        for(InlineKeyboardButton btn : buttons){
            if(i%2==0){
                inlineRow1.add(btn);

            }else {
                inlineRow2.add(btn);
            }
            i++;
        }

        rowsInline.add(inlineRow1);
        rowsInline.add(inlineRow2);

        inlineMarkup.setKeyboard(rowsInline);
        sendQuizMenu.setReplyMarkup(inlineMarkup);
    }

    public SendMessage getSendQuizMenu() {
        return sendQuizMenu;
    }
}
