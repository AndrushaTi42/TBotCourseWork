package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final Pattern PATTERN =
            Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                String text = update.message().text();
                Long chatId = update.message().chat().id();
                if ("/start".equals(text)) {
                    SendMessage message = new SendMessage(
                            chatId,
                            "Привет! Напиши дату и время события, а после само уведомление в формате: " +
                                    "XX.XX.20XX XX:XX текст уведомления"
                    );
                    telegramBot.execute(message);
                } else {
                    Matcher matcher = PATTERN.matcher(text);
                    if (matcher.matches()) {
                        String dateTimeString = matcher.group(1);
                        String message = matcher.group(3);
                        try {
                            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
                            if (dateTime.isBefore(LocalDateTime.now())) {
                                telegramBot.execute(new SendMessage(
                                        chatId,
                                        "Ошибка: нельзя создать напоминание в прошлом!")
                                );
                                return;
                            }
                            NotificationTask task = new NotificationTask(null, chatId, message, dateTime);
                            notificationTaskRepository.save(task);
                            telegramBot.execute(new SendMessage(
                                    chatId,
                                    "Событие сохранено на: " + dateTime.format(DATE_TIME_FORMATTER))
                            );
                            logger.debug("User id = {}, save the massage as of :{} ", chatId, dateTimeString);
                        } catch (DateTimeParseException e) {
                            telegramBot.execute(new SendMessage(chatId, "Ошибка: Неверная дата или время."));
                            logger.warn("User id = {}, incorrect date: {}", chatId, dateTimeString);
                        }
                    } else {
                        logger.warn("User id = {}, incorrect format: {}", chatId, text);
                        telegramBot.execute(new SendMessage(chatId, "Не верный формат: ХХ.ХХ.ХХХХ 00:00 Текст"));
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
