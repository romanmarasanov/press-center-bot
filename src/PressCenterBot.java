import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;


public class PressCenterBot extends TelegramLongPollingBot {
    private static final String EVENTS_LIST_FILE_PATH = "D:\\Study\\polismail\\telegram-bots\\press-center-bot\\src\\events";

    private static List<PlannedEvent> eventsList;
    private static final List<User> usersList = new ArrayList<>();
    private static final ReplyKeyboardMarkup startKeyboard = new ReplyKeyboardMarkup();
    private static final ReplyKeyboardMarkup eventsKeyboard = new ReplyKeyboardMarkup();

    public PressCenterBot() {
        eventsList = getEventsFromFile(EVENTS_LIST_FILE_PATH);

        KeyboardRow startingRow = new KeyboardRow();
        startingRow.add("Узнать события!");
        startKeyboard.setKeyboard(List.of(startingRow));
        startKeyboard.setSelective(true);
        startKeyboard.setResizeKeyboard(true);

        KeyboardRow eventsRow = new KeyboardRow();
        for (PlannedEvent event : eventsList) {
            eventsRow.add(event.name);
        }
        eventsKeyboard.setKeyboard(List.of(eventsRow, startingRow));
        eventsKeyboard.setSelective(true);
        eventsKeyboard.setResizeKeyboard(true);
    }

    @Override
    public void onUpdateReceived(Update update) {
        // All implementation here
        if (update.hasMessage() && update.getMessage().hasText()) {
            // get message and its sender
            Message message = update.getMessage();
            User user = message.getFrom();
            // High-level logging!
            System.out.println("[LOG] event: new message\n\t  from: " + user.getUserName() + "\n\t  text: "
                    + message.getText() + "\n\t  time: " + LocalDateTime.now().toString());
            // Serving new message
            processNewMessage(user, message);
        }
    }

    private void processNewMessage(User user, Message message) {
        String messageText = message.getText();
        if (messageText.equals("/start")) {
            sendAllEvents(user);
            return;
        }
        if (messageText.equals("Узнать события!")) {
            sendAllEvents(user);
            return;
        }
        for (PlannedEvent event : eventsList) {
            if (event.name.equals(messageText.toLowerCase(Locale.ROOT))) {
                sendEvent(user, event);
                return;
            }
        }
        sendStartingMessage(user);
    }

    private void sendStartingMessage(User to) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(to.getId()));
        message.setText("Привет! Чтобы узнать об отрядных активностях на месяц вперед," +
                " воспользуйся кнопкой или напиши мне \"Узнать события!\" вручную");
        message.setReplyMarkup(eventsKeyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendEvent(User to, PlannedEvent event) {
        sendText(to, "Отлично! Лови детальную информацию по выбранному событию!\n\n" + event.toString());
    }

    private void sendAllEvents(User to) {
        StringBuilder textBuilder = new StringBuilder("Держи список отрядных событий на ближайший месяц:\n\n");
        for (PlannedEvent event : eventsList) {
            textBuilder.append(event.name).append("\n").append("Дата: ").append(event.date).append("\n\n");
        }
        textBuilder.append("Если хочешь узнать об определенном событии подробнее," +
                " тыкни на соответствующую кнопку в меню или отправь мне название вручную." +
                " Кроме того, ты всегда можешь обновить список событий, нажав на кнопку \"Узнать события!\"");

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(to.getId()));
        message.setText(textBuilder.toString());
        message.setReplyMarkup(eventsKeyboard);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendText(User recipient, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(recipient.getId()));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendImageFromUrl(String imageUrl, User user) {
        System.out.println("[LOG] event: sending photo\n\t  to: " + user.getUserName() + "\n\t  time: " + LocalDateTime.now().toString());
        SendPhoto imageSender = new SendPhoto();
        imageSender.setChatId(String.valueOf(user.getId()));
        imageSender.setPhoto(new InputFile(imageUrl));
        try {
            execute(imageSender);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Poly_SO_bot";
    }

    @Override
    public String getBotToken() {
        return "2044783819:AAFtfMFf0K5oiFuR3vsAzCSlWkh19ztez-I";
    }

    private List<PlannedEvent> getEventsFromFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<PlannedEvent> events = new ArrayList<>();
        for (String line : lines) {
            String[] eventProps = line.split(";");
            PlannedEvent event = new PlannedEvent(eventProps[0], eventProps[1],
                    eventProps[2], Boolean.parseBoolean(eventProps[3]));
            events.add(event);
        }
        return events;
    }
}