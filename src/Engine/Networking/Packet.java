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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import Engine.GameObject;

public class Packet {

    byte[] data;
    ArrayList<GameObject> gameOjbects = new ArrayList<GameObject>();
    UUID id;

    public Packet(byte[] bytes) {
        data = bytes;
        try {
            deserializeData(bytes);
        } catch (Exception e) {
            System.out.println("Couldn't deserialize data");
        }
    }

    public Packet(UUID senderId, HashMap<ClientData, ArrayList<GameObject>> gameObjects) {
        try {
            serializeData(senderId, gameObjects);
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

    private void serializeData(UUID id, HashMap<ClientData, ArrayList<GameObject>> gameObjects) throws IOException {
        if (gameObjects == null) {
            return;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // Write id
        dos.writeLong(id.getMostSignificantBits());
        dos.writeLong(id.getLeastSignificantBits());

        // Serialize all objects
        List<byte[]> serializedObjects = new ArrayList<>();
        for (Iterator<ArrayList<GameObject>> it = gameObjects.values().iterator(); it.hasNext();) {
            for (GameObject obj : it.next()) {
                serializedObjects.add(obj.toBytes());
            }
        }

        // Write object count
        dos.writeInt(serializedObjects.size());

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

        // Read senderId
        long most = dis.readLong();
        long least = dis.readLong();
        id  = new UUID(most, least);

        // Read object count
        int objectCount = dis.readInt();

        // Read offsets
        int[] offsets = new int[objectCount];
        for (int i = 0; i < objectCount; i++) {
            offsets[i] = dis.readInt();
        }

        // Calculate where object bytes start
        int headerSize = 16 + 4 + 4 * objectCount; // packetType + objectCount + offsets
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
