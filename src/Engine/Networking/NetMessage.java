package Engine.Networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import Engine.Vector2;

public class NetMessage {
    public String type;
    public Object[] data;

    public NetMessage(String type, Object[] data) {
        this.type = type;
        this.data = data;
    }

    public void toOutputStream(DataOutputStream dos) throws IOException {
        dos.writeInt(Network.getIndexFromName(type));

        // Serialize data
        Class<?>[] paramTypes = Network.getParamTypes(type);
        for (int i = 0; i < data.length; i++) {
            Class<?> paramType = paramTypes[0];
            if (paramType == int.class || paramType == Integer.class) {
                dos.writeInt((Integer) data[i]);
            } else if (paramType == float.class || paramType == Float.class) {
                dos.writeFloat((Float) data[i]);
            } else if (paramType == double.class || paramType == Double.class) {
                dos.writeDouble((Double) data[i]);
            } else if (paramType == boolean.class || paramType == Boolean.class) {
                dos.writeBoolean((Boolean) data[i]);
            } else if (paramType == Vector2.class) {
                Vector2 v = (Vector2) data[i];
                dos.writeFloat(v.x);
                dos.writeFloat(v.y);
            } else {
                System.err.println("Could not conver: " + paramType.getClass());
            }
        }
    }

    public static NetMessage fromInputStream(DataInputStream dis) throws IOException {
        int index = dis.readInt();
        String type = Network.getTypeFromIndex(index);

        Class<?>[] paramTypes = Network.getParamTypes(type);
        Object[] result = new Object[paramTypes.length];
        // Deserialize data
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> paramType = paramTypes[0];
            if (paramType == int.class || paramType == Integer.class) {
                result[i] = dis.readInt();
            } else if (paramType == float.class || paramType == Float.class) {
                result[i] = dis.readFloat();
            } else if (paramType == double.class || paramType == Double.class) {
                result[i] = dis.readDouble();
            } else if (paramType == boolean.class || paramType == Boolean.class) {
                result[i] = dis.readBoolean();
            } else if (paramType == Vector2.class) {
                float x = dis.readFloat();
                float y = dis.readFloat();
                result[i] = new Vector2(x, y);
            } else {
                System.err.println(paramType.getClass() + "is not serializable");
                result[i] = null;
            }
        }
        return new NetMessage(type, result);
    }
}
