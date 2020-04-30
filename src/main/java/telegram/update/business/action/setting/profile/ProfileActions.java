package telegram.update.business.action.setting.profile;

import org.springframework.context.ApplicationContext;

import static telegram.update.engine.entity.enums.ValidationKey.NAME_EDITED;
import static telegram.update.engine.entity.enums.ValidationKey.PHONE_EDITED;
import static telegram.update.engine.validation.ValidateConsumers.AFTER_ACTION_SUCCESS;

import telegram.update.engine.entity.Message;
import telegram.update.engine.entity.Profile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileActions {

	public static void editName(Message message, ApplicationContext ctx) {
		Profile profile = message.getSession().getProfile();
		profile.setFullName(message.getSession().getInputData());

		AFTER_ACTION_SUCCESS.accept(message, NAME_EDITED.getKey());
		log.info("Validation success -> name changed: %s", profile.getFullName());
	}

	public static void editPhone(Message message, ApplicationContext ctx) {
		Profile profile = message.getSession().getProfile();
		profile.setPhone(message.getSession().getInputData());

		AFTER_ACTION_SUCCESS.accept(message, PHONE_EDITED.getKey());
		log.info("Validation success -> phone changed: {}", profile.getPhone());
	}
}
