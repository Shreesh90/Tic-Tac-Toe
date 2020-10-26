
package game.res;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Resources {
    
    public static BufferedImage[] letters;
    
    static {
        letters = new BufferedImage[2];
        letters[0] = loadImage("../../res/x.png");
        letters[1] = loadImage("../../res/o.png");
    }
    
    private static BufferedImage loadImage (String path){
        try {
            return ImageIO.read(Class.class.getResource(path));
        } catch (IOException e){
            System.exit(-1);
        }
        return null;
    }
    
}
