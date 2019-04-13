package hu.emanuel.jeremi.antitower.i18n;

public interface MessageProvider {

    public String get(final String key);

    public String get(final int key);

    public String getHelp();

}
