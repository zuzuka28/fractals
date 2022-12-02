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

    private final JButton resetButton = new JButton("Reset");
    private final JButton saveButton = new JButton("Save");
    private int rowsRemaining;


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

        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);

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

    public void enableGUI(boolean b) {
        saveButton.setEnabled(b);
        resetButton.setEnabled(b);
        fractalCollection.setEnabled(b);
    }

    private void drawFractal()
    {
        enableGUI(false);
        rowsRemaining = displaySize;
        for (int i = 0; i < displaySize; i++) {
            FractalWorker drawRow = new FractalWorker(i);
            drawRow.execute();
        }
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
            if (rowsRemaining == 0) {
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
    }
    public class FractalWorker extends SwingWorker<Object, Object> {
        private final int yCoordinate;
        private int[] rgb;

        public FractalWorker(int y) {
            this.yCoordinate = y;
        }

        @Override
        protected Object doInBackground() {
            rgb = new int[displaySize];

            /** Итерируемся по пикселям строки **/
            for (int i = 0; i < displaySize; i++) {
                    /**
                     * Находим соответствующие координаты xCoord и yCoord
                     * в области отображения фрактала.
                     */
                    double xCoord = FractalGenerator.getCoord(range.x,range.x + range.width, displaySize, i);
                    double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, displaySize, yCoordinate);

                    /**
                     * Вычисляем количество итераций для координат в области для отображения фрактала.
                     */
                    int iteration = fractal.numIterations(xCoord, yCoord);

                    /** если вне множества фрактала то красим черным **/
                    if (iteration == -1){
                        rgb[i] = 0;
                    }

                    else {
                        /** выбираем значение оттенка на основе числа итераций */
                        float hue = 0.6f + (float) iteration / 200f;
                        int rgb = Color.HSBtoRGB(hue, 1f, 1f);
                        this.rgb[i] = rgb;
                    }

                }
            return null;
        }
        @Override
        protected void done() {
            for (int i = 0; i < displaySize; i++) {
                display.drawPixel(i, yCoordinate, rgb[i]);
            }
            display.repaint(0,0, yCoordinate,displaySize,1);
            rowsRemaining--;
            if (rowsRemaining == 0) {
                enableGUI(true);
            }
        }
    }

    public static void main(String[] args)
    {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}

