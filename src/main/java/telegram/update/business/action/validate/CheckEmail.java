package telegram.update.business.action.validate;

import static telegram.update.engine.validation.ValidateConsumers.AFTER_ACTION_SUCCESS;

import java.util.function.Predicate;

import static telegram.update.engine.validation.ValidateConsumers.AFTER_ACTION_FAILURE;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import telegram.update.engine.entity.Message;

@Slf4j
@Component
@PropertySource({"locale:messages.properties", "locale:messages_ua.properties"})
public class CheckEmail {

	private static String LAST_MESSAGE_TEXT;
	private static String UA_LAST_MESSAGE_TEXT;

	@Value("${message.startingKeyboard}")
	public void setLastMsgText(String lastMsgText) {
		LAST_MESSAGE_TEXT = lastMsgText;
	}

	@Value("${message.ua.startingKeyboard}")
	public void setUaLastMsgText(String UaLastMsgText) {
		UA_LAST_MESSAGE_TEXT = UaLastMsgText;
	}

	public static void isEmailAddress(Message message, ApplicationContext ctx) {

		if (isNotOldData.test(message)) {
			if (EmailValidator.getInstance().isValid(message.getText())) {
				AFTER_ACTION_SUCCESS.accept(message, "Your email is correct (^*^)");
			}
			
			AFTER_ACTION_FAILURE.accept(message, "You entered the wrong email!");
		}
		
		log.info("Email was successfully validate");

	}

	public static Predicate<Message> isNotOldData = m -> (!m.getText().equals(LAST_MESSAGE_TEXT)
			&& !m.getText().equals(UA_LAST_MESSAGE_TEXT));

}
