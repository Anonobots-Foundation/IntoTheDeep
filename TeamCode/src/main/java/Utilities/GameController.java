package Utilities;

import com.qualcomm.robotcore.hardware.Gamepad;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

public class GameController {

    private boolean aLast = false;
    private boolean bLast = false;
    private boolean backLast = false;
    private boolean dpad_downLast = false;
    private boolean dpad_leftLast = false;
    private boolean dpad_rightLast = false;
    private boolean dpad_upLast = false;
    private boolean guideLast = false;
    private boolean left_bumperLast = false;
    private boolean left_stick_buttonLast = false;
    private boolean right_bumperLast = false;
    private boolean right_stick_buttonLast = false;
    private boolean startLast = false;
    private boolean xLast = false;
    private boolean yLast = false;

    public boolean a;
    public boolean b;
    public boolean back;
    public boolean dpad_down;
    public boolean dpad_left;
    public boolean dpad_right;
    public boolean dpad_up;
    public boolean guide;
    public boolean left_bumper;
    public boolean left_stick_button;
    public float left_stick_x;
    public float left_stick_y;
    public float left_trigger;
    public boolean right_bumper;
    public boolean right_stick_button;
    public float right_stick_x;
    public float right_stick_y;
    public float right_trigger;
    public boolean start;
    public boolean x;
    public boolean y;

    public boolean aPressed;
    public boolean bPressed;
    public boolean backPressed;
    public boolean dpad_downPressed;
    public boolean dpad_leftPressed;
    public boolean dpad_rightPressed;
    public boolean dpad_upPressed;
    public boolean guidePressed;
    public boolean left_bumperPressed;
    public boolean left_stick_buttonPressed;
    public boolean right_bumperPressed;
    public boolean right_stick_buttonPressed;
    public boolean startPressed;
    public boolean xPressed;
    public boolean yPressed;
    public void update(    boolean a,
                           boolean b,
                           boolean back,
                           boolean dpad_down,
                           boolean dpad_left,
                           boolean dpad_right,
                           boolean dpad_up,
                           boolean guide,
                           boolean left_bumper,
                           boolean left_stick_button,
                           float left_stick_x,
                           float left_stick_y,
                           float left_trigger,
                           boolean right_bumper,
                           boolean right_stick_button,
                           float right_stick_x,
                           float right_stick_y,
                           float right_trigger,
                           boolean start,
                           boolean x,
                           boolean y) {
       this.a = a;
       this.b = b;
       this.back = back;
       this.dpad_down = dpad_down;
       this.dpad_left = dpad_left;
       this.dpad_right = dpad_right;
       this.dpad_up = dpad_up;
       this.guide = guide;
       this.left_bumper = left_bumper;
       this.left_stick_button = left_stick_button;
       this.left_stick_x = left_stick_x;
       this.left_stick_y = left_stick_y *-1;
       this.left_trigger = left_trigger;
       this.right_bumper = right_bumper;
       this.right_stick_button = right_stick_button;
       this.right_stick_x = right_stick_x;
       this.right_stick_y = right_stick_y *-1;
       this.right_trigger = right_trigger;
       this.start = start;
       this.x = x;
       this.y = y;
        aPressed = a && !aLast;
        bPressed = b && !bLast;
        backPressed = back && !backLast;
        dpad_downPressed = dpad_down && !dpad_downLast;
        dpad_rightPressed = dpad_right && !dpad_rightLast;
        dpad_leftPressed = dpad_left && !dpad_leftLast;
        dpad_upPressed = dpad_up && !dpad_upLast;
        guidePressed = guide && !guideLast;
        left_bumperPressed = left_bumper && !left_bumperLast;
        left_stick_buttonPressed = left_stick_button && !left_stick_buttonLast;
        right_bumperPressed = right_bumper && !right_bumperLast;
        right_stick_buttonPressed = right_stick_button && !right_stick_buttonLast;
        startPressed = start && !startLast;
        xPressed = x && !xLast;
        yPressed = y && !yLast;

        aLast = a;
        bLast = b;
        backLast = back;
        dpad_downLast = dpad_down;
        dpad_rightLast = dpad_right;
        dpad_leftLast = dpad_left;
        dpad_upLast = dpad_up;
        guideLast = guide;
        left_bumperLast = left_bumper;
        left_stick_buttonLast = left_stick_button;
        right_bumperLast = right_bumper;
        right_stick_buttonLast = right_stick_button;
        startLast = start;
        xLast = x;
        yLast = y;
    }
}
