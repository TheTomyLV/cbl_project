package Engine.Sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;

public class AudioClip {
    private AudioFormat format;
    private byte[] audioData;
    private Clip clip;

    public AudioClip(String filePath) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filePath));
            AudioFormat baseFormat = stream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );
            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, stream);
            audioData = decodedStream.readAllBytes();
            format = decodedFormat;
            clip = createClip(false);
            clip.setFramePosition(clip.getFrameLength());
            clip.start();
        } catch (Exception e) {
            System.err.println("Failed to load audio clip : " + e.getMessage());
        }

    }

    public Clip createClip(boolean loop) throws LineUnavailableException, IOException {
        Clip clip = AudioSystem.getClip();
        clip.open(format, audioData, 0, audioData.length);
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        return clip;
    }
}
