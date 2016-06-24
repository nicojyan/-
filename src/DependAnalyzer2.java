import yoshino.app.analyzer.Debug;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuki on 2016/5/17.
 */
public class DependAnalyzer2
{
    private HashSet<String> mClassSet;
    private HashSet<String> mAllDependSet;
    private HashMap<String, String> mFileMap;
    private HashMap<String, HashSet<String>> mDependMap;
    private String[] mAnalyzeClasses;

    public DependAnalyzer2(String[] analyzeClasses, HashSet<String> aClassSet, HashMap<String, String> aFileMap)
    {
        mClassSet = aClassSet;
        mFileMap  = aFileMap;
        mAnalyzeClasses = analyzeClasses;
        mAllDependSet = new HashSet<>();
        mDependMap = new HashMap<>();
    }

    public void dependAnalyze()
    {
        for(String str : mAnalyzeClasses)
        dependAnalyze(str);
    }

    private void dependAnalyze(String aClass)
    {
        if(!mClassSet.contains(aClass) && !mAllDependSet.contains(aClass)) {
            System.out.println(aClass + "not exist!");
        }

        if(!mFileMap.containsKey(aClass)) {
            System.out.println("file for " + aClass + " not exist!");
        }

        if(mDependMap.containsKey(aClass)) {
            return;
        }

        String fileName = mFileMap.get(aClass);

        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            fileInputStream = new FileInputStream(fileName);
            byte[] buffer = new byte[1024 * 64];
            int readCount = 0;
            while((readCount = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, readCount);
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch(IOException e) {
            e.printStackTrace();
            return;
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String fileText = new String(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());

        Pattern classPattern = Pattern.compile("L([0-9a-zA-Z$]+/)+[0-9a-zA-Z$]+;");
        Matcher matcher = classPattern.matcher(fileText);
        HashSet<String> dependentSet = new HashSet<>();
        while(matcher.find()) {
            String str = matcher.group();
            if(Debug.DEBUG) {
                Debug.log("find value: " + str);
            }
            if(mAllDependSet.contains(str)) {
                if(!str.equals(aClass)) {
                    dependentSet.add(str);
                }
            }
            if(mClassSet.contains(str)) {
                mAllDependSet.add(str);
                mClassSet.remove(str);
                if(!str.equals(aClass)) {
                    dependentSet.add(str);
                }
            }
        }

        mDependMap.put(aClass, dependentSet);

        Iterator<String> iterator = dependentSet.iterator();
        while(iterator.hasNext()) {
            dependAnalyze(iterator.next());
        }
    }

    public void printResult()
    {
        // list(mAnalyzeClasses, new HashSet<String>(), 0);
        String str = null;
        for(String s : mAnalyzeClasses) {
            str += s + ", ";
        }
        System.out.println(str + "not depend on these " + mClassSet.size() + " classes");
        System.out.println();
        Iterator<String> iterator = mClassSet.iterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    private void list(String aClass, HashSet<String> before, int level)
    {
        if(before.contains(aClass)) {
            return;
        }
        final String bank = "    ";
        for(int i = 0; i < level; i++) {
            System.out.print(bank);
        }
        System.out.println(aClass);
        HashSet<String> before2 = new HashSet<>();
        before2.addAll(before);
        before2.add(aClass);
        Iterator<String> iterator = mDependMap.get(aClass).iterator();
        while(iterator.hasNext()) {
            list(iterator.next(), before2, level + 1);
        }
    }

    public void createDeleteScriptForWindows(String path)
    {
        FileOutputStream fileOutputStream = null;
        File file = new File(path, "delete.bat");
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            Iterator<String> iterator = mClassSet.iterator();
            while(iterator.hasNext()) {
                fileOutputStream.write(("del \"" + mFileMap.get(iterator.next()) + "\"\r\n").getBytes());
            }
            fileOutputStream.flush();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
