package telegram.update.engine.services;

import static telegram.update.engine.entity.enums.Modifier.CREATE;
import static telegram.update.engine.entity.enums.Modifier.EDIT;
import static telegram.update.engine.utils.MethodUtil.proccessMethod;
import static telegram.update.engine.utils.Transformer.TO_MESSAGE;
import static telegram.update.engine.validation.ValidateConsumers.AFTER_ACTION_FAILURE;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telegram.update.engine.entity.Message;
import telegram.update.engine.entity.Session;
import telegram.update.engine.entity.enums.Modifier;
import telegram.update.engine.exception.ValidationErrorException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionHandlerService {

	@Resource
	private ApplicationContext ctx;

	private static final String BASE_ACTION_PATH = "telegram.update.business.action.";
    private static final String BASE_VALIDAION_PATH = "telegram.update.business.validation.";
    
	public List<Message> generateMessage(Update update, Session session) {
		return generateMessages(update, session, CREATE);
	}

	public List<Message> generateCallbackMessage(Update update, Session session) {
		return generateMessages(update, session, EDIT);
	}

	private List<Message> generateMessages(Update update, Session session, Modifier modifier) {
		List<Message> messages = new LinkedList<>();

		Message message = TO_MESSAGE.apply(update, session);
		message.setModifier(modifier);
		messages.add(message);
		log.info(String.format("ActionHandlerService: message was built for user %s",
				session.getProfile().getUserName()));
		callActions(message);

		return messages;
	}

	private void callActions(Message message) {
		Optional.ofNullable(message.getSession().getActiveButton()).ifPresent(b -> {
			try {
				if (message.getModifier().equals(CREATE)) {
					Optional.ofNullable(b.getInputDataValidator())
							.ifPresent(v -> proccessMethod(BASE_VALIDAION_PATH, v, Arrays.asList(message, ctx)));
					Optional.ofNullable(b.getAction())
							.ifPresent(a -> proccessMethod(BASE_ACTION_PATH, a, Arrays.asList(message, ctx)));
				} else if (b.getInputDataValidator() == null) {
					Optional.ofNullable(b.getAction())
							.ifPresent(a -> proccessMethod(BASE_ACTION_PATH, a, Arrays.asList(message, ctx)));
				}
			} catch (ValidationErrorException e) {
				AFTER_ACTION_FAILURE.accept(message, buildValidationErrorMessage(b.getId().toLowerCase()));
			}
		});
	}

	private String buildValidationErrorMessage(String key) {
		String errorKey = key.substring(key.lastIndexOf('/') + 1, key.length());
		return String.format("error.validation.%s", errorKey);
	}
}