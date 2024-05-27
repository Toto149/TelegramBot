package org.example.menu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {
    private SendMessage sendMenu = new SendMessage();
    private InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    private List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    private List<InlineKeyboardButton> rowInline = new ArrayList<>();
    private List<InlineKeyboardButton> rowInline2 = new ArrayList<>();

    public MainMenu(long chadId,List<String> options){
        sendMenu.setChatId(chadId);
        sendMenu.setText("Choose an Option: ");
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for(int i=0;i<options.size();i++){
            buttons.add(new InlineKeyboardButton());
        }
        int i = 0;
        for(String option: options){
            buttons.get(i).setText(option);
            buttons.get(i).setCallbackData(option.toLowerCase());
            i++;
        }
        int j = 0;
        for(InlineKeyboardButton btn : buttons){
            if (j % 2 == 0) {
                rowInline.add(btn);
            } else {
                rowInline2.add(btn);
            }
            j++;
        }

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        markupInline.setKeyboard(rowsInline);
        sendMenu.setReplyMarkup(markupInline);

    }

    public SendMessage getSendMenu(){
        return sendMenu;
    }

}
