package yoshino.app.analyzer;

/**
 * Created by yuki on 2016/5/17.
 */
public final class Debug
{
    public static final boolean DEBUG = true;

    public static void log(String debugMessage)
    {
        System.out.println("yoshino.app.analyzer.Debug Message: " + debugMessage);
    }
}
