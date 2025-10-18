package Engine.Networking;

import Engine.GameObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class Packet {

    byte[] data;
    ArrayList<GameObject> gameOjbects = new ArrayList<GameObject>();
    UUID id;

    public Packet(byte[] bytes) {
        data = bytes;
        try {
            deserializeData(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Packet(UUID senderId, HashMap<ClientData, ArrayList<GameObject>> gameObjects) {
        try {
            serializeData(senderId, gameObjects);
        } catch (Exception e) {
            data = new byte[0];
            e.printStackTrace();
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

        // Get object count
        int objectCount = 0;
        for (Iterator<ArrayList<GameObject>> it = gameObjects.values().iterator(); it.hasNext();) {
            objectCount += it.next().size();
        }

        dos.writeInt(objectCount);

        // Serialize objects
        for (Iterator<ArrayList<GameObject>> it = gameObjects.values().iterator(); it.hasNext();) {
            ArrayList<GameObject> entityGameObjects = it.next();
            for (int i = 0; i < entityGameObjects.size(); i++) {
                entityGameObjects.get(i).toOutputStream(dos);
            }
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

        ArrayList<GameObject> objects = new ArrayList<GameObject>();
        for (int i = 0; i < objectCount; i++) {
            GameObject obj = GameObject.fromInputStream(dis);
            objects.add(obj);
        }

        gameOjbects = objects;
    }

}
