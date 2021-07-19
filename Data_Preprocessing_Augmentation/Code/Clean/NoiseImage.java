import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class NoiseImage {
  public static void main(String[] args) throws IOException {
    BufferedImage image = ImageIO.read(new File("src/digit.png"));
    BufferedImage output = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
    Random random = new Random();
    for (int i = 0; i < output.getHeight(); i++) {
      for (int j = 0; j < output.getWidth(); j++) {
        Color color = new Color(image.getRGB(j, i), true);
        if (isTransparent(image.getRGB(j, i))) {
          color = new Color(255, 255, 255);
        }
        int choice = random.nextInt(2);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();

        int rand = random.nextInt(50);
        if (choice == 0) {
          r += rand;
          g += rand;
          b += rand;
          a += rand;
        } else {
          r -= rand;
          g -= rand;
          b -= rand;
          a -= rand;
        }

        r = isOutOfBounds(r);
        g = isOutOfBounds(g);
        b = isOutOfBounds(b);
        a = isOutOfBounds(a);
        output.setRGB(j, i, new Color(r, g, b, a).getRGB());
      }
    }
    File outFile = new File("output.png");
    ImageIO.write(output, "png", outFile);
  }

  private static int isOutOfBounds(int val) {
    if (val > 255) {
      return 255;
    }
    return Math.max(val, 0);
  }

  private static boolean isTransparent(int val) {
    return val >> 24 == 0;
  }
}
