import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;

public class DumpMethods {
    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\uesrk\\.gradle\\caches\\ng_execute\\0344f4fc400fc472bc1a77f83cd09b727ed739b9c2974225dfaa79950789372c\\transformed\\");
        URL[] urls = {file.toURI().toURL()};
        URLClassLoader loader = new URLClassLoader(urls);
        Class<?> clazz = loader.loadClass("net.minecraft.util.datafix.DataFixTypes");
        for (Method m : clazz.getDeclaredMethods()) {
            System.out.println(m);
        }
    }
}
