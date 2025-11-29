package functions;

import functions.meta.*;

public final class Functions {
    private Functions() {
        throw new AssertionError("Нельзя создать экземпляр утилитного класса");
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }


    public static double integrate(Function function, double leftBound, double rightBound, double step) {
        if (leftBound >= rightBound) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг должен быть положительным");
        }
        if (leftBound < function.getLeftDomainBorder() || rightBound > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал выходит за область определения функции");
        }

        double integral = 0.0;
        double current = leftBound;

        // вычисление интеграла методом трапеций
        while (current < rightBound) {
            double next = Math.min(current + step, rightBound);
            double fCurrent = function.getFunctionValue(current);
            double fNext = function.getFunctionValue(next);

            // площадь трапеции (f(a) + f(b)) * (b - a) / 2
            double area = (fCurrent + fNext) * (next - current) / 2.0;
            integral += area;

            current = next;
        }

        return integral;
    }
}