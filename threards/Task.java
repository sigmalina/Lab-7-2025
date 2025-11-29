package threads;

import functions.*;

public class Task {
    private Function function;
    private double leftX;
    private double rightX;
    private double step;
    private int tasks;

    public Task(int tasks) {
        this.tasks = tasks;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public double getLeftX() {
        return leftX;
    }

    public void setLeftX(double leftX) {
        this.leftX = leftX;
    }

    public double getRightX() {
        return rightX;
    }

    public void setRightX(double rightX) {
        this.rightX = rightX;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public int getTasks() {
        return tasks;
    }

    public void setTasks(int tasks) {
        this.tasks = tasks;
    }
    private volatile boolean processed = false;

    public synchronized boolean isProcessed() {
        return processed;
    }

    public synchronized void setProcessed(boolean processed) {
        this.processed = processed;
    }
}