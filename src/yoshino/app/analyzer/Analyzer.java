package yoshino.app.analyzer;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;
import java.util.TreeMap;

/**
 * Created by yuki on 2016/5/18.
 */
public class Analyzer
{
    private TreeMap<String, String> mClassFileMap = null;

    private File mSourceDirectoryFile = null;

    private String mSourceDirectoryPath = null;

    public Analyzer(String sourceDir) throws NotExistException, NotDirectoryException
    {
        File file = new File(sourceDir);

        if (!file.exists()) {
            throw new NotExistException(sourceDir + " not exist!");
        }

        if (!file.isDirectory()) {
            throw new NotDirectoryException(sourceDir + " not a directory!");
        }

        mSourceDirectoryFile = file;

        mSourceDirectoryPath = file.getAbsolutePath() + "\\";

        mClassFileMap = new TreeMap<>();
    }

    public void recurseDirectory() throws EmptyDirectoryException
    {
        File[] files = mSourceDirectoryFile.listFiles();

        if (files == null) {
            throw new EmptyDirectoryException(mSourceDirectoryFile + " is a empty directory!");
        }

        Stack<File> aFileStack = new Stack<>();
        for (File file : files) {
            aFileStack.push(file);
        }

        while (!aFileStack.empty()) {
            File aFile = aFileStack.pop();
            if (aFile.isFile()) {
                String filePath = aFile.getAbsolutePath();
                String str = filePath.replace(mSourceDirectoryPath, "L");
                str = str.replace(".smali", ";");
                str = str.replaceAll("\\\\", "/");
                if (str.matches(AnalyzerUtils.CLASS_PATTERN)) {
                    mClassFileMap.put(str, filePath);
                }
            }

            if (aFile.isDirectory()) {
                File[] files1 = aFile.listFiles();
                if (files1 == null) {
                    continue;
                }
                for (File file1 : files1) {
                    aFileStack.push(file1);
                }
            }
        }
    }

    public TreeMap<String, String> getClassFileMap()
    {
        return mClassFileMap;
    }

    public void listClass()
    {
        long t = System.currentTimeMillis();
        Iterator<String> iterator = mClassFileMap.keySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println("total " + mClassFileMap.size() + " classes!");
        System.out.println("time used " + (System.currentTimeMillis() - t) + " ms!");
    }

    public class NotDirectoryException extends Exception
    {
        public NotDirectoryException(String exceptionMessage)
        {
            super(exceptionMessage);
        }
    }

    public class NotExistException extends Exception
    {
        public NotExistException(String exceptionMessage)
        {
            super(exceptionMessage);
        }
    }

    public class EmptyDirectoryException extends Exception
    {
        public EmptyDirectoryException(String exceptionMessage)
        {
            super(exceptionMessage);
        }
    }
}
