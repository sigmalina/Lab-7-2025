package threads;

import functions.Function;
import functions.basic.Log;
import java.util.Random;

public class SimpleGenerator implements Runnable {
    private final Task task;

    public SimpleGenerator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        Random random = new Random();

        for (int i = 0; i < task.getTasks(); i++) {
            synchronized (task) {
                // логарифмическая функция со случайным основанием от 1 до 10
                double base = 1 + random.nextDouble() * 9;
                Function function = new Log(base);

                // левая граница [0, 100]
                double leftX = random.nextDouble() * 100;

                // правая граница [100, 200]
                double rightX = 100 + random.nextDouble() * 100;

                // шаг дискретизации [0.01, 1]
                double step = 0.01 + random.nextDouble() * 0.99;

                // установка данных в задание
                task.setFunction(function);
                task.setLeftX(leftX);
                task.setRightX(rightX);
                task.setStep(step);

                System.out.println("Source " + leftX + " " + rightX + " " + step);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println("Generator прерван");
                break;
            }
        }
    }
}