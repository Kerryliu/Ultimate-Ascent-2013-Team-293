package edu.wpi.first.wpilibj.robot;

/**
 * This is where all the ports are declared.
 */
public class RobotMap {

    public static final int //Ports are declared here.  
            //Joysticks:
            leftJoystickPort = 1,
            rightJoystickPort = 2,
            gamepadPort = 3,
            //Joystick Buttons:
            autoAimButton = 3, //Joystick (Right)
            fireButton = 1, //Joystick (Right)
            safetyButton = 10, //Joystick (right)
            autoAngleButton = 6, //Gamepad
            toggleFeedButton = 8, //Gamepad
            raiseAngleButton = 5, //Gamepad
            lowerAngleButton = 7, //Gamepad
            climbButton1 = 3, //Gamepad
            climbButton2 = 1, //Gamepad
            //PWM's:             
            leftDrivePWM1 = 1,
            leftDrivePWM2 = 2,
            rightDrivePWM1 = 7,
            rightDrivePWM2 = 8,
            shooterPWM1 = 3,
            shooterPWM2 = 4,
            anglePWM = 5,
            winchPWM = 6,
            //Relays:
            triggerRelay = 1,
            //Encoders:
            shooterAChannelDin = 1,
            shooterBChannelDin = 2,
            angleAChannelDin = 3,
            angleBChannelDin = 4,
            //Limit Switches
            shooterAngleLimitLower = 8,
            shooterAngleLimitUpper = 9,
            triggerLimit = 7,
            climberClawLimit1 = 10,
            climberClawLimit2 = 11,
            autonomousSwitch = 13,
            frontLimit = 14,
            extraNonExistentButSoonToBeUsedSwitch = 15;
}
