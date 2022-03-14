import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Exporter {
  public static void main(String[] args) throws IOException {
    Map<String, Integer> labelNames = new HashMap<>();
    labelNames.put("airplane", 0);
    labelNames.put("automobile", 1);
    labelNames.put("bird", 2);
    labelNames.put("cat", 3);
    labelNames.put("deer", 4);
    labelNames.put("dog", 5);
    labelNames.put("frog", 6);
    labelNames.put("horse", 7);
    labelNames.put("ship", 8);
    labelNames.put("truck", 9);

    BufferedReader labels = new BufferedReader(new FileReader("CIFAR-Data/trainLabels.csv"));
    labels.readLine();

    double[][] data = new double[50000][3072];
    double[][] target = new double[50000][10];

    File[] files = new File("CIFAR-Data/train").listFiles();
    assert files != null;
    Arrays.sort(files);
    int index = 0;
    for (File file : files) {
      if (index != 0 && index % 1000 == 0) {
        System.out.println(index + " / " + files.length);
      }
      BufferedImage bufferedImage = ImageIO.read(file);
      data[index] = getPixels(bufferedImage);
      target[index][labelNames.get(labels.readLine().split(",")[1])] = 1;
      index++;
    }

    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("input-cifar10.ser"));
    objectOutputStream.writeObject(data);
    objectOutputStream.close();

    objectOutputStream = new ObjectOutputStream(new FileOutputStream("target-cifar10.ser"));
    objectOutputStream.writeObject(target);
    objectOutputStream.close();

  }

  private static double[] getPixels(BufferedImage bufferedImage) {
    double[] vals = new double[bufferedImage.getWidth() * bufferedImage.getHeight() * 3];
    int[] pixel;
    int index = 0;
    for (int y = 0; y < bufferedImage.getHeight(); y++) {
      for (int x = 0; x < bufferedImage.getWidth(); x++) {
        pixel = bufferedImage.getRaster().getPixel(x, y, new int[3]);
        vals[index] = pixel[0] / 255.0;
        vals[index + 1] = pixel[1] / 255.0;
        vals[index + 2] = pixel[2] / 255.0;
        index += 3;
      }
    }
    return vals;
  }
}
