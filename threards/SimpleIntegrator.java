package threads;
import functions.Function;
import functions.Functions;
import java.util.Random;

public class SimpleIntegrator implements Runnable {
    private final Task task;

    public SimpleIntegrator(Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        int processed = 0;
        while (processed < task.getTasks()) {
            synchronized (task) {
                Function function = task.getFunction();
                double leftX = task.getLeftX();
                double rightX = task.getRightX();
                double step = task.getStep();

                if (function != null) {
                    try {
                        double result = Functions.integrate(function, leftX, rightX, step);
                        System.out.println("Result " + leftX + " " + rightX + " " + step + " " + result);
                        processed++; // увеличиваем только при успешной обработке
                    } catch (Exception e) {
                        // игнорируем ошибки
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}