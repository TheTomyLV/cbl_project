package Engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * Class for managing images for gameObject rendering.
 */
public class Sprite {
    
    static HashMap<Integer, BufferedImage> images = new HashMap<>();
    static HashMap<String, Integer> fileIndexMap = new HashMap<>();
    /**
     * Loads image and stores it in cache for multiple uses.
     * @param path path to image
     * @return bufferedImage
     */
    public static BufferedImage getImage(String path) {
        if (!fileIndexMap.containsKey(path)) {
            return null;
        }

        int index = fileIndexMap.get(path);
        return images.get(index);
    }

    private static BufferedImage loadImage(Path path) {
        try {
            BufferedImage image = ImageIO.read(path.toFile());
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void loadAssets(String directory) {

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    loadAssets(path.toString());
                } else {
                    BufferedImage image = loadImage(path);

                    if (image != null) {
                        images.put(fileIndexMap.size(), image);
                        fileIndexMap.put(path.toString(), fileIndexMap.size());
                    }


                }
            }
        } catch (Exception e) {
            return;
        }
    }
}
