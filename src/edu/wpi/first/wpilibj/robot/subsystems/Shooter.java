package edu.wpi.first.wpilibj.robot.subsystems;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.robot.RobotMap;

/**
 * This class controls the Frisbee pushing arm and also keeps the shooter motors
 * running at a set RPM.  
 */
public class Shooter {

    private static final DriverStationLCD LCD = DriverStationLCD.getInstance();
    private static final double encoderCountPerRev = 256,
            ratio = 0.01, //This is to account for the additional friction on the second PWM
            spinUpTolerance = 350, //Spin up speed from 0 to targetRPM-spinUpTolerance so we can fire quicker after loading. 
            tolerance = 45, //+/- this amount of RPM
            shooterPWMAccelerationSpeed = 0.9,
            rate = 0.0015, //Tune it.
            targetRPM = 2550;
    private static double //RPM
            PWMCurrentSpeed = 0.85,
            rawValue, //the current encoder value used for calculations.  
            lastRawValue,
            deltaCounts, //change in counts
            lastTime,
            currentTime,
            deltaTime; //change in time
    public static double currentRPM;
    public static final Talon shooterPWM1 = new Talon(RobotMap.shooterPWM1),
            shooterPWM2 = new Talon(RobotMap.shooterPWM2);
    private static final Relay triggerRelay = new Relay(RobotMap.triggerRelay);
    private static final DigitalInput triggerLimit = new DigitalInput(RobotMap.triggerLimit);
    public static final Encoder RPMEncoder = new Encoder(RobotMap.shooterAChannelDin, RobotMap.shooterBChannelDin, false, CounterBase.EncodingType.k1X);

    public static void shooterInit() { //Should only be run in init sequences
        if (triggerLimit.get() == false) {
            while (triggerLimit.get() == false) {
                triggerRelay.set(Relay.Value.kForward);
            }
        }
        RPMEncoder.start();
        RPMEncoder.reset();
        shooterPWM1.set(PWMCurrentSpeed + ratio);
        shooterPWM2.set(-PWMCurrentSpeed);
    }

    /**
     * I considered using a PID for this, but it would take too much time and
     * effort to configure. Because of this, a simple stepping function is used,
     * which seems to do its job. It might not be the best way to control the
     * RPM, but it is reliable and quick enough.
     */
    public static void runShooter() { //Calculate RPM Must be running somewhere in the code.  
        //Following should be self-explanatory.  Join the build team if you do not understand. 
        LCD.println(DriverStationLCD.Line.kUser3, 1, "Shooter Running");
        if (currentRPM < targetRPM - spinUpTolerance) {
            shooterPWM1.set(shooterPWMAccelerationSpeed + ratio); //"ratio" is the offset to account for friction in shooter motor
            shooterPWM2.set(-shooterPWMAccelerationSpeed);
        } else if (currentRPM > targetRPM + tolerance) {
            PWMCurrentSpeed -= rate;
        } else if (currentRPM < targetRPM - tolerance) {
            PWMCurrentSpeed += rate;
        }
        shooterPWM1.set(PWMCurrentSpeed + ratio); //Motors run in opposite directions.  
        shooterPWM2.set(-PWMCurrentSpeed);
    }

    public static void stopShooter() {
        shooterPWM1.set(0.0);
        shooterPWM2.set(0.0);
        LCD.println(DriverStationLCD.Line.kUser3, 1, "Shooter Stopped");
    }

    public static void fireShooter(boolean button) {
        if (button == true) {
            LCD.println(DriverStationLCD.Line.kUser6, 1, "FIRING!!! PEW! PEW!");
        }
        if (((button == true && shooterStatus() == true) || triggerLimit.get() == false) && Angle.angleEncoder.getDistance() < 11.7) {
            triggerRelay.set(Relay.Value.kForward);
            LCD.println(DriverStationLCD.Line.kUser6, 1, "---Reloading---        ");
        } else if (triggerLimit.get() == true) {
            triggerRelay.set(Relay.Value.kOff);
            LCD.println(DriverStationLCD.Line.kUser6, 1, "........................");
        }
    }

    /**
     * This is where the magic happens. It really should be (deltaCounts /
     * encoderCountPerRev) / (60 * deltaTime) <- I don't care. My code is best
     * code.
     */
    public static double calculateRPM() {
        rawValue = Math.abs(RPMEncoder.getRaw());
        currentTime = Timer.getFPGATimestamp(); //Static
        deltaCounts = rawValue - lastRawValue;
        deltaTime = currentTime - lastTime;
        currentRPM = deltaCounts * (60 / encoderCountPerRev) / deltaTime;
        lastRawValue = rawValue;
        lastTime = currentTime;
        return currentRPM;
    }

    /**
     * Anyone should be able to understand this class.
     */
    public static boolean shooterStatus() {
        if (Math.abs(currentRPM - targetRPM) < tolerance) {
            return true;
        } else {
            return false;
        }
    }
}
