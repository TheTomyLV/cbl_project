package Engine.Networking;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Network {
    private static Map<String, Consumer<Object>> handlers = new HashMap<>();
    private static Map<String, Class<?>[]> methodParamTypes = new HashMap<>();
    private static Map<Integer, String> indexMap = new HashMap<>();
    private static Map<String, Integer> typeMap = new HashMap<>();

    public static <T> void registerHandler(String type, Class<?>[] paramTypes, Consumer<T> handler) {
        handlers.put(type, (Consumer<Object>) handler);
        methodParamTypes.put(type, paramTypes);
        int index = indexMap.size();
        indexMap.put(index, type);
        typeMap.put(type, index);
    }

    public static String getTypeFromIndex(int index) {
        return indexMap.get(index);
    }

    public static int getIndexFromName(String name) {
        return typeMap.get(name);
    }

    public static Class<?>[] getParamTypes(String name) {
        return methodParamTypes.get(name);
    }

    public static void onMessageReceived(NetMessage msg) {
        Consumer<Object> handler = handlers.get(msg.type);
        if (handler != null) {
            handler.accept(msg.data);
        } else {
            System.out.println("No handler for type: " + msg.type);
        }
    }

    public static void registerHandlersFromClass(Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(NetEvent.class)) {
                NetEvent annotation = method.getAnnotation(NetEvent.class);
                String messageType = annotation.value();

                // Check that it's static and has one parameter
                if (!Modifier.isStatic(method.getModifiers())) {
                    System.err.println("Method " + method.getName() + " must be static.");
                    continue;
                }
                if (method.getParameterCount() != 1) {
                    System.err.println("Method " + method.getName() + " must have exactly one parameter.");
                    continue;
                }

                // Get parameter type
                Class<?> paramType = method.getParameterTypes()[0];
                Class<?>[] paramTypes = method.getParameterTypes();

                // Register the handler
                registerHandler(messageType, paramTypes, (Object data) -> {
                    try {
                        // Optionally, cast data to correct type
                        method.invoke(null, paramType.cast(data));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                System.out.println("Registered handler: " + messageType + " → " + cls.getSimpleName() + "." + method.getName());
            }
        }
    }
}
