import yoshino.app.analyzer.Debug;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by yuki on 2016/5/15.
 */
public class Analyser
{
    private String mSourceDir;

    private File mSourceDirFile;

    private HashSet<String> mClassSet;

    private HashMap<String, String> mFileMap;

    public Analyser(String sourceDir)
    {
        this.mClassSet = new HashSet<>();
        this.mFileMap = new HashMap<>();
        init(sourceDir);
    }

    private void init(String sourceDir)
    {
        File file = new File(sourceDir);

        if(Debug.DEBUG) {
            Debug.log(file.getAbsolutePath());
            try {
                Debug.log(file.getCanonicalPath());
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        if(file.exists() == false) {
            System.out.println("不存在\"" + sourceDir + "\"");
            return;
        }

        if(file.isDirectory() == false) {
            System.out.println("\"" + sourceDir + "\"不是文件夹");
            return;
        }

        mSourceDir = file.getAbsolutePath() + "\\";
        mSourceDirFile = file;

        File[] files = file.listFiles();
        if(file == null) {
            System.out.println("文件夹\"" + sourceDir + "\"下没有文件或文件夹");
            return;
        }
    }

    public void analyse()
    {
        enumerateDir(mSourceDirFile, 1);
    }

    private void enumerateDir(File dir, int level)
    {
        if(dir.exists() == false) {
            return;
        }

        File[] files = dir.listFiles();
        if(files == null) {
            return;
        }

        for(File aFile : files) {
            if(aFile.isFile()) {
                // System.out.println("F/" + aFile);
                String str = aFile.toString();
                // str = str.substring(sourceDir.length() + offset, str.lastIndexOf(".smali"));
                str = str.replace(mSourceDir, "L");
                str = str.replace(".smali", ";");
                str = str.replaceAll("\\\\", "/");
                mClassSet.add(str);
                mFileMap.put(str, aFile.getAbsolutePath());
            } else {
                // System.out.println("D/" + aFile);
                enumerateDir(aFile, level + 1);
            }
        }
    }

    public void depend()
    {
        Dependent.analyse(mClassSet, mFileMap);
    }

    public void dependAnalyze()
    {
        String[] analyzeClasses = {
                "Lyoshino/app/rgss/RGSSApplication;",
                "Lyoshino/app/rgss/SplashActivity;",
                "Lyoshino/app/rgss/ImagePainter;",
                "Lyoshino/app/rgss/RGSSActivity;",
                "Lyoshino/app/rgss/SDLActivity;",
                "Lyoshino/app/rgss/SplashActivity$1;"
        };
        DependAnalyzer2 analyzer = new DependAnalyzer2(analyzeClasses, mClassSet, mFileMap);
        analyzer.dependAnalyze();
        analyzer.printResult();
        analyzer.createDeleteScriptForWindows("C:\\Users\\yuki\\Desktop");
    }

    public void list()
    {
        Iterator<String> iterator = mClassSet.iterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
//            String str = iterator.next();
//            System.out.println(str + " --> " + mFileMap.get(str));
        }
        Debug.log("total " + mClassSet.size() + " classes.");
    }
}
