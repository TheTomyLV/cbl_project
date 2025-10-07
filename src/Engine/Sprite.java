package Engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 * Class for managing images for gameObject rendering.
 */
public class Sprite {
    
    static HashMap<String, BufferedImage> images = new HashMap<String, BufferedImage>();

    /**
     * Loads image and stores it in cache for multiple uses.
     * @param path path to image
     * @return bufferedImage
     */
    public static BufferedImage getImage(String path) {
        if (images.containsKey(path)) {
            return images.get(path);
        }

        try {
            BufferedImage image = ImageIO.read(new File(path));
            images.put(path, image);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
