package i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Localization {
	public static final Locale ENGLISH = Locale.ENGLISH;
	public static final Locale SPANISH = new Locale("es");
	private static final String BUNDLE_NAME = "messages";

	private Locale locale;
	private ResourceBundle bundle;

	public Localization() {
		setLocale(ENGLISH);
	}

	public Localization(Locale locale) {
		setLocale(locale);
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		if (!ENGLISH.equals(locale) && !SPANISH.equals(locale)) {
			throw new IllegalArgumentException("Unsupported locale: " + locale);
		}
		this.locale = locale;
		bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

	public void toggleLocale() {
		setLocale(ENGLISH.equals(locale) ? SPANISH : ENGLISH);
	}

	public String text(String key) {
		return bundle.getString(key);
	}

	public String format(String key, Object... arguments) {
		MessageFormat formatter = new MessageFormat(text(key), locale);
		return formatter.format(arguments);
	}
}
