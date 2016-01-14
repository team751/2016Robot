package org.team751;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import com.kauailabs.nav6.frc.IMUAdvanced;


public class StateSenderUDP {

	private final int port;
	private final InetAddress ipAddress;

	public StateSenderUDP(InetAddress ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	/**
	 * Instantiates a new UDP sender. 
	 *
	 * @param ipAddressString the ip address string. Must be prechecked for validity; this function will only check the validity of the address format.
	 * @param port the port
	 * @throws UnknownHostException the unknown host exception
	 */
	public StateSenderUDP(String ipAddressString, int port) throws UnknownHostException {
		this.ipAddress = InetAddress.getByName(ipAddressString);
		this.port = port;
	}
	
	public void sendState(RobotState state, int secondsRemaining) throws IOException{
		DatagramSocket serverSocket = new DatagramSocket(port);
		InetAddress IPAddress = ipAddress;
        /*
		byte[] sendData = new byte[5]; // 1 for robot state, 4 for time remaining
        // convert int to bytes
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(secondsRemaining);
        byte[] intData = b.array();
        System.arraycopy(intData, 0, sendData, 0, 4); // copy int into sending array
        sendData[4] = robotStateToConstant(state);
        */
        IMUAdvanced imu = Robot.getImu();
        StringBuilder messageString = new StringBuilder("{");
        // TODO: leftTick and rightTick
        messageString.append(roundFour(imu.getRoll()) + ","); // x angular
        messageString.append(roundFour(imu.getPitch()) + ","); // y angular
        messageString.append(roundFour(imu.getYaw()) + ","); // z angular
        messageString.append(roundFour(imu.getWorldLinearAccelX()) + ",");
        messageString.append(roundFour(imu.getWorldLinearAccelY()) + ",");
        messageString.append(roundFour(imu.getWorldLinearAccelZ()) + "}");
        byte[] sendData = messageString.toString().getBytes(StandardCharsets.UTF_8);
        
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
        serverSocket.close();
	}
	
	private static byte robotStateToConstant(RobotState state){
		switch (state) {
		case AUTO:
			return 0;
		case DISABLED:
			return 1;
		case TELEOP:
			return 2;
		default:
			throw new RuntimeException("Invalid state");
		}
	}
	
	private static float roundFour(float number){
		number *= 1000;
		Math.round(number);
		number /= 1000;
		return number;
	}
}
