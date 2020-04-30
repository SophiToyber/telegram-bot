package telegram.update.engine.services;

import static telegram.update.engine.services.ResourceHandlerService.fillMessageConfig;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;
import telegram.update.engine.entity.Message;
import telegram.update.engine.entity.Session;

@Component
@RequiredArgsConstructor
public class BotOrchestration {

    private final TextService textService;
    private final KeyboardService keyboardService;
    private final ActionHandlerService actionHandlerService;
    private final MessageHandlerService messageHandlerService;

    @Value("${message.config.path}")
    private String messageConfigPath;
    private static final String START = "/start";

    private static final Predicate<String> TEXT_IS_START = text -> text.startsWith(START);
    private static final Predicate<String> IS_NOT_NULL_TEXT = text -> Optional.ofNullable(text).isPresent();

    public void adaptCallback(Update update, Session session) {

        if (clickShouldNotBeProcessed(session)) {
            return;
        }
        boolean keyboardIsExpired = session.getLastMessageId()
            .compareTo(update.getCallbackQuery().getMessage().getMessageId()) > 0;
        if (keyboardIsExpired) {
            messageHandlerService.sendExpiredMessage(session.getLocale(), session.getProfile().getId(),
                                                     update.getCallbackQuery().getMessage().getMessageId());
            return;
        }

        setActiveButton(session);
        String keyboardName = session.getActiveButton().getNext();
        if (IS_NOT_NULL_TEXT.test(keyboardName)) {
            fillMessageConfig(session, String.format("%s%s", messageConfigPath, keyboardName));
        }

        List<Message> messages = actionHandlerService.generateCallbackMessage(update, session);
        adaptMessageConfig(messages);
    }

    public void adaptMessage(Update update, Session session) {

        if (IS_NOT_NULL_TEXT.and(TEXT_IS_START).test(session.getInputData())) {
            session.setActiveButton(null);
            fillMessageConfig(session, String.format("%sstart.json", messageConfigPath));
        }

        List<Message> messages = actionHandlerService.generateMessage(update, session);
        adaptMessageConfig(messages);
    }

    private boolean clickShouldNotBeProcessed(Session session) {
        return !(Objects.isNull(session.getActiveButton())
                || !session.getCallbackData().equals(session.getActiveButton().getId())
                || Boolean.TRUE.equals(session.getActiveButton().getMultiClickable()));
    }

    private void setActiveButton(Session session) {
        session.getCurrentButtons()
            .stream()
            .filter(b -> containsIgnoreCase(b.getId(), session.getCallbackData()))
            .findAny()
            .ifPresent(session::setActiveButton);
    }

    private void adaptMessageConfig(List<Message> messages) {
        messages.forEach(m -> {
            textService.generateMessageText(m);
            keyboardService.generateKeyboard(m);
        });

        messageHandlerService.sendMessage(messages);
    }
}
