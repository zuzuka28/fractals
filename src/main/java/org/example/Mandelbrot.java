package org.example;

import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator {

    public static final int MAX_ITERATIONS = 2000;

    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }

    @Override
    public int numIterations(double x, double y) {
        int iteration = 0;
        /** начальная точка и точка дополнительная */
        ComplexNumber point = new ComplexNumber();
        ComplexNumber c = new ComplexNumber(x, y);

        /** Zn = Zn-1 ^ 2 + c */
        while (iteration < MAX_ITERATIONS && point.squareRadius() < 4)
        {
            point = ComplexNumber.add(ComplexNumber.square(point), c);
            iteration += 1;
        }

        /**
         * если количество максимальных итераций достигнуто, возвращаем -1, чтобы указать, что точка не вышла за границу множества
         */
        if (iteration == MAX_ITERATIONS)
        {
            return -1;
        }

        return iteration;
    }
}
