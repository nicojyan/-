import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by yuki on 2016/5/17.
 */
public class ClassSet
{
    private HashSet<String> mClassSet = new HashSet<>();

    public void add(String aSmaliFile)
    {
        aSmaliFile = aSmaliFile.substring(0, aSmaliFile.indexOf(".smali"));
        aSmaliFile = aSmaliFile.replaceAll("\\\\", "/");
        mClassSet.add(aSmaliFile);
    }

    public void list()
    {
        Iterator<String> iterator = mClassSet.iterator();
        int count = 0;
        long t = System.currentTimeMillis();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
            count ++;
        }
        t = System.currentTimeMillis() - t;
        System.out.println("time used " + t + " ms.");
        System.out.println("total " + count + " classes.");
//        for(String str : mClassSet) {
//            System.out.println(str);
//        }
    }

    public void list2()
    {
        int count = 0;
        long t = System.currentTimeMillis();
        for(String str : mClassSet) {
            System.out.println(str);
            count ++;
        }
        t = System.currentTimeMillis() - t;
        System.out.println("time used " + t + " ms.");
        System.out.println("total " + count + " classes.");
    }
}
