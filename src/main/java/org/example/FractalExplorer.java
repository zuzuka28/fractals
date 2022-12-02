package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;


public class FractalExplorer
{
    /** ширина и высота отображения в пикселях. **/
    private final int displaySize;

    /**
     ссылка JImageDisplay, для обновления отображения в разных методах в процессе вычисления фрактала.
     */
    private final JImageDisplay display;

    /** ссылка на базовый класс для отображения других видов фракталов **/
    private final FractalGenerator fractal;

    /**
     * диапазон
     * то, что мы в сейчас показываем.
     */
    private final Rectangle2D.Double range;

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

    }

    /**
     * инициализирует графический интерфейс Swing с помощью JFrame, содержащего
     * Объект JImageDisplay и кнопку для очистки дисплея
     */
    public void createAndShowGUI()
    {
        /**  использование BorderLayout для содержимого. **/
        display.setLayout(new BorderLayout());
        JFrame myFrame = new JFrame("Fractal Explorer");

        /** центрирование **/
        myFrame.add(display, BorderLayout.CENTER);

        /** кнопка очистки. **/
        JButton resetButton = new JButton("Reset");
        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);

        /** хэндлер нажатий крысы **/
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        /** стандартный выход из приложения **/
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /** Jpanel сверху */
        JPanel myPanel = new JPanel();

        myFrame.add(myPanel, BorderLayout.NORTH);

        /** Кнопка ресета снизу */
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(resetButton);
        myFrame.add(myBottomPanel, BorderLayout.SOUTH);

        /** Размещаем содержимое фрейма, делаем его видимым и запрещаем изменение размера окна */
        myFrame.pack();
        myFrame.setVisible(true);
        myFrame.setResizable(false);

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
            /** получаем источник нажатия **/
            String command = e.getActionCommand();

            if (command.equals("Reset")) {
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

