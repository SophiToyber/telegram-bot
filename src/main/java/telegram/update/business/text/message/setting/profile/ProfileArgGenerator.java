package telegram.update.business.text.message.setting.profile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telegram.update.engine.entity.Profile;
import telegram.update.engine.entity.Session;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileArgGenerator {

    public static Object[] getArgs(Session session) {
        Profile profile = session.getProfile();
        log.info("Call ProfileArgGenerator method getArgs");
        return new Object[] { profile.getFullName(), profile.getPhone() };
    }

    public static Object[] getName(Session session) {
        log.info("Call ProfileArgGenerator method getName");
        return new Object[] { session.getProfile().getFullName() };
    }

    public static Object[] getPhone(Session session) {
        log.info("Call ProfileArgGenerator method getPhone");
        return new Object[] { session.getProfile().getPhone() };
    }
}
