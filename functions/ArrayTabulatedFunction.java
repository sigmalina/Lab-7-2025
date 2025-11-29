package functions;
import java.io.*;
import java.util.Iterator;

public class ArrayTabulatedFunction implements TabulatedFunction{
    private static final double EPSILON = 1e-10;

    private FunctionPoint[] points;
    private int pointsCount;

    // конструктор без параметров
    public ArrayTabulatedFunction() {
        this.pointsCount = 0;
        this.points = new FunctionPoint[10];
    }

    // конструктор равномерное распределение с у=0
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 10];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    // конструктор равномерное распределение с заданными у
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 10];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    // конструктор из массива точек
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        // проверка упорядоченности с машинным эпсилоном
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() - points[i-1].getX() < -EPSILON) {
                throw new IllegalArgumentException("Точки не упорядочены по возрастанию X");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 10];

        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    // реализация методов интерфейса Function
    @Override
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    @Override
    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        // проверяем точное совпадение
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - x) < 1e-10) {
                return points[i].getY();  // возвращаем Y существующей точки
            }
        }

        // если точного совпадения нет - ищем интервал для интерполяции
        int i = 0;
        while (i < pointsCount - 1 && points[i + 1].getX() < x) {
            i++;
        }

        // делаем линейную интерполяцию
        FunctionPoint leftPoint = points[i];
        FunctionPoint rightPoint = points[i + 1];

        double k = (rightPoint.getY() - leftPoint.getY()) / (rightPoint.getX() - leftPoint.getX());
        return k * (x - leftPoint.getX()) + leftPoint.getY();
    }

    // реализация методов интерфейса TabulatedFunction
    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return new FunctionPoint(points[index]);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }

        if ((index > 0 && point.getX() <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && point.getX() >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException("Нарушена упорядоченность точек");
        }

        points[index] = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return points[index].getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }

        if ((index > 0 && x <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && x >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException("Нарушена упорядоченность точек");
        }

        points[index].setX(x);
    }

    @Override
    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return points[index].getY();
    }

    @Override
    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        points[index].setY(y);
    }

    @Override
    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалить точку: меньше 3 точек");
        }

        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на дублирование
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
        }

        // увеличение массива при необходимости
        if (pointsCount >= points.length) {
            FunctionPoint[] newArray = new FunctionPoint[pointsCount + 10];
            System.arraycopy(points, 0, newArray, 0, pointsCount);
            points = newArray;
        }

        // поиск позиции для вставки
        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }

        // сдвиг элементов
        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pointsCount; i++) {
            sb.append("(").append(points[i].getX()).append("; ").append(points[i].getY()).append(")");
            if (i < pointsCount - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false;

        TabulatedFunction other = (TabulatedFunction) o;

        if (this.getPointsCount() != other.getPointsCount()) return false;

        // Оптимизация для ArrayTabulatedFunction
        if (o instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction arrayOther = (ArrayTabulatedFunction) o;
            for (int i = 0; i < pointsCount; i++) {
                // Сравниваем точки через equals (уже исправлен с эпсилон)
                if (!this.points[i].equals(arrayOther.points[i])) {
                    return false;
                }
            }
        } else {
            // Общий случай для любого TabulatedFunction
            for (int i = 0; i < pointsCount; i++) {
                FunctionPoint myPoint = this.getPoint(i);
                FunctionPoint otherPoint = other.getPoint(i);
                if (!myPoint.equals(otherPoint)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = pointsCount;
        for (int i = 0; i < pointsCount; i++) {
            result ^= points[i].hashCode();
        }
        return result;
    }

    @Override
    public Object clone() {
        // создаем копию через конструктор
        FunctionPoint[] pointsCopy = new FunctionPoint[this.pointsCount];
        for (int i = 0; i < this.pointsCount; i++) {
            pointsCopy[i] = new FunctionPoint(this.points[i]);
        }
        return new ArrayTabulatedFunction(pointsCopy);
    }

    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < pointsCount;
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException("No more elements");
                }
                // Создаем копию для защиты инкапсуляции
                FunctionPoint original = points[currentIndex];
                FunctionPoint copy = new FunctionPoint(original.getX(), original.getY());
                currentIndex++;
                return copy;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }
    // вложенный класс фабрики
    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new ArrayTabulatedFunction(points);
        }
    }
}