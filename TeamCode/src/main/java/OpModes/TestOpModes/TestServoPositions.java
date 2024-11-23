package OpModes.TestOpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import OpModes.Robot;
@TeleOp(name = "TestServoPosition")
public class TestServoPositions extends Robot {

    String[] servoNames = new String[7];
    Servo[] servos = new Servo[7];
    int currentServoPos;
    Servo currentServo;
    public void init(){
        super.init();
        currentServoPos = 0;
        servoNames[0] = "Intake Grabbing";
        servos[0] = intakeGrabberServo;
        servoNames[1] = "Intake Rotation";
        servos[1] = intakeRotationServo;
        servoNames[2] = "Intake Deployment";
        servos[2] = intakeDeploymentServo;
        servoNames[3] = "Delivery Grabbing";
        servos[3] = deliveryGrabberServo;
        servoNames[4] = "Delivery Bucket";
        servos[4] = deliveryBucketDeploymentServo;
        servoNames[5] = "Delivery Gate";
        servos[5] = deliveryBucketGateServo;
        servoNames[6] = "Ratchet";
        servos[6] = hangingRatchetServo;


    }

    public void loop() {
        super.loop();
        if(gameController1.left_bumperPressed) {
            if(currentServoPos == 0)
                currentServoPos = servos.length-1;
            else
                currentServoPos -= 1;
        }
        if(gameController1.right_bumperPressed) {
            if(currentServoPos == servos.length-1)
                currentServoPos = 0;
            else
                currentServoPos += 1;
        }

        currentServo = servos[currentServoPos];
        String currentServoName = servoNames[currentServoPos];
        telemetry.addLine("Bumpers To Change Servo");
        telemetry.addLine("Dpad Up/Down to adjust postion 0.01");
        telemetry.addLine("Dpad Right/Left to adjust postion 0.1");
        double positionValue = currentServo.getPosition();
        if(gameController1.dpad_upPressed)
            positionValue += 0.01;
        if(gameController1.dpad_downPressed)
            positionValue -= 0.01;
        if(gameController1.dpad_rightPressed)
            positionValue += 0.1;
        if(gameController1.dpad_leftPressed)
            positionValue -= 0.1;
        currentServo.setPosition(positionValue);
        telemetry.addData("Current Servo", currentServoName);
        telemetry.addData("Current Servo Position", positionValue);

    }
}
