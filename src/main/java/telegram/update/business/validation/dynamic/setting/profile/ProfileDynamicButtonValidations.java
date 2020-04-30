package telegram.update.business.validation.dynamic.setting.profile;

import java.util.Objects;

import org.springframework.context.ApplicationContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import telegram.update.engine.entity.Message;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileDynamicButtonValidations {

    public static boolean validatePhoneButton(Message message, ApplicationContext ctx) {
        return Objects.isNull(message.getSession().getProfile().getPhone());
    }
}
