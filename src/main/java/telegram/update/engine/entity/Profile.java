package telegram.update.engine.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Profile {
    private Long id;
    private String fullName;
    private String userName;
    private String phone;
    private String languageCode;
    private boolean bot;
}
