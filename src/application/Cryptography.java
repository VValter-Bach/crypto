package application;

import com.sun.tools.javac.Main;
import configuration.Configuration;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Cryptography {
    private boolean cracker;
    private Class c;

    public Cryptography(String algorithm) {
        try {
            cracker = algorithm.contains("cracker");
            URL[] urls = {new File(Configuration.instance.componentDirectory + "\\" + algorithm + ".jar").toURI().toURL()};
            URLClassLoader loader = new URLClassLoader(urls, Main.class.getClassLoader());
            c = Class.forName(algorithm, true, loader);
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    public String Execute(String methodName, String message, String Filename) {
        String out = null;
        try {
            Method method = c.getDeclaredMethod(methodName, String.class, File.class);
            File file = new File(Configuration.instance.keyDirectory + Filename);
            out = (String) method.invoke(c, message, file);
        } catch (Exception e) {
            System.out.println("Error calling the function");
            return null;
        }
        return out;
    }

    public String Execute(String message) {
        String out;
        try {
            Method method = c.getDeclaredMethod("decrypt", String.class);
            out = (String) method.invoke(c, message);
        } catch (Exception e) {
            System.out.println("Error calling the function");
            return null;
        }
        return out;
    }
}
