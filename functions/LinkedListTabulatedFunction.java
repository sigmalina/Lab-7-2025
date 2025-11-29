package functions;
import java.io.*;
import java.util.Iterator;

public class LinkedListTabulatedFunction implements TabulatedFunction {
    private static final double EPSILON = 1e-10;

    // внутренний класс для узлов списка
    private class FunctionNode {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        // конструктор
        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }

    private FunctionNode head; // голова списка
    private int pointsCount;

    // конструкторы
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) throw new IllegalArgumentException("Left border >= right border");
        if (pointsCount < 2) throw new IllegalArgumentException("Less than 2 points");

        initHead();
        double xStep = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * xStep;
            double y = 0;
            addNodeToTail().point = new FunctionPoint(x, y);
        }
        this.pointsCount = pointsCount;
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) throw new IllegalArgumentException("Left border >= right border");
        if (values.length < 2) throw new IllegalArgumentException("Less than 2 points");

        initHead();
        double xStep = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * xStep;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
        this.pointsCount = values.length;
    }

    // конструктор из массива точек
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        // проверка упорядоченности
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() - points[i-1].getX() < -EPSILON) {
                throw new IllegalArgumentException("Точки не упорядочены по возрастанию X");
            }
        }

        initHead();
        for (FunctionPoint point : points) {
            addNodeToTail().point = new FunctionPoint(point);
        }
        this.pointsCount = points.length;
    }

    // конструктор по умолчанию для клонирования
    public LinkedListTabulatedFunction() {
        initHead();
    }

    // инициализация головы списка
    private void initHead() {
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        pointsCount = 0;
    }

    // добавление узла в конец
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode tail = head.prev;

        tail.next = newNode;
        newNode.prev = tail;
        newNode.next = head;
        head.prev = newNode;

        pointsCount++;
        return newNode;
    }

    // добавление узла по индексу
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        if (index == pointsCount) {
            return addNodeToTail();
        }

        FunctionNode newNode = new FunctionNode(null);
        FunctionNode target = getNodeByIndex(index);
        FunctionNode prevNode = target.prev;

        prevNode.next = newNode;
        newNode.prev = prevNode;
        newNode.next = target;
        target.prev = newNode;

        pointsCount++;
        return newNode;
    }

    // удаление узла по индексу
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        FunctionNode toDelete = getNodeByIndex(index);
        toDelete.prev.next = toDelete.next;
        toDelete.next.prev = toDelete.prev;
        pointsCount--;

        return toDelete;
    }

    // получение узла по индексу
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        FunctionNode current = head.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // реализация методов Function
    @Override
    public double getLeftDomainBorder() {
        return pointsCount > 0 ? getPointX(0) : Double.NaN;
    }

    @Override
    public double getRightDomainBorder() {
        return pointsCount > 0 ? getPointX(pointsCount - 1) : Double.NaN;
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        // проверка точного совпадения
        FunctionNode current = head.next;
        while (current != head) {
            if (Math.abs(current.point.getX() - x) < 1e-10) {
                return current.point.getY();
            }
            current = current.next;
        }

        // поиск интервала и интерполяция
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = getPointX(i);
            double x2 = getPointX(i + 1);
            if (x >= x1 && x <= x2) {
                double y1 = getPointY(i);
                double y2 = getPointY(i + 1);
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }

        return Double.NaN;
    }

    // реализация методов TabulatedFunction
    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        setPointX(index, point.getX());
        setPointY(index, point.getY());
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index > 0 && x <= getPointX(index - 1))
            throw new InappropriateFunctionPointException("X must be greater than previous point");
        if (index < pointsCount - 1 && x >= getPointX(index + 1))
            throw new InappropriateFunctionPointException("X must be less than next point");

        getNodeByIndex(index).point.setX(x);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    @Override
    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    @Override
    public void deletePoint(int index) {
        if (pointsCount < 3) throw new IllegalStateException("Cannot delete point - less than 3 points left");
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на дублирование
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(getPointX(i) - point.getX()) < EPSILON)
                throw new InappropriateFunctionPointException("Point with this X already exists");
        }

        int insertIndex = 0;
        while (insertIndex < pointsCount && getPointX(insertIndex) < point.getX()) {
            insertIndex++;
        }

        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        FunctionNode current = head.next;
        while (current != head) {
            sb.append("(").append(current.point.getX()).append("; ").append(current.point.getY()).append(")");
            if (current.next != head) {
                sb.append(", ");
            }
            current = current.next;
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

        // оптимизация для LinkedListTabulatedFunction
        if (o instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction listOther = (LinkedListTabulatedFunction) o;
            FunctionNode currentThis = this.head.next;
            FunctionNode currentOther = listOther.head.next;

            while (currentThis != head && currentOther != listOther.head) {
                // сравниваем точки через equals
                if (!currentThis.point.equals(currentOther.point)) {
                    return false;
                }
                currentThis = currentThis.next;
                currentOther = currentOther.next;
            }
        } else {
            // общий случай для любого TabulatedFunction
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
        FunctionNode current = head.next;
        while (current != head) {
            result ^= current.point.hashCode();
            current = current.next;
        }
        return result;
    }

    @Override
    public Object clone() {
        // создаем новый список из копий точек
        FunctionPoint[] pointsCopy = new FunctionPoint[pointsCount];
        FunctionNode current = head.next;

        for (int i = 0; i < pointsCount; i++) {
            pointsCopy[i] = new FunctionPoint(current.point); // используем копирующий конструктор
            current = current.next;
        }

        return new LinkedListTabulatedFunction(pointsCopy);
    }

    @Override
    public Iterator<FunctionPoint> iterator() {
        return new Iterator<FunctionPoint>() {
            private FunctionNode currentNode = head.next; // начинаем с ПЕРВОГО реального узла

            @Override
            public boolean hasNext() {
                return currentNode != head; // проверяем, что не дошли до головы (не прошли полный круг)
            }

            @Override
            public FunctionPoint next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException("No more elements");
                }
                // Создаем копию для защиты инкапсуляции
                FunctionPoint original = currentNode.point;
                FunctionPoint copy = new FunctionPoint(original.getX(), original.getY());
                currentNode = currentNode.next;
                return copy;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove operation is not supported");
            }
        };
    }
    //воженный класс
    public static class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
            return new LinkedListTabulatedFunction(leftX, rightX, pointsCount);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        }

        @Override
        public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
            return new LinkedListTabulatedFunction(points);
        }
    }
}