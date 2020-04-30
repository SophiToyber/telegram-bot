package telegram.update.engine.services;

import static telegram.update.engine.utils.LocalizationService.getMessage;
import static telegram.update.engine.utils.MethodUtil.proccessMethod;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telegram.update.engine.entity.Message;
import telegram.update.engine.entity.Session;
import telegram.update.engine.entity.config.TextConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextService {

	private static final String BASE_TEXT_PATH = "telegram.update.business.text.message.";
	@Resource
	private ApplicationContext ctx;	

	public void generateMessageText(Message message) {
		Session session = message.getSession();
		TextConfig textConfig = session.getMessageConfig().getText();

		if (textConfig.getKey() == null) {
			String text = null;
			text = (String) proccessMethod(BASE_TEXT_PATH, textConfig.getArgGenerationMethodPath(),
					Arrays.asList(message, ctx));
			message.setText(text);
		} else {
			Object[] textArgs = null;
			if (textConfig.getArgGenerationMethodPath() != null) {
				textArgs = (Object[]) proccessMethod(BASE_TEXT_PATH, textConfig.getArgGenerationMethodPath(),
						Arrays.asList(session));
			}
			message.setText(getMessage(textConfig.getKey(), session.getLocale(), textArgs));
		}
		log.info(String.format("TextService: text was successfully populated for user %s",
				message.getSession().getProfile().getUserName()));
	}
}
