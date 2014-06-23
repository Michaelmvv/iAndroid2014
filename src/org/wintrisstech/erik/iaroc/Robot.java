package org.wintrisstech.erik.iaroc;


import org.wintrisstech.sensors.UltraSonicSensors;

import android.os.SystemClock;
import ioio.lib.api.exception.ConnectionLostException;

/**************************************************************************
 * A class to abstract an higher level API to control the robot
 **************************************************************************/
public class Robot {

	private Lada lada;
	private final Dashboard dashboard;
	private UltraSonicSensors sonar;
	private int TURN_SPEED = 25;

	// for maintain heading
	private int initialHeading;
	public int leftSpeed = 0;
	public int rightSpeed = 0;

	public Robot(Dashboard dashboard, Lada lada, UltraSonicSensors sonar) {
		this.dashboard = dashboard;
		this.lada = lada;
		this.sonar = sonar;
		this.initialHeading = readCompass();
	}

	public int getInitialHeading() {
		return initialHeading;
	}

	public void log(String message) {
		dashboard.log(message);
	}

	public void speak(String message) {
		dashboard.speak(message);
	}

	public void goForward(int centimeters) throws ConnectionLostException {
		int totalDistance = 0;
		lada.readSensors(Lada.SENSORS_GROUP_ID6);
		lada.driveDirect(250, 250);
		while (totalDistance < centimeters * 10) {
			lada.readSensors(Lada.SENSORS_GROUP_ID6);
			int dd = lada.getDistance();
			totalDistance += dd;
			log("" + totalDistance / 10 + " cm");
		}
		stop();
	}

	public void stop() throws ConnectionLostException {
		lada.driveDirect(0, 0);
	}

	public void turnToHeading(int desiredHeading)
			throws ConnectionLostException {
		int currentHeading = readCompass();
		int delta = currentHeading - desiredHeading;
		log("Current Heading:" + currentHeading);
		if (delta <= 3) {
			stop();
			TURN_SPEED = 1;
		} else {
			if (delta > 0 && delta <= 180 || delta < 0 && delta >= 180) {
				rotateLeft();
			} else {
				rotateRight();
			}
		}
	}

	public int readCompass() {
		return (int) (dashboard.getAzimuth() + 360) % 360;
	}

	public void rotateRight() throws ConnectionLostException {
		lada.driveDirect(-TURN_SPEED, TURN_SPEED);
	}

	public void rotateLeft() throws ConnectionLostException {
		lada.driveDirect(TURN_SPEED, -TURN_SPEED);
	}

	// fixes lada.driveDirect() by switching the order of the arguments
	public void driveDirect(int left, int right) throws ConnectionLostException {
		lada.driveDirect(right, left);
	}

	public void followStraightWall(int speed, int turnSpeed, int bufferDis, int distance, String side, int sleepTime)
			throws ConnectionLostException, InterruptedException {
	/*
	 * Speed - turn - sleep -yes/no
	 * 200 - 10 - 100 - yes, but slow
	 * 450 - 20 - 100 - no
	 * 450 - 30 - 100 - no
	 * 300 - 15 - 100 - no
	 * 300 - 15 - 50  - no
	 * 300 - 10 - 50  - OK, needs work
	 */
		if (side.equalsIgnoreCase("right")) {
			int right = this.getRightDistance();
			int delta = right - distance;
			dashboard.log("Delta: " + delta);
			if (delta > bufferDis) {
				dashboard.log("Turning Right");
				leftSpeed = speed + turnSpeed;
				rightSpeed = speed - turnSpeed;
			} else if (delta < -bufferDis) {
				dashboard.log("Turning Left");
				leftSpeed = speed - turnSpeed;
				rightSpeed = speed + turnSpeed;
			} else {
				dashboard.log("Go straight");
				leftSpeed = speed;
				rightSpeed = speed;
			}

		} else if (side.equalsIgnoreCase("left")) {
			int left = this.getLeftDistance();
			int delta = left - distance;
			if (delta > bufferDis) {
				leftSpeed = speed + turnSpeed;
				rightSpeed = speed - turnSpeed;
			} else if (delta < bufferDis) {
				leftSpeed = speed - turnSpeed;
				rightSpeed = speed + turnSpeed;
			} else {
				leftSpeed = speed;
				rightSpeed = speed;
			}

		} else {
			leftSpeed = 0;
			rightSpeed = 0;
			dashboard.log("STUPID: LEARN TO SPEL");
		}
		this.driveDirect(leftSpeed, rightSpeed);
		SystemClock.sleep(sleepTime);

	}
	
	public void doLeftWallHugging()
	{
		return;
	}
	
	public void turnLeft() throws ConnectionLostException
	{
		this.driveDirect(-300, 300);
		SystemClock.sleep(700);
		stop();
	}
	
	public void turnRight() throws ConnectionLostException
	{
		driveDirect(300, -300);
		SystemClock.sleep(700);
		stop();
	}
	
	public void turnAround() throws ConnectionLostException {
		turnRight();
		turnRight();
	}
	
	public void michaelsFollowStraightWall(int speed, String side,int sleepTime) throws ConnectionLostException, InterruptedException {
		if (side.equalsIgnoreCase("Right")) {
		//	int right = this.getRightDistance();
			
		}
		
	}

	public void timer() {

	}
	
	// gets called on an interval
	// adjusts the left/right wheel speed
	public void maintainHeadingLogic() {
		int current = readCompass();
		dashboard.log("current heading: " + current);
		int delta = initialHeading - current;
		dashboard.log("    delta: " + delta);

		if (delta >= 2) {
			leftSpeed = leftSpeed + 10;
			rightSpeed = 100;
			dashboard.log("steering right...");
		} else if (delta <= -2) {
			rightSpeed = rightSpeed + 10;
			leftSpeed = 100;
			dashboard.log("steering left...");
		} else {
			rightSpeed = 100;
			leftSpeed = 100;
		}
	}

	//
	public void maintainHeading() throws ConnectionLostException {
		driveDirect(leftSpeed, rightSpeed);
	}

	public int getLeftDistance() throws ConnectionLostException,
			InterruptedException {
		this.sonar.read();
		return this.sonar.getLeftDistance();
	}

	public int getRightDistance() throws ConnectionLostException,
			InterruptedException {
		this.sonar.read();
		return this.sonar.getRightDistance();
	}

	public int getFrontDistance() throws ConnectionLostException,
			InterruptedException {
		this.sonar.read();
		return this.sonar.getFrontDistance();
	}
}