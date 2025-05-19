package application;

import javax.sound.sampled.*;
import java.io.*;

public class SoundPlayer {
    public static void play(String resource) {
        try {
            InputStream audioSrc = SoundPlayer.class.getResourceAsStream(resource);
            if (audioSrc == null) {
                System.err.println("Ses dosyası bulunamadı: " + resource);
                return;
            }
            BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception e) {
            System.err.println("Ses çalınamadı: " + resource + " - " + e.getMessage());
        }
    }
}
