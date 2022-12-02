package org.example;

import java.awt.geom.Rectangle2D;

public class Tricorn extends FractalGenerator {

    public static final int MAX_ITERATIONS = 2000;

    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2;
        range.width = 4;
        range.height = 4;
    }

    @Override
    public int numIterations(double x, double y) {
        int iteration = 0;
        /** начальная точка и точка дополнительная */
        ComplexNumber point = new ComplexNumber();
        ComplexNumber c = new ComplexNumber(x, y);

        while (iteration < MAX_ITERATIONS && point.squareRadius() < 4)
        {
            point = ComplexNumber.add(ComplexNumber.square(point).conjugate(), c);
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
    @Override
    public String toString() {
        return "Tricorn";
    }
}
