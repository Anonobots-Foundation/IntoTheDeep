package OpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import Commands.Command;
import Subsystems.DeliveryManager;
import Subsystems.IntakeManager;
import Utilities.MovementVars;

@TeleOp(name = "Driver Controlled")
public class DriverControlled extends Robot{
    Command runningCommand;
    public void init() {
        super.init();

    }

    public void init_loop() {
        super.init_loop();
        telemetry.addLine("LB+RB to zero motors. This is only for testing");
        if(gameController1.left_bumper && gameController1.right_bumper) {
            intakeExtensionMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeExtensionMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            deliveryLiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            deliveryLiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }


    }

    public void start() {
        super.start();
        intakeManager.moveToHome();
        deliveryManager.moveToHome();
    }
    public void loop() {
        super.loop();

        if(runningCommand != null) {
            if(runningCommand.isDone() || (gameController1.left_bumper && gameController1.right_bumper)) {
                runningCommand = null;
            }
            else {
                if(runningCommand.allowDriverControl)
                    applyMovement(runningCommand.driverSpeedMultiple);
                runningCommand.update();
                return; // break out of loop to prevent any driver input code from running.
            }
        }
        else
            applyMovement(1.0);

        //retract intake - prevent intake from trying to move in and out
        if(gameController1.left_trigger > 0)
            intakeManager.moveIntakeExtension(-gameController1.left_trigger);
        else if(gameController1.right_trigger >0)
            intakeManager.moveIntakeExtension(gameController1.right_trigger);
        //intake deployment
        if(gameController1.xPressed) {
            if(gameController1.right_bumper)
                intakeManager.retractIntake();
            else if(gameController1.left_bumper)
                intakeManager.toggleGrabber();
            else
                intakeManager.deployIntake();
        }


        //DELIVERY BASKET
        if(gameController1.yPressed) {
            if(gameController1.dpad_left)
                deliveryManager.moveDeliveryToPosition(DeliveryManager.DeliveryPositions.LOW_BASKET);
            else if(gameController1.dpad_right)
                deliveryManager.moveDeliveryToPosition(DeliveryManager.DeliveryPositions.HIGH_BASKET);
            else if(gameController1.right_bumper)
                deliveryManager.toggleBucketGate();
            else if(gameController1.dpad_down)
                deliveryManager.adjustDeliveryHeight(-50);
            else if(gameController1.dpad_up)
                deliveryManager.adjustDeliveryHeight(50);
            else
                deliveryManager.toggleBasketAndHome();
        }
        if(gameController1.startPressed)
            deliveryManager.moveDeliveryToPosition(DeliveryManager.DeliveryPositions.DROP_SAMPLE);
        if(gameController1.aPressed)
            deliveryManager.moveDeliveryToPosition(DeliveryManager.DeliveryPositions.LEVEL_1_CLIMB);
        //DELIVERY CHAMBER
        /*
        if(gameController1.bPressed) {
            if(gameController1.dpad_left)
                deliveryManager.moveDeliveryToPosition(DeliveryManager.DeliveryPositions.LOW_CHAMBER);
            else if(gameController1.dpad_right)
                deliveryManager.moveDeliveryToPosition(DeliveryManager.DeliveryPositions.HIGH_CHAMBER);
            else if(gameController1.right_bumper) {}
                //TODO:Call Scoring Routine
            else if(gameController1.left_bumper)
                deliveryManager.toggleGrabber();
            else
                deliveryManager.toggleChamberAndHome();
        }

         */
        if(gameController1.xPressed) {
            double adjustment = 0;
            if(gameController1.dpad_up) {
                adjustment = 0.01;
            }
            else if(gameController1.dpad_down) {
                adjustment = -0.01;
                intakeManager.adjustDeploymentHeight(adjustment);
            }
        }
        telemetry.addData("Extension Pos", intakeExtensionMotor.getCurrentPosition());
    }

    private void applyMovement(double powerMultiple) {
        MovementVars.movementX = gameController1.left_stick_x * (1-gameController1.left_trigger*0.625)*powerMultiple*1.5;
        MovementVars.movementY = gameController1.left_stick_y * (1-gameController1.left_trigger*0.625)*powerMultiple;
        MovementVars.movementRotate = gameController1.right_stick_x * (1-gameController1.left_trigger*0.625)*powerMultiple;
    }
}
