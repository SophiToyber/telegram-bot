package telegram.update.engine;

import static java.util.Locale.ENGLISH;
import static org.telegram.abilitybots.api.objects.Flag.CALLBACK_QUERY;
import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import lombok.extern.slf4j.Slf4j;
import telegram.update.engine.config.EngineProperties;
import telegram.update.engine.entity.Profile;
import telegram.update.engine.entity.Session;
import telegram.update.engine.services.BotOrchestration;

@Slf4j
@Component
public class Bot extends AbilityBot {

    private final EngineProperties properties;
    private final BotOrchestration botOrchestration;

    @Resource
    private ApplicationContext ctx;

    private Map<Long, Session> sessions;

    @Lazy
    @Autowired
    public Bot(EngineProperties properties, BotOrchestration botOrchestration) {
        super(properties.getToken(), properties.getUsername());
        this.sessions = new HashMap<>();
        this.properties = properties;
        this.botOrchestration = botOrchestration;
    }

    public Reply replyToMessage() {
        Consumer<Update> consumer = u -> {
            Session session = fetchSession(u, u.getMessage().getChatId(), u.getMessage().getText());
            botOrchestration.adaptMessage(u, session);
        };
        return Reply.of(consumer, MESSAGE);
    }

    public Reply replyToCallBack() {
        Consumer<Update> consumer = u -> {
            Session session = fetchSession(u, u.getCallbackQuery().getMessage().getChatId(),
                                           u.getCallbackQuery().getData());
            botOrchestration.adaptCallback(u, session);
        };
        return Reply.of(consumer, CALLBACK_QUERY);
    }

    private Session fetchSession(Update update, Long chatId, String data) {
        Session session = this.sessions.get(chatId);

        if (session == null) {
            User user = update.getMessage().getFrom();
            log.info("Create new session for user: {}", user.getId());
            Profile profile = Profile.builder()
                .id(Long.valueOf(user.getId()))
                .fullName(String.format("%s %s", user.getFirstName(), user.getLastName()))
                .bot(user.getBot())
                .userName(user.getUserName())
                .languageCode(user.getLanguageCode())
                .build();

            session = Session.builder()
                .profile(profile)
                .locale(ENGLISH)
                .build();

            sessions.put(update.getMessage().getChatId(), session);
        }

        if (update.hasCallbackQuery()) {
            session.setCallbackData(data);
        } else {
            session.setInputData(data);
        }
        return session;
    }

    @Override
    public int creatorId() {
        return properties.getCreator().getId();
    }

}