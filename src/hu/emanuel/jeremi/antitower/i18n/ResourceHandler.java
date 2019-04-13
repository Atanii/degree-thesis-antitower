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
        res = ResourceBundle.getBundle("hu.emanuel.jeremi.antitower.i18n.GameTexts", loc);
    }

    private void setLocale() {
        if (lang.equalsIgnoreCase("HU")) {
            loc = new Locale("hu", "HU");
        } else if (lang.equalsIgnoreCase("EN")) {
            loc = new Locale("en", "US");
        } else {
            loc = new Locale("hu", "HU");
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

    @Override
    public final String getHelp() {
        String msg
                = get("move_forward") + "\n"
                + get("move_backward") + "\n"
                + get("move_left") + "\n"
                + get("move_right") + "\n"
                + get("turn_left") + "\n"
                + get("turn_right") + "\n"
                + get("shoot") + "\n"
                + get("inventory_slots") + "\n"
                + get("toggle_rain") + "\n"
                + get("use") + "\n";
        return msg;
    }

}
