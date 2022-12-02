package org.example;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JImageDisplay extends javax.swing.JComponent {
    public BufferedImage displayImage;

    public JImageDisplay(int width, int height) {
        displayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        /** setPreferredSize() устанавливает размер в формате Dimension */
        Dimension imageDimension = new Dimension(width, height);
        super.setPreferredSize(imageDimension);
    }

    public BufferedImage getRenderedImage(){
        return this.displayImage;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage (displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight(), null);
    }

    /** устанавливает все пиксели изображения в черный цвет (значение RGB 0) */
    public void clearImage () {
        displayImage.setRGB(0, 0, 0);
    }

    /** устанавливает пиксель определенного цвета. */
    public void drawPixel(int x, int y, int rgbColor)
    {
        displayImage.setRGB(x, y, rgbColor);
    }
}
