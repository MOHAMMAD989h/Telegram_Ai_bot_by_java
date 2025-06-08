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
        return "HomeTech_Support_1_bot";
    }

    @Override
    public String getBotToken() {
        return "7724285641:AAETN6ppyqOoUwEUmkIphfzTgG9mO41kIXM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
        if (!update.hasMessage()) {
            System.out.println("null-enter text");
        }
        else if(update.getMessage().hasDocument()){
            sendText(update,"please not send document");
        }
        else if(update.getMessage().hasPhoto()){
            sendText(update,"please not send photo");
        }
        else if(update.getMessage().hasText()){
            sendText(update,ChatbotService.getAIResponse(update.getMessage().getText()));
        }
        else if(update.getMessage().hasAudio()){}
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


        public void getDocument(Update update) throws TelegramApiException {
            if(!update.hasMessage()){
                System.out.println("null-enter text");
                return;
            }
            Document document = update.getMessage().getDocument();
            String fileid = document.getFileId();

            GetFile getFile = new GetFile(fileid);


            String path = execute(getFile).getFilePath();

            File file = downloadFile(path);

            file.renameTo(new File("data/" + document.getFileName()));
        }
        public void getPhoto(Update update) throws TelegramApiException {
        List<PhotoSize> photos = update.getMessage().getPhoto();
        PhotoSize largestPhoto = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);
        if (largestPhoto != null) {
            String fileId = largestPhoto.getFileId();

            String fileName = largestPhoto.getFileUniqueId() + ".jpg";

            GetFile getFileRequest = new GetFile(fileId);
            org.telegram.telegrambots.meta.api.objects.File telegramFile = execute(getFileRequest);

            File localFile = downloadFile(telegramFile);

            /*
            File dataDir = new File("data/");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
               اگر پوشه نباشد می سازد
             */
            localFile.renameTo(new File("data/" + fileName));
            System.out.println("Photo saved successfully as: " + fileName);
        }
    }
    public void getAudio(Update update) throws TelegramApiException {
        if(!update.hasMessage()){
            System.out.println("null-enter text");
            return;
        }
        Audio audio = update.getMessage().getAudio();
        String fileid = audio.getFileId();

        GetFile getFile = new GetFile(fileid);


        String path = execute(getFile).getFilePath();

        File file = downloadFile(path);

        file.renameTo(new File("data/" + audio.getFileName()));
    }
}
