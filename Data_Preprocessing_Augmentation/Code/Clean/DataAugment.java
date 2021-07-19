import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DataAugment {
  public static void main(String[] args) throws IOException {
    int counter = 1;
    BufferedImage image = ImageIO.read(new File("src/digit.png"));
    for (double i = -0.2; i <= 0.2; i += 0.05) {
      for (double j = -0.2; j <= 0.2; j += 0.05) {
        BufferedImage output = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = output.createGraphics();
        graphics2D.shear(i, j);
        graphics2D.drawImage(image, 0, 0, null);

        File outFile = new File("output-images/output" + counter + ".png");
        ImageIO.write(output, "png", outFile);
        counter++;
      }
    }
  }
}
