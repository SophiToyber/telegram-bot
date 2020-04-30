package telegram.update.engine.utils;

import static telegram.update.engine.utils.ExposedResourceBundleMessageSource.LOCALE_MESSAGES;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.context.MessageSource;

public class LocalizationService {
	
	 private static MessageSource messageSource;

	    static {
	        ExposedResourceBundleMessageSource rs = new ExposedResourceBundleMessageSource();
	        rs.setBasenames(LOCALE_MESSAGES);
	        rs.setAlwaysUseMessageFormat(false);
	        rs.setUseCodeAsDefaultMessage(true);
	        rs.setDefaultEncoding(StandardCharsets.UTF_8.name());
	        messageSource = rs;
	    }

	    public static String getMessage(String key, Locale locale, Object... args) {
	        return messageSource.getMessage(key, args, locale);
	    }
	
}
