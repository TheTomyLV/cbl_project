package Engine.Networking;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;

import Engine.GameObject;

public class NetworkHandleRegister {
    public static void registerAllGameObjectHandlers(String basePackage) {
        try {
            List<Class<?>> classes = getClassesRecursive(basePackage);

            for (Class<?> cls : classes) {
                if (GameObject.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                    Network.registerHandlersFromClass(cls);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Class<?>> getClassesRecursive(String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return Collections.emptyList();
        }

        File directory = new File(resource.getFile());
        if (!directory.exists()) {
            return Collections.emptyList();
        }

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                // Recurse into subpackages
                classes.addAll(getClassesRecursive(packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (Throwable ignored) {
                    // you can print ignored.getMessage() for debugging if needed
                }
            }
        }

        return classes;
    }
}