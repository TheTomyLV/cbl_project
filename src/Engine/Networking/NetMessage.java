package Engine.Networking;

import Engine.Vector2;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A network message class to allow communication between server and client.
 */
public class NetMessage {
    public String type;
    public Object[] data;

    /**
     * Creates a new message.
     * @param type message type
     * @param data message arguments
     */
    public NetMessage(String type, Object[] data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Writes NetMessage to the given output stream.
     * @param dos DataOutputStream
     * @throws IOException when there is a problem writing to the output stream
     */
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

    /**
     * Reads the given input stream to get the NetMessage.
     * @param dis DataInputStream
     * @return NetMessage
     * @throws IOException when there is a problem with reading the input stream
     */
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
