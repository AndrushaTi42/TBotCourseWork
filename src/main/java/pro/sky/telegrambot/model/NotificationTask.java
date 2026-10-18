package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String textMessage;
    private LocalDateTime dateTime;

    public NotificationTask() {
    }

    public NotificationTask(Long id, Long chatId, String textMessage, LocalDateTime dateTime) {
        this.id = id;
        this.chatId = chatId;
        this.textMessage = textMessage;
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", textMessage='" + textMessage + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        NotificationTask that = (NotificationTask) o;
        return  Objects.equals(chatId, that.chatId)
                && Objects.equals(textMessage, that.textMessage)
                && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(chatId);
        result = 31 * result + Objects.hashCode(textMessage);
        result = 31 * result + Objects.hashCode(dateTime);
        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
