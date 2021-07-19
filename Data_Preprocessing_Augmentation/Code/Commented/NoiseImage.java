// Import necessary libraries

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class NoiseImage {
  public static void main(String[] args) throws IOException {
    // Read an image from a filepath
    BufferedImage image = ImageIO.read(new File("src/digit.png"));

    // Initialize an output image of the same width and height encoded in ARGB format.
    BufferedImage output = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);

    // Random object to generate noise values
    Random random = new Random();

    // For each pixel in output, get its corresponding color on the input image
    // (which has an alpha filter) and check whether it is transparent.
    // If it is, then set the color to white, otherwise, set the
    // R, G, B, A values to those found on the original pixel.
    // Have a random choice between 0 and 1 decide whether a random
    // value between 0-49 is subtracted from or added to the original
    // pixel's color. Then, check whether the values for RGBA are
    // < 0 or > 255, in which case it is set to 0 and 255 respectively.
    // Finally, set the corresponding pixel on the output image
    // to the new color using the RGBA values.
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

    // Initialize the file to which the new image is written to.
    File outFile = new File("output.png");

    // Write the output image to the file.
    ImageIO.write(output, "png", outFile);
  }

  // Check if a value is > 255 or < 0. If it is > 255, set value to 255.
  // Otherwise, if value < 0, set value to 0.
  // Note that the value is returned, not directly changed.
  // Also, if the pixel is in normal range (0-255), the Math.max(val, 0) will
  // return the normal pixel's value.
  private static int isOutOfBounds(int val) {
    if (val > 255) {
      return 255;
    }
    return Math.max(val, 0);
  }

  // Check if a pixel is transparent.
  // This function uses the '>>' operator, which shifts the binary representation
  // of 'val' to the right 24 times. If that value is equal to 0, then
  // output true. For example, RGBA int color '-1023410176' (rgba: 0, 0, 0, 195)
  // can be represented in binary as '-111101000000000000000000000000',
  // which when shifted 24 times to the right produces '-111101', which isn't 0,
  // therefore the pixel must be non-transparent.
  private static boolean isTransparent(int val) {
    return val >> 24 == 0;
  }
}
