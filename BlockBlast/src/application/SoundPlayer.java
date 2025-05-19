package application;

import javafx.scene.media.AudioClip;

public class SoundPlayer {
    public static void play(String resource) {
        try {
            java.net.URL url = SoundPlayer.class.getResource(resource);
            if (url == null) {
                System.err.println("Ses dosyası bulunamadı: " + resource);
                return;
            }
            AudioClip clip = new AudioClip(url.toExternalForm());
            clip.play();
        } catch (Exception e) {
            System.err.println("Ses çalınamadı: " + resource + " - " + e.getMessage());
        }
    }
    public void preloadSounds() {
        javafx.scene.media.AudioClip clip;

        clip = new javafx.scene.media.AudioClip(getClass().getResource("/sounds/pickup.wav").toExternalForm());
        clip.setVolume(0.0);
        clip.play();

        clip = new javafx.scene.media.AudioClip(getClass().getResource("/sounds/place.wav").toExternalForm());
        clip.setVolume(0.0);
        clip.play();

        clip = new javafx.scene.media.AudioClip(getClass().getResource("/sounds/clear.wav").toExternalForm());
        clip.setVolume(0.0);
        clip.play();

        clip = new javafx.scene.media.AudioClip(getClass().getResource("/sounds/levelup.wav").toExternalForm());
        clip.setVolume(0.0);
        clip.play();
    }

}
