package functions;
import java.io.Serializable;

public class FunctionPoint implements Serializable, Cloneable {
    private static final double EPSILON = 1e-10;

    double x;
    double y;

    // конструктор с параметрами
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // копирующий конструктор
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    // конструктор по умолчанию
    public FunctionPoint() {
        x = 0;
        y = 0;
    }

    // геттеры и сеттеры
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    @Override
    public String toString() {
        return "(" + x + "; " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FunctionPoint that = (FunctionPoint) obj;

        // сравнение double с машинным эпсилоном
        return Math.abs(that.x - x) < EPSILON &&
                Math.abs(that.y - y) < EPSILON;
    }

    @Override
    public int hashCode() {
        // преобразуем координаты x и y в битовое представление
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);

        // разбиваем каждое 64 битное число на две 32 битные части
        int xPart1 = (int)(xBits);          // младшие 32 бита x
        int xPart2 = (int)(xBits >>> 32);   // старшие 32 бита x

        int yPart1 = (int)(yBits);          // младшие 32 бита y
        int yPart2 = (int)(yBits >>> 32);   // старшие 32 бита y

        return xPart1 ^ xPart2 ^ yPart1 ^ yPart2;
    }

    @Override
    public Object clone() {
        return new FunctionPoint(this);
    }
}