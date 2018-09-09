package hu.emanuel.jeremi.antitower.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceHandler implements MessageProvider {

	private final ResourceBundle res;
	private final String lang;
	private Locale loc;
	
	public ResourceHandler(String lang) {
		this.lang = lang;
		setLocale();
		res = ResourceBundle.getBundle("hu.emanuel.jeremi.antitower.i18n.GameTexts",loc);
	} 
	
	private final void setLocale() {
		if( lang.equalsIgnoreCase("HU") ) {
			loc = new Locale("hu","HU");			 
		}
		else if( lang.equalsIgnoreCase("EN") ) {
			loc = new Locale("en","US");
		} else {
			loc = new Locale("hu","HU");
		}
	}
	
	@Override
	public final String get(final String key) {
		return res.getString(key);
	}
	
	@Override
	public final String get(final int key) {
		return res.getString(String.valueOf(key));
	}

}
