package telegram.update.business.text.message;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telegram.update.engine.entity.Session;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StartArgGenerator {

    public static Object[] getArgs(Session session) {
        log.info("Call StartArgGenerator method getArgs");
        return new Object[] { session.getProfile().getFullName() };
    }
}