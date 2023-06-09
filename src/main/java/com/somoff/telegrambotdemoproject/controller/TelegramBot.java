package com.somoff.telegrambotdemoproject.controller;

import com.somoff.telegrambotdemoproject.exception.ServiceException;
import com.somoff.telegrambotdemoproject.service.CityService;
import com.somoff.telegrambotdemoproject.service.ExchangeRatesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String EUR = "/eur";
    private static final String CNY = "/cny";
    private static final String HELP = "/help";
    private static final String CITIES = "/cities";
    private static final String EXIT = "/exit";
    private static final String RUN = "/run";

    private static final String RERUN = "/rerun";

    private int keyBot = 1;


    private final ExchangeRatesService exchangeRatesService;
    private final CityService cityService;

    @Value("${bot.username}")
    private String botUsername;


    @Autowired
    public TelegramBot(@Value("${bot.token}") String botToken, ExchangeRatesService exchangeRatesService, CityService cityService) {
        super(botToken);
        this.exchangeRatesService = exchangeRatesService;
        this.cityService = cityService;
        List<BotCommand> listOfCommands = new ArrayList<>(List.of(
                new BotCommand(START, "запустить бота"),
                new BotCommand(CITIES, "запустить режим игры 'Города'"),
                new BotCommand(HELP, "справка/правила игры")
                ));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка настройки списка команд бота: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        if (keyBot == 1) {
            switch (message) {
                case START -> {
                    String userName = update.getMessage().getChat().getUserName();
                    startCommand(chatId, userName);
                }
                case CITIES -> {
                    keyBot = 2;
                    String userName = update.getMessage().getChat().getUserName();
                    startCitiesCommand(chatId, userName);
                }
                case USD -> usdCommand(chatId);
                case EUR -> eurCommand(chatId);
                case CNY -> cnyCommand(chatId);
                case HELP -> helpCommand(chatId);
                default -> unknownCommand(chatId);
            }
        }
        if (keyBot == 2) {
            switch (message){
                case EXIT -> {
                    keyBot = 1;
                    String userName = update.getMessage().getChat().getUserName();
                    startCommand(chatId, userName);
                }
                case RUN -> startPlayCommand(chatId);
                case RERUN -> rerunCitiesCommand(chatId);
                case HELP -> helpCommandCities(chatId);
                default -> {
                    if(!CITIES.equals(message))
                        playCitiesCommand(chatId, message);
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                                
                Вы сможете узнать официальные курсы валют на сегодня, установленные ЦБ РФ.
                                
                Для этого воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                /cny - курс юаня

                                
                Дополнительные команды:
                /cities - игра 'Города'
                /help - получение справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void startCitiesCommand(Long chatId, String userName) {
        try {
            cityService.downloadCityInfo();
        } catch (ServiceException e) {
            log.error("Ошибка загрузки городов в БД", e);
        }
        var text = """
                Добро пожаловать в режим игры, %s!
                                
                Этот бот может играть с вами в игру 'Города'.
                                
                Для управления воспользуйтесь командами:
                /run - начать(продолжить) игру
                /rerun - перезапустить игру
                /exit - выход из игры 'Города'

                Дополнительные команды:
                /help - правила игры
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }

    private void usdCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getUSDExchangeRate();
            var text = "Курс доллара на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            log.error("Ошибка получения курса доллара", e);
            formattedText = "Не удалось получить текущий курс доллара. Попробуйте позже.";
        }
        sendMessage(chatId, formattedText);
    }

    private void eurCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getEURExchangeRate();
            var text = "Курс евро на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            log.error("Ошибка получения курса евро", e);
            formattedText = "Не удалось получить текущий курс евро. Попробуйте позже.";
        }
        sendMessage(chatId, formattedText);
    }

    private void cnyCommand(Long chatId) {
        String formattedText;
        try {
            var usd = exchangeRatesService.getCNYExchangeRate();
            var text = "Курс юаня на %s составляет %s рублей";
            formattedText = String.format(text, LocalDate.now(), usd);
        } catch (ServiceException e) {
            log.error("Ошибка получения курса юаня", e);
            formattedText = "Не удалось получить текущий курс юаня. Попробуйте позже.";
        }
        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту
                                
                Для получения текущих курсов валют воспользуйтесь командами:
                /usd - курс доллара
                /eur - курс евро
                /cny - курс юаня
                """;
        sendMessage(chatId, text);
    }
    private void helpCommandCities(Long chatId) {
        var text = """
                Справочная информация по режиму игры 'Города'
                                
                Правила игры 'Города':
                Игрок называет реально существующий город России, затем бот озвучивает город, название которого начинается на ту букву, которой оканчивается предыдущий названный город, потом игрок называет город, начинающийся с буквы, которая совпадает с последней буквой города, названного ботом и т.д.
                
                Исключением в правилах игры являются названия, оканчивающиеся на «Ь», «Ъ», «Й», «Ы»:
                в таких случаях участник называет город на предпоследнюю букву.
                
                Игра оканчивается, когда очередной участник не может назвать нового города
                
                /run - начать(продолжить) игру
                /rerun - перезапустить игру
                /exit - выход из игры 'Города'
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду!";
        sendMessage(chatId, text);
    }

    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        sendMessage.setReplyMarkup(getKeyboardRow(keyBot));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }


    private void playCitiesCommand(Long chatId, String message) {
        cityService.registerUser(chatId);
        var result = cityService.findCityInBD(chatId, message);
        sendMessage(chatId, result);
    }

    private void startPlayCommand(Long chatId) {
        cityService.registerUser(chatId);
        var result = cityService.getFirstSymbol(chatId);
        sendMessage(chatId, result);
    }

    private void rerunCitiesCommand(Long chatId) {
        cityService.registerUser(chatId);
        cityService.clearPlayCities(chatId);
        startPlayCommand(chatId);
    }

    private ReplyKeyboardMarkup getKeyboardRow(int keyBot) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        if (keyBot == 1) {
            row.add("/usd");
            row.add("/eur");
            row.add("/cny");
            keyboardRows.add(row);
            row = new KeyboardRow();
            row.add("/cities");
            row.add("/help");
            keyboardRows.add(row);
        }
        if (keyBot == 2) {
            row.add("/run");
            row.add("/rerun");
            keyboardRows.add(row);
            row = new KeyboardRow();
            row.add("/help");
            row.add("/exit");
            keyboardRows.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;

    }

}

