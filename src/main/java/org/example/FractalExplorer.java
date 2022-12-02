package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;


public class FractalExplorer
{
    /** ширина и высота отображения в пикселях. **/
    private final int displaySize;

    /**
     ссылка JImageDisplay, для обновления отображения в разных методах в процессе вычисления фрактала.
     */
    private final JImageDisplay display;

    /** ссылка на базовый класс для отображения других видов фракталов **/
    private FractalGenerator fractal;

    /**
     * диапазон
     * то, что мы в сейчас показываем.
     */
    private Rectangle2D.Double range;

    /** набор фракталов, которые можно отрисовать */
    private final JComboBox fractalCollection;

    /**
     * принимает значение размера отображения в качестве аргумента
     * затем сохраняет это значение в соответствующем поле
     * инициализирует объекты диапазона и фрактального генератора
     */
    public FractalExplorer(int size) {
        displaySize = size;
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);
        fractalCollection = new JComboBox<>();
    }

    /**
     * инициализирует графический интерфейс Swing с помощью JFrame, содержащего
     * Объект JImageDisplay и кнопку для очистки дисплея
     */
    public void createAndShowGUI()
    {
        JFrame mainFrame = new JFrame("Fractal Generator");

        fractalCollection.addItem(new Mandelbrot());
        fractalCollection.addItem(new Tricorn());
        fractalCollection.addItem(new BurningShip());
        fractalCollection.addActionListener(new ButtonHandler());

        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        JButton resetButton = new JButton("Reset");
        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);

        JButton saveButton = new JButton("Save");
        ButtonHandler saveHandler = new ButtonHandler();
        saveButton.addActionListener(saveHandler);

        JPanel topPanel = new JPanel();
        JLabel label = new JLabel("Fractals:");
        topPanel.add(label, BorderLayout.CENTER);
        topPanel.add(fractalCollection, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(resetButton, BorderLayout.CENTER);
        bottomPanel.add(saveButton, BorderLayout.CENTER);

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(display, BorderLayout.CENTER);
        mainFrame.add(topPanel, BorderLayout.NORTH);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.pack();
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);
    }

    private void drawFractal()
    {
        /**Проходим через каждый пиксель на дисплее **/
        for (int x=0; x<displaySize; x++){
            for (int y=0; y<displaySize; y++){

                /**
                 * Находим соответствующие координаты xCoord и yCoord
                 * в области отображения фрактала.
                 */
                double xCoord = FractalGenerator.getCoord(
                        range.x,
                        range.x + range.width,
                        displaySize,
                        x);
                double yCoord = FractalGenerator.getCoord(
                        range.y,
                        range.y + range.height,
                        displaySize,
                        y);

                /**
                 * Вычисляем количество итераций для координат в области для отображения фрактала.
                 */
                int iteration = fractal.numIterations(xCoord, yCoord);

                /** если вне множества фрактала то красим черным **/
                if (iteration == -1){
                    display.drawPixel(x, y, 0);
                }

                else {
                    /** выбираем значение оттенка на основе числа итераций */
                    float hue = 0.6f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);

                    /** обновляем дисплей цветом для каждого пикселя. **/
                    display.drawPixel(x, y, rgbColor);
                }

            }
        }
        /** обновим отображаемое на дисплее */
        display.repaint();
    }
    private class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getActionCommand().equals("Reset")) {
                fractal.getInitialRange(range);
                drawFractal();
            } else if (e.getActionCommand().equals("Save")) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Image", "png");
                chooser.setFileFilter(filter);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION) {
                    try {
                        ImageIO.write(
                                display.getRenderedImage(),
                                "png",
                                chooser.getSelectedFile().getName().matches(".*[.png]") ? chooser.getSelectedFile() : new File( chooser.getSelectedFile() + ".png"));
                    } catch (NullPointerException | IOException writeE) {
                        JOptionPane.showMessageDialog(display, writeE.getMessage(), "Cannot save image", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                fractal = (FractalGenerator) fractalCollection.getSelectedItem();
                range = new Rectangle2D.Double(0,0,0,0);
                fractal.getInitialRange(range);
                drawFractal();
            }

        }
    }
    private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            /** получаем корды нажатия **/
            int x = e.getX();
            double xCoord = FractalGenerator.getCoord(range.x,
                    range.x + range.width, displaySize, x);
            int y = e.getY();
            double yCoord = FractalGenerator.getCoord(range.y,
                    range.y + range.height, displaySize, y);

            /** приближаем диапазон в направлении нажатия */
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            /** перерысовываем */
            drawFractal();
        }
    }

    public static void main(String[] args)
    {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}

