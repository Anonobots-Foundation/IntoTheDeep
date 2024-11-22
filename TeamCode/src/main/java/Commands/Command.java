package Commands;

public abstract class Command {
    public abstract void activate();
    public abstract void update();
    public abstract boolean isDone();
    public boolean newStep;
    public int currentStep;
    public boolean allowDriverControl = false;
    public double driverSpeedMultiple = 1;
    public void nextStep() {
        currentStep += 1;
        newStep = true;
    }
    public void setCurrentStep(int nextStep)
    {
        currentStep = nextStep;
        newStep = true;
    }
}
