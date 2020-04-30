package telegram.update.engine.entity.config;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import telegram.update.engine.entity.enums.ButtonType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ButtonConfig {
	private ButtonType type;
	@NotNull
	private LabelConfig label;
	private String url;
	private String action;
	@NotNull
	private String id;
	private String next;
	private String inputDataValidationMethodPath;
	private String dynamicValidationMethodPath;
	private Boolean multiClickable;
}
