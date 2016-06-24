import yoshino.app.analyzer.Analyzer;
import yoshino.app.analyzer.DependAnalyzer;

import java.util.TreeMap;

/**
 * Created by yuki on 2016/5/15.
 */
public class Main
{
    public static void main(String[] args) throws Analyzer.NotExistException, Analyzer.NotDirectoryException, Analyzer.EmptyDirectoryException
    {
//        Analyser analyser = new Analyser("C:\\Users\\yuki\\Desktop\\neko\\neko\\smali\\\\\\\\");
//        analyser.analyse();
//        // analyser.list();
//        analyser.dependAnalyze();

        Analyzer analyzer = new Analyzer("C:\\Users\\yuki\\Desktop\\neko\\neko\\smali\\");

        analyzer.recurseDirectory();

        analyzer.listClass();

        String[] analyzeClasses = {
                "Lyoshino/app/rgss/RGSSApplication;",
                "Lyoshino/app/rgss/SplashActivity;",
                "Lyoshino/app/rgss/ImagePainter;",
                "Lyoshino/app/rgss/RGSSActivity;",
                "Lyoshino/app/rgss/SDLActivity;",
                "Lyoshino/app/rgss/SplashActivity$1;"
        };

        DependAnalyzer dependAnalyzer = new DependAnalyzer(analyzeClasses, analyzer.getClassFileMap());

        dependAnalyzer.analyze();

        dependAnalyzer.listNotDependClasses();

        dependAnalyzer.createDeleteScriptForWindows("C:\\Users\\yuki\\Desktop");
    }
}
