package Engine.Sound;

import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent.Type;

public class AudioPlayer {
    static ArrayList<Clip> activeClips = new ArrayList<>();

    public static Clip playAudio(AudioClip audioClip, boolean loop) {
        AudioPlayer.update();
        try {
            Clip clip = audioClip.createClip(loop);
            
            clip.start();

            activeClips.add(clip);

            return clip;
        } catch (Exception e) {
            System.err.println("Error playing audio: " + e.getMessage());
        }
        return null;
    }

    public static void update() {
        for (Iterator<Clip> it = activeClips.iterator(); it.hasNext();) {
            Clip clip = it.next();
            if (!clip.isActive()) {
                clip.close();
                it.remove();
            }
        }
}

    public static void stopAudio(Clip audioClip) {
        audioClip.stop();
        audioClip.close();
        activeClips.remove(audioClip);
    }

    public static void stopAll() {
        for (Clip clip : activeClips) {
            clip.stop();
            clip.close();
        }
        activeClips.clear();
    }
}
