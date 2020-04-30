package telegram.update.engine.services;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telegram.update.engine.entity.Session;

import static telegram.update.engine.generic.deserializer.MessageConfigDeserializer.mapJsonToMessageConfig;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceHandlerService {

    public static void fillMessageConfig(Session session, String jsonPath) {
        try {
            session.setMessageConfig(mapJsonToMessageConfig(jsonPath));
            log.info("Json parse success. Message is configured for user {}", session.getProfile().getFullName());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
