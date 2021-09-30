import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
    private static final ReplyKeyboardMarkup mainKeyboard = new ReplyKeyboardMarkup();
    private static final InlineKeyboardMarkup mainInlineKeyboard = new InlineKeyboardMarkup();
    private static final InlineKeyboardMarkup eventsInlineKeyboard = new InlineKeyboardMarkup();
    private static final InlineKeyboardMarkup squadsInlineKeyboard = new InlineKeyboardMarkup();
    private static final InlineKeyboardMarkup predictorInlineKeyboard = new InlineKeyboardMarkup();


    public PressCenterBot() {
        eventsList = getEventsFromFile(EVENTS_LIST_FILE_PATH);

        setMainKeyboard();
        setMainInlineKeyboard();
        setEventsKeyboard();
        setSquadsKeyboard();
        setPredictorKeyboard();
    }

    private void setPredictorKeyboard() {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Назад к меню");
        button.setCallbackData("ССервО");
        predictorInlineKeyboard.setKeyboard(List.of(List.of(button)));
    }

    private void setSquadsKeyboard() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("ССО");
        button1.setCallbackData("ССО");
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("СПО");
        button2.setCallbackData("СПО");
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("ССхО");
        button3.setCallbackData("ССхО");
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("CАО");
        button4.setCallbackData("САО");
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("СОП");
        button5.setCallbackData("СОП");
        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("ССервО");
        button6.setCallbackData("ССервО");
        squadsInlineKeyboard.setKeyboard(List.of(List.of(button1), List.of(button2), List.of(button3), List.of(button4), List.of(button5), List.of(button6)));
    }

    private void setEventsKeyboard() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Сентябрь");
        button1.setCallbackData("Сентябрь");
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Октябрь");
        button2.setCallbackData("Октябрь");
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Ноябрь");
        button3.setCallbackData("Ноябрь");
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("Декабрь");
        button4.setCallbackData("Декабрь");
        eventsInlineKeyboard.setKeyboard(List.of(List.of(button1, button2, button3, button4)));
    }

    private void setMainInlineKeyboard() {
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Мероприятия");
        button1.setCallbackData("Мероприятия");
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Отряды Политеха");
        button2.setCallbackData("Отряды");
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Отрядный Предсказатель");
        button3.setCallbackData("Предсказатель");
        mainInlineKeyboard.setKeyboard(List.of(List.of(button1), List.of(button2), List.of(button3)));
    }

    private void setMainKeyboard() {
        KeyboardRow startingRow = new KeyboardRow();
        startingRow.add("К главному меню!");
        mainKeyboard.setKeyboard(List.of(startingRow));
        mainKeyboard.setSelective(true);
        mainKeyboard.setResizeKeyboard(true);
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
        if (update.hasCallbackQuery()) {
            // get message and its sender
            String message = update.getCallbackQuery().getData();
            String userId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
            // High-level logging!
            System.out.println("[LOG] event: new query\n\t  from: " + userId + "\n\t  text: "
                    + message + "\n\t  time: " + LocalDateTime.now().toString());
            // Serving new message
            processNewQuery(userId, message);
        }
    }

    private void processNewQuery(String user, String messageText) {

        switch (messageText) {
            case "Мероприятия" -> sendEvents(user);
            case "Отряды" -> sendSquads(user);
            case "Предсказатель" -> sendPredictor(user);
//            case "Сентябрь" -> sendSeptember(user);
//            case "Октябрь" -> sendOctober(user);
//            case "Ноябрь" -> sendNovember(user);
//            case "Декабрь" -> sendDecember(user);
//            case "ССО" -> sendSso(user);
//            case "СПО" -> sendSpo(user);
//            case "ССхО" -> sendSsho(user);
//            case "САО" -> sendSao(user);
//            case "ССервО" -> sendServo(user);

        }
    }

    private void sendPredictor(String userId) {
        SendMessage eventsMessage = new SendMessage();
        eventsMessage.setReplyMarkup(squadsInlineKeyboard);
        eventsMessage.setText("""
                Один замечательный человек в своё время создал мини-бота, который проверяет, какой из отрядов Политеха тебе больше подходит. Если интересно, можешь пройти его опрос, это займет всего пару минут! 
                
                Держи ссылку: https://t.me/so_poly_bot
                """);
        eventsMessage.setChatId(userId);
        eventsMessage.disableWebPagePreview();
        sendMessage(eventsMessage);
    }

    private void sendSquads(String userId) {
        SendMessage eventsMessage = new SendMessage();
        eventsMessage.setReplyMarkup(squadsInlineKeyboard);
        eventsMessage.setText("""
                В Политехе очень много отрядов от различных направлений: 
                
                ССО (Строители)
                СПО (Педагоги)
                ССхО (Сельхозники)
                САО (Археологи)
                СОП (Проводники)
                ССервО (Сервисники)
                
                Про отряды какого направления желаешь узнать?
                """);
        eventsMessage.setChatId(userId);
        sendMessage(eventsMessage);
    }

    private void sendEvents(String userId) {
        SendMessage eventsMessage = new SendMessage();
        eventsMessage.setReplyMarkup(eventsInlineKeyboard);
        eventsMessage.setText("Я знаю о всех мероприятиях в мире студотрядов Политеха на этот семестр!" +
                " На какой месяц хочешь список мероприятий?");
        eventsMessage.setChatId(userId);
        sendMessage(eventsMessage);
    }

    private void processNewMessage(User user, Message message) {
        String messageText = message.getText();
        if (messageText.equals("/start") || messageText.toLowerCase(Locale.ROOT).equals("к главному меню!")) {
            sendStartingMessage(user);
            return;
        }
        SendMessage misunderstandMessage = new SendMessage();
        misunderstandMessage.setText("Я не понимаю! Воспользуйся клавишами в сообщениях выше" +
                " или вызови главное меню кнопкой под клавиатурой");
        misunderstandMessage.setChatId(String.valueOf(user.getId()));
        misunderstandMessage.setReplyMarkup(mainKeyboard);
        sendMessage(misunderstandMessage);
    }

    private void sendStartingMessage(User to) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(to.getId()));
        message.setText("""
                Это моё главное меню. Вот ссылки которые тебе могут пригодиться:

                Студенческие отряды Политеха в VK: 
                https://vk.com/so_politeh
                
                Студенческие отряды Политеха в Instagram: 
                https://www.instagram.com/so_politech/
                
                канал Штаба СО СПбПУ в Youtube: 
                https://www.youtube.com/channel/UCyXW-EVJixzZv33bOkn_Qhg
                
                Санкт-Петербургский политехнический университет в VK: 
                https://vk.com/pgpuspb
                
                СПбСО | Студенческие отряды Санкт-Петербурга в VK: 
                https://vk.com/spbso
                
                Российские Студенческие Отряды | РСО в VK: 
                https://vk.com/rso_official

                Я могу рассказать об отрядах Политеха и Мероприятиях! Выбирай, о чем хочешь узнать!""");
        message.setReplyMarkup(mainKeyboard);
        message.setReplyMarkup(mainInlineKeyboard);
        message.disableWebPagePreview();
        sendMessage(message);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
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
                    eventProps[2], eventProps[3]);
            events.add(event);
        }

        return events;
    }
}