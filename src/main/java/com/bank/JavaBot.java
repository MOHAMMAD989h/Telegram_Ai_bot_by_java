package com.bank;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.List;

public class JavaBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return System.getenv("TELEGRAM_BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("TELEGRAM_BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
        if (!update.hasMessage()) {
            System.out.println("null-enter text");
        }
        else if(update.getMessage().getText().equals("/operator")) {
            sendText(update,"برای ارسال پیام به اپراتور پیام خود را داده و در ابتدای ان comment/  بنویسید");
        } else if (update.getMessage().getText().startsWith("/comment")) {
                sendTextToOperator(update,update.getMessage().getText());
        } else if(update.getMessage().hasDocument()){
            sendText(update,"please not send document");
        }
        else if(update.getMessage().hasPhoto()){
            sendText(update,"please not send photo");
        }
        else if(update.getMessage().hasAudio()){
            sendText(update,"please not send audio");
        }
        else if(update.getMessage().hasText()){
            sendText(update,ChatbotService.getAIResponse(update.getMessage().getText()));
        }
        }catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }


        public void sendText(Update update,String text) throws TelegramApiException {
            String chat_id = update.getMessage().getChatId().toString();

            SendMessage message = new SendMessage();

            message.setChatId(chat_id);

            message.setText(text);
            execute(message);
        }

        public void sendTextToOperator(Update update,String text) throws TelegramApiException {
        String chat_id = "7266124167";

        SendMessage message = new SendMessage();

        message.setChatId(chat_id);

        message.setText(text);
        execute(message);
        }

}
