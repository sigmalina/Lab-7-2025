package threads;

import functions.basic.Log;
import java.util.concurrent.Semaphore;

public class Generator extends Thread {
    private Task task;
    private Semaphore semaphore;

    public Generator(Task task, Semaphore semaphore) {
        this.task = task;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < task.getTasks() && !isInterrupted(); i++) {
                semaphore.acquire();

                // Создаем новую задачу только если предыдущая уже обработана
                if (task.isProcessed() || task.getFunction() == null) {
                    task.setFunction(new Log(1 + (Math.random() * 9)));
                    task.setLeftX(Math.random() * 100);
                    task.setRightX(Math.random() * 100 + 100);
                    task.setStep(Math.random());
                    task.setProcessed(false); // сбрасываем флаг для новой задачи

                    System.out.println("Source " + task.getLeftX() + " " +
                            task.getRightX() + " " + task.getStep());
                }

                semaphore.release();
            }
        } catch (InterruptedException e) {
            System.out.println("Generator was interrupted");
        }
    }
}