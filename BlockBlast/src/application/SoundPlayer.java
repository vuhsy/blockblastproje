//package application;

package application;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Gerçek ses oynatma işlemini yapan sınıf.
 * Hem SoundEffectPlayer arayüzünü implemente eder,
 * hem de eski projeyi bozmamak için statik play(String) metodunu korur.
 */
public class SoundPlayer implements SoundEffectPlayer {

    /** Eski kodu bozmamak için bırakıldı: SoundPlayer.play("/sounds/clear.wav"); */
    public static void play(String resource) {
        new SoundPlayer().playSound(resource);
    }

    /** Arayüzün gerektirdiği yeni metod: */
    @Override
    public void playSound(String resource) {
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

