package OpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import Utilities.MovementVars;

@TeleOp(name = "Driver Controlled")
public class DriverControlled extends Robot{

    public void init() {
        super.init();
    }

    public void init_loop() {
        super.init_loop();
    }

    public void start() {
        super.start();
    }
    public void loop() {
        super.loop();

        applyMovement(1.0);

    }

    private void applyMovement(double powerMultiple) {
        MovementVars.movementX = gameController1.left_stick_x * (1-gameController1.left_trigger*0.625)*powerMultiple*1.5;
        MovementVars.movementY = gameController1.left_stick_y * (1-gameController1.left_trigger*0.625)*powerMultiple;
        MovementVars.movementRotate = gameController1.right_stick_x * (1-gameController1.left_trigger*0.625)*powerMultiple;
    }
}
