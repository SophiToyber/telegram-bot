package telegram.update.engine.utils;

import static telegram.update.engine.utils.LocalizationService.getMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import telegram.update.engine.entity.Message;
import telegram.update.engine.entity.Session;
import telegram.update.engine.interfaces.functional.ThiFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transformer {

    private static final String ERROR_OLD_MESSAGE = "error.oldMessage";

    // ------------------UPDATE------------------ //
    public static final BiFunction<Update, Session, Message> TO_MESSAGE = (u, s) -> {
        Message.MessageBuilder builder = Message.builder().session(s);
        if (u.hasCallbackQuery()) {
            builder.messageId(u.getCallbackQuery().getMessage().getMessageId())
                .text(u.getCallbackQuery().getMessage().getText());
        } else {
            builder.messageId(u.getMessage().getMessageId()).text(u.getMessage().getText());
        }
        Message message = builder.build();
        if (!Optional.ofNullable(message.getSession().getLastMessageId()).isPresent()
                || message.getSession().getLastMessageId().compareTo(message.getMessageId()) < 0) {
            message.getSession().setLastMessageId(message.getMessageId());
        }
        return message;
    };
    // ------------------UPDATE------------------ //

    // ------------------MESSAGE------------------ //
    public static final Function<Message, SendMessage> TO_SEND_MESSAGE_WITH_INLINE = m -> transformSend(m,
                                                                                                        transformInlineKeyboardMarkup(m));

    public static final Function<Message, EditMessageText> TO_EDIT_MESSAGE_TEXT = m -> new EditMessageText()
        .setChatId(m.getSession().getProfile().getId())
        .setMessageId(m.getMessageId())
        .setText(m.getText())
        .enableHtml(true)
        .setReplyMarkup(transformInlineKeyboardMarkup(m));

    public static final ThiFunction<Locale, Long, Integer, EditMessageText> TO_EDIT_MESSAGE_TEXT_WITH_EXPIRED_KEYBOARD = (
            locale, chatId, messageId) -> new EditMessageText().setChatId(chatId)
                .setMessageId(messageId)
                .setText(getMessage(ERROR_OLD_MESSAGE, locale));

    public static final BiFunction<String, Long, SendMessage> TO_SEND_STORY = (text,
            chatId) -> new SendMessage().setChatId(chatId).setText(text).enableHtml(true);

    private static SendMessage transformSend(Message message, ReplyKeyboard replyKeyboard) {
        return new SendMessage().setChatId(message.getSession().getProfile().getId())
            .setText(message.getText())
            .enableHtml(true)
            .setReplyMarkup(replyKeyboard);
    }

    private static InlineKeyboardMarkup transformInlineKeyboardMarkup(Message m) {
        List<List<InlineKeyboardButton>> keyboardRows = new LinkedList<>();
        m.getCallbacks().stream().forEachOrdered(row -> {
            List<InlineKeyboardButton> inlineRow = new LinkedList<>();
            row.stream().forEach(b -> {
                if (b != null) {
                    inlineRow.add(new InlineKeyboardButton().setUrl(b.getUrl())
                        .setCallbackData(b.getId())
                        .setText(b.getKey()));
                } else {
                    inlineRow.add(new InlineKeyboardButton().setText(	b.getKey()).setCallbackData(b.getId()));
                }
            });
            keyboardRows.add(inlineRow);
        });

        return new InlineKeyboardMarkup().setKeyboard(keyboardRows);
    }
    // ------------------MESSAGE------------------ //
}
