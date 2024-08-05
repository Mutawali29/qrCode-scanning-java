import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class QRCodeDetection extends JFrame {

    private JLabel imageLabel;
    private JLabel resultLabel;
    private QRCodeProcessor qrCodeProcessor;

    public QRCodeDetection() {
        super("Deteksi QR Code");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        qrCodeProcessor = new QRCodeProcessor();

        imageLabel = new JLabel();
        resultLabel = new JLabel("QR Code Output: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Serif", Font.BOLD, 24));

        add(imageLabel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(800, 600));
        pack();
        setVisible(true);

        detectQRCode("src/qrImages/qrcodeGalon.jpg");
    }

    private void detectQRCode(String imagePath) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat inputImage = Imgcodecs.imread(imagePath);
        if (inputImage.empty()) {
            System.err.println("Error: Unable to read the image. Please check the file path.");
            return;
        }

        Mat outputImage = inputImage.clone();
        String decodedText = qrCodeProcessor.detectQRCodeInImage(inputImage, outputImage);

        if (!decodedText.isEmpty()) {
            resultLabel.setText("QR Code Output: " + decodedText);
        } else {
            resultLabel.setText("QR Code Output: Not detected");
        }

        BufferedImage image = qrCodeProcessor.convertMatToBufferedImage(outputImage);

        imageLabel.setIcon(new ImageIcon(image));
        imageLabel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QRCodeDetection::new);
    }
}
