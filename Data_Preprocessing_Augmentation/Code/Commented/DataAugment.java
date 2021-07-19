// Import necessary libraries

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DataAugment {
  // Not the best/standard practice to throw exceptions, but
  // this program is meant for demonstration purposes of
  // data augmentation, not writing perfect code
  // that handles every error.
  public static void main(String[] args) throws IOException {
    // Store a counter that helps create files. Names: output1, output2, ...
    int counter = 1;

    // Create an object that stores the input image
    BufferedImage image = ImageIO.read(new File("src/digit.png"));

    // Perform shearing/skewing operations.
    // Note that i and j adjust the amount of x-shearing and y-shearing.
    // Shearing basically tilts an image (that is rectangular) by a
    // certain angle, to create another image (which now is technically a parallelogram).
    for (double i = -0.2; i <= 0.2; i += 0.05) {
      for (double j = -0.2; j <= 0.2; j += 0.05) {
        // Create another object to store the output image, which is later written
        // into an output file.
        BufferedImage output = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);

        // Create a graphics object. It is used to perform the shearing transformation.
        Graphics2D graphics2D = output.createGraphics();

        // Set the transformation to shear by a certain amount.
        // Note that this function call appears before we draw an image,
        // because we are technically setting a configuration
        // such that painting operations (like drawImage()) that follow
        // have the transformation applied to it.
        graphics2D.shear(i, j);

        // Draw the original image, but perform the shearing transformation on it.
        graphics2D.drawImage(image, 0, 0, null);

        // Create a file object that stores the output file's path.
        // We use the counter here so that each file is named
        // 'output1', 'output2', and so forth.
        // Note that we store these files in another folder called 'output-images'.
        // IMPORTANT: The folder must exist before running the program.
        File outFile = new File("output-images/output" + counter + ".png");

        // Write the image into the file in the PNG format
        ImageIO.write(output, "png", outFile);

        // Increment the counter (which stores which file is currently being
        // written into).
        counter++;
      }
    }
  }
}
