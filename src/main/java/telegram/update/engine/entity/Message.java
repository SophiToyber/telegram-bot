package telegram.update.engine.entity;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import telegram.update.engine.entity.enums.Modifier;

@Data
@Builder
public class Message {
	private Session session;
    private Integer messageId;
    private Modifier modifier;

    private String text;
    private List<List<Button>> callbacks;
}
