package edu.wpi.first.wpilibj.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.robot.RobotMap;

/**
 * For lack of a better explanation, this class is where all the driver control
 * stuff is located. Joysticks and buttons are declared in this class. All the
 * methods are run in Spike, and can be commented out in case a problem arrises.
 */
public class OI {

    //GamePad Axes: (For future reference)
    private static final int gamepadLeftX = 1,
            gamepadLeftY = 2,
            gamepadRightX = 3,
            gamepadRightY = 4,
            gamepadDPadX = 5,
            gamepadDPadY = 6;
    private static final double feederAngle = 14,
            optimalAngle = 3.14159;
    //Toggling shooter stuff:
    private static boolean currentButton = true,
            previousButton = false,
            toggleFeed = false,
            isAtOptimalAngle = true;
    public static boolean beginClimb = false;
    //Joysticks and Gamepad
    private static final Joystick leftJoystick = new Joystick(RobotMap.leftJoystickPort),
            rightJoystick = new Joystick(RobotMap.rightJoystickPort),
            gamepad = new Joystick(RobotMap.gamepadPort);
    //Buttons (I wish there was a neater way of declaring all this crap)
    private static final JoystickButton autoAngleButton = new JoystickButton(gamepad, RobotMap.autoAngleButton),
            raiseAngleButton = new JoystickButton(gamepad, RobotMap.raiseAngleButton),
            lowerAngleButton = new JoystickButton(gamepad, RobotMap.lowerAngleButton),
            toggleFeedButton = new JoystickButton(gamepad, RobotMap.toggleFeedButton),
            autoAimButton = new JoystickButton(rightJoystick, RobotMap.autoAimButton),
            climbButton1 = new JoystickButton(gamepad, RobotMap.climbButton1),
            climbButton2 = new JoystickButton(gamepad, RobotMap.climbButton2),
            safetyButton = new JoystickButton(rightJoystick, RobotMap.safetyButton),
            fireButton = new JoystickButton(rightJoystick, RobotMap.fireButton);
    private static final DriverStationLCD LCD = DriverStationLCD.getInstance();

    /**
     * Allows the operator to drive the robot. Removes control if the robot is
     * autoAiming or the front limit is pressed (The robot will back up until it
     * isn't).
     */
    public static void driveRobot() {
        if (autoAimButton.get() == false) {
            DriveTrain.tankDrive(leftJoystick.getY(), rightJoystick.getY());
        }
        //Else you're auto aiming...
    }

    /**
     * Very messy method. Shouldn't be that hard to figure out though. Read
     * through it carefully. :P
     */
    public static void controlAutoAim() {
        if (toggleFeed == false) {
            if (autoAimButton.get() == true) {
                Angle.setAngle(Vision.calculateAngle());
                AutoCenter.lineUpTarget();
            } else if (autoAngleButton.get() == true) {
                Angle.setAngle(Vision.calculateAngle());
            } //Angle Control code below is for testing, calibrating, and manual shooting if problems arise.    
            else if (raiseAngleButton.get() == true) {
                Angle.angleUp();
            } else if (lowerAngleButton.get() == true) {
                Angle.angleDown();
            } else {
                Angle.angleStop();
                LCD.println(DriverStationLCD.Line.kUser1, 1, ".............................");
            }
        } else { //If toggleFeed is now true and climbButton is not pressed, go to feeder angle
            Angle.setAngle(feederAngle); //Feeder Angle (Approximately)
        }
    }

    /**
     * Readies the robot to climb which disables most of the robot's functions.
     * Once you push this button, there's no going back. Because of this, two
     * buttons are created so no "accidental" presses happen.
     */
    public static void controlClimb() {
        if (climbButton1.get() == true && climbButton2.get() == true) {
            beginClimb = true;
        }
        if (beginClimb == true) {
            Shooter.stopShooter();
            Angle.autoClimb();
            LCD.println(DriverStationLCD.Line.kUser5, 1, "Climber Engaged");
        }
    }

    /**
     * I created current button since there is a possibility that you might let
     * go right in the middle of the if statement. Unlikely but it might break
     * the code. This class should be self explanatory.
     */
    public static void controlFeed() {
        currentButton = toggleFeedButton.get();
        if ((currentButton == true) && (previousButton == false)) {
            toggleFeed = !toggleFeed;
            isAtOptimalAngle = false;
        }
        previousButton = currentButton;
        if (toggleFeed == false) {
            if (isAtOptimalAngle == false) {
                Angle.setAngle(optimalAngle);
                if (Angle.anglePWM.getSpeed() == 0) {
                    isAtOptimalAngle = true;
                }
            }
        }
    }

    /**
     * Fires if the fireButton is pressed or autoAiming is done.
     */
    public static void controlTrigger() {
        Shooter.fireShooter(OI.fireButton.get());
    }

    /**
     * Used to pull in the winch to initialize the robot's winch prior to each
     * match.
     */
    public static void controlWinch() {
        if (safetyButton.get() == true) {
            Angle.winchPWM.set(gamepad.getRawAxis(gamepadDPadY));//Up pulls in.
        }
    }
}
