package yoshino.app.analyzer;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuki on 2016/5/18.
 */
public class DependAnalyzer
{
    private TreeMap<String, String> mClassFileMap = null;

    private TreeSet<String> mClassSet = null;

    private TreeSet<String> mAllDependSet = null;

    private TreeMap<String, TreeSet<String>> mDependMap = null;

    private String[] mNeedAnalyzeClasses = null;

    public DependAnalyzer(String[] needAnalyzeClasses, TreeMap<String, String> aClassFileMap)
    {
        mClassFileMap = new TreeMap<>();
        mClassFileMap.putAll(aClassFileMap);
        mNeedAnalyzeClasses = needAnalyzeClasses;
        mClassSet = new TreeSet<>();
        mClassSet.addAll(aClassFileMap.keySet());
        mAllDependSet = new TreeSet<>();
        mDependMap = new TreeMap<>();
    }

    public void analyze()
    {
        if (Debug.DEBUG) {
            System.out.println(mClassSet.size());
        }

        Stack<String> aClassStack = new Stack<>();

        for (String aClass : mNeedAnalyzeClasses) {
            if (mClassSet.contains(aClass)) {
                aClassStack.push(aClass);
                mAllDependSet.add(aClass);
                mClassSet.remove(aClass);
                if (Debug.DEBUG) {
                    System.out.println("add " + aClass);
                }
            }
        }

        Pattern classPattern = Pattern.compile(AnalyzerUtils.CLASS_PATTERN);

        while (!aClassStack.empty()) {
            String aClass = aClassStack.pop();
            if(mDependMap.containsKey(aClass)) {
                continue;
            }
            if (!mClassFileMap.containsKey(aClass)) {
                if (Debug.DEBUG) {
                    System.out.println("not containsKey " + aClass);
                }
                continue;
            }
            String fileName = mClassFileMap.get(aClass);
            String fileText = readFileContents(fileName);
//            if(Debug.DEBUG) {
//                Debug.log("read content: " + fileText);
//            }
            Matcher matcher = classPattern.matcher(fileText);
            TreeSet<String> dependentSet = new TreeSet<>();
            while(matcher.find()) {
                String str = matcher.group();
//                if(Debug.DEBUG) {
//                    Debug.log("find value: " + str);
//                }
                if(mClassSet.contains(str)) {
                    dependentSet.add(str);
                    aClassStack.push(str);
                    mAllDependSet.add(str);
                    mClassSet.remove(str);
                } else if (mAllDependSet.contains(str)) {
                    dependentSet.add(str);
                    aClassStack.push(str);
                }
            }
            mDependMap.put(aClass, dependentSet);
        }
    }

    public void listNotDependClasses()
    {
        long t = System.currentTimeMillis();
        Iterator<String> iterator = mClassSet.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        System.out.println("total " + mClassSet.size() + " classes not depend!");
        System.out.println("time used " + (System.currentTimeMillis() - t) + " ms!");
    }

    private String readFileContents(String path)
    {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            fileInputStream = new FileInputStream(path);
            byte[] buffer = new byte[1024 * 64];
            int readCount = 0;
            while((readCount = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, readCount);
            }
            byteArrayOutputStream.flush();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            return new String(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
        }
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

    public TreeMap<String, String> getClassFileMap()
    {
        return mClassFileMap;
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
                fileOutputStream.write(("del \"" + mClassFileMap.get(iterator.next()) + "\"\r\n").getBytes());
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
