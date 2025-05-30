// Import necessary libraries

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

public class Brighten {
  public static void main(String[] args) throws IOException {
    // Store a list of JLabels that contain the output images
    JLabel[] images = new JLabel[8];

    // Read the input image from a filepath
    BufferedImage image = ImageIO.read(new File("src/digit.png"));

    // Set the first image in the array to the original image
    images[0] = new JLabel();
    images[0].setIcon(new ImageIcon(image));

    // Set the 2-8th images to be the original image scaled by a
    // brightness factor.
    // The brightness is increased to 110%, 120%, ..., 170% the original image.
    for (int i = 1; i < 8; i++) {
      RescaleOp op = new RescaleOp(1.0f + i * 0.1f, 0.0f, null);
      BufferedImage temp = op.filter(image, null);
      images[i] = new JLabel();
      images[i].setIcon(new ImageIcon(temp));
    }

    // Display the 8 images on a panel
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel panel = new JPanel(new GridLayout(4, 2));
    for (JLabel img : images) {
      panel.add(img);
    }
    frame.setSize(400, 400);
    frame.add(panel);
    frame.setVisible(true);
  }
}
