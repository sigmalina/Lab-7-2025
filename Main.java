import functions.*;
import functions.basic.*;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n Тестирование итераторов ");
        testIterators();

        System.out.println("\n Тестирование фабрик");
        testFactories();

        System.out.println("\n Тестирование рефлексии ");
        testReflection();
    }

    private static void testIterators() {
        System.out.println("ArrayTabulatedFunction:");
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16});
        for (FunctionPoint p : arrayFunc) {
            System.out.println("  " + p);
        }

        System.out.println("LinkedListTabulatedFunction:");
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 4, new double[]{0, 1, 4, 9, 16});
        for (FunctionPoint p : listFunc) {
            System.out.println("  " + p);
        }

        System.out.println("Явный итератор:");
        Iterator<FunctionPoint> iterator = arrayFunc.iterator();
        while (iterator.hasNext()) {
            System.out.println("  " + iterator.next());
        }
    }

    private static void testFactories() {
        System.out.println("Фабрика по умолчанию (Array):");
        TabulatedFunction tf1 = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, 5);
        System.out.println("   Функция: " + tf1);

        System.out.println("Установка LinkedList фабрики:");
        TabulatedFunctions.setTabulatedFunctionFactory(
                new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        TabulatedFunction tf2 = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, 5);
        System.out.println("   Функция: " + tf2);

        System.out.println("Возврат к Array фабрике:");
        TabulatedFunctions.setTabulatedFunctionFactory(
                new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        TabulatedFunction tf3 = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, 5);
        System.out.println("   Функция: " + tf3);
    }

    private static void testReflection() {
        System.out.println("Тестирование рефлексии ");

        System.out.println("1. Создание ArrayTabulatedFunction через рефлексию:");
        TabulatedFunction f1 = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println("   Класс: " + f1.getClass().getSimpleName());
        System.out.println("   Значения: " + f1);

        System.out.println("2. Создание ArrayTabulatedFunction через рефлексию:");
        TabulatedFunction f2 = TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println("   Класс: " + f2.getClass().getSimpleName());
        System.out.println("   Значения: " + f2);

        System.out.println("3. Создание через рефлексию из массива точек:");
        TabulatedFunction f3 = TabulatedFunctions.createTabulatedFunction(
                LinkedListTabulatedFunction.class,
                new FunctionPoint[] {new FunctionPoint(0, 0), new FunctionPoint(10, 10)});
        System.out.println("   Класс: " + f3.getClass().getSimpleName());
        System.out.println("   Значения: " + f3);

        System.out.println("4. Табулирование с рефлексией:");
        TabulatedFunction f4 = TabulatedFunctions.tabulate(
                LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println("   Класс: " + f4.getClass().getSimpleName());
        System.out.println("   Значения: " + f4);
    }
}