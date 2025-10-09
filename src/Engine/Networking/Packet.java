package Engine.Networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import Engine.GameObject;

public class Packet {

    public enum PacketType {
        Connected,
        Disconnected,
        Data
    }

    PacketType packetType;
    byte[] data;
    ArrayList<GameObject> gameOjbects = new ArrayList<GameObject>();

    public Packet(byte[] bytes) {
        data = bytes;
        try {
            deserializeData(bytes);
        } catch (Exception e) {
            System.out.println("Couldn't deserialize data");;
        }
        
    }

    public Packet(PacketType packetType, ArrayList<GameObject> gameObjects) {
        this.packetType = packetType;
        try {
            serializeData(gameObjects);
        } catch (Exception e) {
            data = new byte[0];
            System.out.println("Couldn't serialize gameObjects");;
        }
    }

    public ArrayList<GameObject> getGameObjects() {
        return gameOjbects;
    }

    public byte[] getBytes() {
        return data;
    }

    private void serializeData(ArrayList<GameObject> gameObjects) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write packet to array
        dos.writeInt(packetType.ordinal());

        // Write object count
        dos.writeInt(gameObjects.size());

        // Serialize all objects
        List<byte[]> serializedObjects = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            serializedObjects.add(obj.toBytes());
        }

        // Calculate and write start indecies
        int currentOffset = 0;
        for (byte[] objBytes : serializedObjects) {
            dos.writeInt(currentOffset);
            currentOffset += objBytes.length;
        }

        // Write all GameObjects
        for (byte[] objBytes : serializedObjects) {
            dos.write(objBytes);
        }

        dos.flush();
        data = baos.toByteArray();
    }

    private void deserializeData(byte[] data) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        // Read packet type
        int packetOrdinal = dis.readInt();
        packetType = PacketType.values()[packetOrdinal];

        // Read object count
        int objectCount = dis.readInt();

        // Read offsets
        int[] offsets = new int[objectCount];
        for (int i = 0; i < objectCount; i++) {
            offsets[i] = dis.readInt();
        }

        // Calculate where object bytes start
        int headerSize = 4 + 4 + 4 * objectCount; // packetType + objectCount + offsets
        byte[] objectData = Arrays.copyOfRange(data, headerSize, data.length);

        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        for (int i = 0; i < objectCount; i++) {
            int start = offsets[i];
            int end = (i == objectCount - 1) ? objectData.length : offsets[i + 1];

            byte[] objBytes = Arrays.copyOfRange(objectData, start, end);
            GameObject obj = GameObject.fromBytes(objBytes);
            objects.add(obj);
        }

        gameOjbects = objects;
    }

}
