package Subsystems;

import OpModes.Robot;

public class DeliveryManager {
    private Robot robot;
    public DeliveryManager(Robot robot) {
        //assign the global robot to the local property to access in this class
        this.robot = robot;
    }

    public void update() {

    }
}
