package telegram.update.business.validation.setting.profile;

import static telegram.update.engine.validation.ValidateConsumers.IS_NAME;
import static telegram.update.engine.validation.ValidateConsumers.IS_PHONE;

import org.springframework.context.ApplicationContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import telegram.update.engine.entity.Message;
import telegram.update.engine.exception.ValidationErrorException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileValidations {

    public static void validateName(Message message, ApplicationContext ctx) {
        if (!IS_NAME.test(message.getSession().getInputData())) {
            throw new ValidationErrorException(String.format("Validation failed -> name didn't change: %s",
                                                             message.getSession().getProfile().getFullName()));
        }
    }

    public static void validatePhone(Message message, ApplicationContext ctx) {
        if (!IS_PHONE.test(message.getSession().getInputData())) {
            throw new ValidationErrorException(String.format("Validation failed -> phone didn't change: %s",
                                                             message.getSession().getProfile().getPhone()));
        }
    }
}
