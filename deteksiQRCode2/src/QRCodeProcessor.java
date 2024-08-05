import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class QRCodeProcessor {

    public String detectQRCodeInImage(Mat inputImage, Mat outputImage) {
        QRCodeDetector qrCodeDetector = new QRCodeDetector();
        Mat points = new Mat();
        String decodedText = qrCodeDetector.detectAndDecode(inputImage, points);

        if (!decodedText.isEmpty()) {
            System.out.println("QR Code Detected: " + decodedText);
            if (!points.empty()) {
                for (int i = 0; i < points.rows(); i++) {
                    Point pt1 = new Point(points.get(i, 0));
                    Point pt2 = new Point(points.get((i + 1) % points.rows(), 0));
                    Imgproc.line(outputImage, pt1, pt2, new Scalar(0, 255, 0), 3);
                }
                drawMultilineText(outputImage, decodedText, new Point(points.get(0, 0)[0], points.get(0, 0)[1] - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 2);
            }
        } else {
            System.out.println("QR Code not detected");
        }

        return decodedText;
    }

    private void drawMultilineText(Mat img, String text, Point org, int fontFace, double fontScale, Scalar color, int thickness) {
        int baseLine[] = new int[1];
        int yOffset = 0;
        int maxWidth = img.cols() - (int) org.x;
        String[] lines = splitTextIntoLines(text, maxWidth, fontFace, fontScale, thickness);

        for (String line : lines) {
            Size textSize = Imgproc.getTextSize(line, fontFace, fontScale, thickness, baseLine);
            Imgproc.putText(img, line, new Point(org.x, org.y + yOffset), fontFace, fontScale, color, thickness);
            yOffset += textSize.height + baseLine[0] + 5;
        }
    }

    private String[] splitTextIntoLines(String text, int maxWidth, int fontFace, double fontScale, int thickness) {
        StringBuilder line = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        int baseLine[] = new int[1];

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            Size textSize = Imgproc.getTextSize(testLine, fontFace, fontScale, thickness, baseLine);
            if (textSize.width > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line.append(line.length() == 0 ? word : " " + word);
            }
        }

        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines.toArray(new String[0]);
    }

    public BufferedImage convertMatToBufferedImage(Mat frame) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (frame.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = frame.channels() * frame.cols() * frame.rows();
        byte[] b = new byte[bufferSize];
        frame.get(0, 0, b);
        BufferedImage image = new BufferedImage(frame.cols(), frame.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
}
