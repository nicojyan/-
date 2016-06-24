import yoshino.app.analyzer.Debug;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuki on 2016/5/17.
 */
public class Dependent
{
    public static void analyse(HashSet<String> aClassSet, HashMap<String, String> aFileMap)
    {
        System.out.println("input the class you wanna to analyse");
        String aClass = new Scanner(System.in).nextLine();
        if(aClassSet.contains(aClass) == false || aFileMap.containsKey(aClass) == false) {
            System.out.println(aClass + " not exist.");
            return;
        }

        String fileName = aFileMap.get(aClass);

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

//        if(yoshino.app.analyzer.Debug.DEBUG) {
//            System.out.println();
//            System.out.println(fileText);
//            System.out.println();
//        }

        // Pattern classPattern = Pattern.compile("L([0-9z-zA-Z$]+/)+([0-9z-zA-Z$]+);");
        Pattern classPattern = Pattern.compile("L([0-9a-zA-Z$]+/)+[0-9a-zA-Z$]+;");
        Matcher matcher = classPattern.matcher(fileText);
        HashSet<String> dependentSet = new HashSet<>();
        while(matcher.find()) {
            String str = matcher.group();
            if(Debug.DEBUG) {
                Debug.log("find value: " + str);
            }
            if(aClassSet.contains(str)) {
                dependentSet.add(str);
            }
        }

        System.out.println(aClass + " depend on these " + dependentSet.size() + " classes:");
        Iterator<String> iterator = dependentSet.iterator();
        while(iterator.hasNext()) {
            System.out.println("    " + iterator.next());
        }
    }
}
