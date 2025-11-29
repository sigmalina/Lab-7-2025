package threads;

import functions.Functions;
import java.util.concurrent.Semaphore;

public class Integrator extends Thread {
    private Task task;
    private Semaphore semaphore;

    public Integrator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasks() && !isInterrupted(); i++) {
                semaphore.acquire();

                if (!task.isProcessed() && task.getFunction() != null) {
                    // Вычисляем интеграл только если задача еще не обработана
                    double result = Functions.integrate(
                            task.getFunction(),
                            task.getLeftX(),
                            task.getRightX(),
                            task.getStep()
                    );

                    System.out.println("Result " + task.getLeftX() + " " +
                            task.getRightX() + " " + task.getStep() + " " + result);

                    task.setProcessed(true); // Помечаем как обработанную
                }

                semaphore.release();
            }
        } catch (InterruptedException e) {
            System.out.println("Integrator was interrupted");
        } catch (Exception e) {
            System.out.println("Integrator error: " + e.getMessage());
        }
    }
}