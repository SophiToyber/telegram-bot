package telegram.update.engine.services;

import static telegram.update.engine.utils.LocalizationService.getMessage;
import static telegram.update.engine.utils.MethodUtil.proccessMethod;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telegram.update.engine.entity.Button;
import telegram.update.engine.entity.Message;
import telegram.update.engine.entity.Session;
import telegram.update.engine.entity.config.ButtonConfig;
import telegram.update.engine.entity.config.LabelConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyboardService {

	private static final String BASE_DYNAMIC_VALIDAION_PATH = "telegram.update.business.validation.dynamic.";
    private static final String BASE_BUTTON_TEXT_GENERATOR_METHOD_PATH = "telegram.update.business.text.button.";

	@Resource
	private ApplicationContext ctx;

	public void generateKeyboard(Message message) {


		Session session = message.getSession();
		List<List<ButtonConfig>> callbacks = session.getMessageConfig().getCallbacks();

		message.setCallbacks(callbacks.stream().map(row -> row.stream()
				.filter(buttonConf -> checkDynamicCondition(buttonConf.getDynamicValidationMethodPath(), message))
				.map(buttonConf -> Button.builder().key(extractTextFromLabelConfig(buttonConf.getLabel(), message))
						.url(buttonConf.getUrl()).action(buttonConf.getAction()).id(buttonConf.getId())
						.next(buttonConf.getNext()).inputDataValidator(buttonConf.getInputDataValidationMethodPath())
						.multiClickable(buttonConf.getMultiClickable()).build())
				.collect(Collectors.toList())).filter(row -> !row.isEmpty()).collect(Collectors.toList()));
		fillSessionWithInfo(session, message.getCallbacks());

		log.info(String.format("KeyboardService: keyboard was successfully created for user %s",
				message.getSession().getProfile().getUserName()));
	}

	private String extractTextFromLabelConfig(LabelConfig labelConfig, Message message) {
		if (Objects.nonNull(labelConfig.getText())) {
			return labelConfig.getText();
		} else if (Objects.nonNull(labelConfig.getKey())) {
			return getMessage(labelConfig.getKey(), message.getSession().getLocale());
		} else {
			return getButtonTextFromMethod(labelConfig.getMethodPath(), message);
		}
	}

	private void fillSessionWithInfo(Session session, List<List<Button>> keyboard) {
		session.setCurrentButtons(keyboard.stream().flatMap(List::stream).collect(Collectors.toSet()));
	}

	private boolean checkDynamicCondition(String dynamicValidationMethodPath, Message message) {
		if (Objects.isNull(dynamicValidationMethodPath)) {
			return true;
		}
		return (boolean) proccessMethod(BASE_DYNAMIC_VALIDAION_PATH, dynamicValidationMethodPath,
				Arrays.asList(message, ctx));
	}

	private String getButtonTextFromMethod(String buttonTextGeneratorMethodPath, Message message) {
		return (String) proccessMethod(BASE_BUTTON_TEXT_GENERATOR_METHOD_PATH, buttonTextGeneratorMethodPath,
				Arrays.asList(message, ctx));
	}
}