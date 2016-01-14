
package org.team751;

import java.io.IOException;
import java.net.UnknownHostException;

import com.kauailabs.nav6.frc.IMUAdvanced;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	static CANTalon leftFrontWheel = new CANTalon(1);
	static CANTalon rightFrontWheel = new CANTalon(2);
	static StateSenderUDP sender;

	static{
		try {
			sender = new StateSenderUDP("10.7.51.75", 6000);
		} catch (UnknownHostException e) {
			// We're screwed if this happens, but sam told me to just print the stack trace and move on
			e.printStackTrace();
		}
	}
	

	private static SerialPort serial_port;
	private static IMUAdvanced imu;
	MotorControlUDP motorControlUDP = new MotorControlUDP(9999);
	Thread motorControlThread;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	motorControlThread = new Thread(motorControlUDP);
    	motorControlThread.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
    private static void setupIMU() {
		try {
			serial_port = new SerialPort(57600, SerialPort.Port.kUSB);

			byte update_rate_hz = 50;
			setImu(new IMUAdvanced(serial_port, update_rate_hz));
		} catch (Exception ex) {

		}
		if (getImu() != null) {
			LiveWindow.addSensor("IMU", "Gyro", getImu());
		}
	}

	public static IMUAdvanced getImu() {
		if (imu == null) Robot.setupIMU();
		return imu;
	}

	public static void setImu(IMUAdvanced imu) {
		Robot.imu = imu;
	}
	
	@Override
	public void disabledInit() {
		try {
			sender.sendState(RobotState.DISABLED, (int)DriverStation.getInstance().getMatchTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.disabledInit();
	}

	@Override
	public void autonomousInit() {
		try {
			sender.sendState(RobotState.AUTO, (int)DriverStation.getInstance().getMatchTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.autonomousInit();
	}

	@Override
	public void teleopInit() {
		try {
			sender.sendState(RobotState.TELEOP, (int)DriverStation.getInstance().getMatchTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.teleopInit();
	}
    
}
