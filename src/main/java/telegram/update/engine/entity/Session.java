package telegram.update.engine.entity;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import lombok.Builder;
import lombok.Data;
import telegram.update.engine.entity.config.MessageConfig;

@Data
@Builder
public class Session {

    private Locale locale;
    private Profile profile;
    private String inputData;
    private String callbackData;
    private Integer lastMessageId;
    private MessageConfig messageConfig;

    private Button activeButton;
    @Builder.Default
    private Set<Button> currentButtons = new HashSet<>();
}
