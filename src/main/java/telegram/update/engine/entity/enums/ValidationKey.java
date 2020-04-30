package telegram.update.engine.entity.enums;

public enum ValidationKey {

	NAME_EDITED("message.name.edited"), 
	PHONE_EDITED("message.phone.edited"),
	EMAIL_EDITED("message.email.correct");

	private String key;

	private ValidationKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}
}