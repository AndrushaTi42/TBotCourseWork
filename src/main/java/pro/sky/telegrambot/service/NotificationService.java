package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;

@Service
public class NotificationService {
    private Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationTaskRepository notificationTaskRepository;
    @Autowired
    private TelegramBot telegramBot;

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotifications() {
        logger.info("Processing search notification");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> tasks = notificationTaskRepository.findByDateTime(now);
        tasks.forEach(task -> {
            SendMessage sendMessage = new SendMessage(task.getChatId(), task.getTextMessage());
            logger.info("Processing send notification: {}", sendMessage);
            try {
                telegramBot.execute(sendMessage);
                logger.info("Notification successfully sent for chatId: {}", task.getChatId());
            } catch (Exception e) {
                logger.error("Error in sending notification for chatId: {}", task.getChatId(), e);
            } finally {
                logger.debug("Delete of task {} in database", task.getChatId());
                notificationTaskRepository.delete(task);
            }
        });

    }
}
