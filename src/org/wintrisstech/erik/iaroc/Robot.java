package org.wintrisstech.erik.iaroc;

import org.wintrisstech.sensors.UltraSonicSensors;

import android.os.SystemClock;
import ioio.lib.api.exception.ConnectionLostException;

/**************************************************************************
 * A class to abstract an higher level API to control the robot
 **************************************************************************/
public class Robot {
	private Boolean isSpeakEnabled = true;
	private Lada lada;
	private final Dashboard dashboard;
	private UltraSonicSensors sonar;
	private int TURN_SPEED = 150;

	// for gold rush
	public int IrCheckFrequency;
	public int GRloopsElapsed;
	public int IRloop = 0;

	// for maintain heading
	private int initialHeading;
	public int leftSpeed = 0;
	public int rightSpeed = 0;

	public boolean hit;
	public boolean hitLeft;
	public boolean hitRight;

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
		if (isSpeakEnabled == true) {
			dashboard.speak(message);
		}
	}

	public void goForward(int centimeters) throws ConnectionLostException {
		int totalDistance = 0;
		lada.readSensors(Lada.SENSORS_GROUP_ID6);
		lada.driveDirect(200, 200);
		while ((totalDistance < centimeters * 10)) {
			if (this.lada.isBumpLeft()) {
				this.dashboard.log("Left Bump Hit");
				hit = true;
				hitLeft = true;
				break;
			}
			if (this.lada.isBumpRight()) {
				this.dashboard.log("Right Bump Hit");
				hit = true;
				hitRight = true;
				break;
			}
			lada.readSensors(Lada.SENSORS_GROUP_ID6);
			int dd = lada.getDistance();
			totalDistance += dd;
			// log("" + totalDistance / 10 + " cm");
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
		while (delta > 3) {
			if (delta > 0 && delta <= 180 || delta < 0 && delta >= 180) {
				rotateLeft();
			} else {
				rotateRight();
			}
			delta = readCompass() - desiredHeading;
		}
		stop();
	}

	public int readCompass() {
		return (int) (dashboard.getAzimuth() + 360) % 360;
	}

	public void rotateRight() throws ConnectionLostException {
		this.driveDirect(TURN_SPEED, -TURN_SPEED);
	}

	public void rotateLeft() throws ConnectionLostException {
		this.driveDirect(-TURN_SPEED, TURN_SPEED);
	}

	// fixes lada.driveDirect() by switching the order of the arguments
	public void driveDirect(int left, int right) throws ConnectionLostException {
		lada.driveDirect(right, left);
	}

	public void followStraightWall(int speed, int turnSpeed, int bufferDis,
			int distance, String side, int sleepTime)
			throws ConnectionLostException, InterruptedException {
		/*
		 * Speed - turn - sleep -yes/no, 200 - 10 - 100 - yes, but slow, 450 -
		 * 20- 100 - no , 450 - 30 - 100 - no , 300 - 15 - 100 - no , 300 - 15 -
		 * 50 - no, 300 - 10 - 50 - OK, needs work, 300 - 10 - 50 - Buffer: 3
		 * -worked Well, 350 - 10 - 50 - Buffer:3 - Worked, 400 - 10 - 50 -
		 * Buffer:3 - Not working, 400 - 10 - 30 - Buffer:3 - Not working, 400 -
		 * 10 - 30 - Buffer:1 - Not working, 400 - 7 - 30 - Buffer:1 - not
		 * working, 400 - 6 - 30 - Buffer:1 - Not working,
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

	public void forwardOneSpace() throws ConnectionLostException,
			InterruptedException {
		this.dashboard.log("Im here in the FOS method!");
		if (this.getFrontDistance() < 50) {
			this.dashboard.log("Going forward sensor dis : "
					+ (this.getFrontDistance() - 10));
			goForward(this.getFrontDistance() - 15);
			if (hit == false) {
				bumpToCorrect();
			}
		} else if (this.getFrontDistance() > 70
				&& this.getFrontDistance() < 100) {
			goForward(this.getFrontDistance() - 15);
			if (hit == false) {
				bumpToCorrect();
			}
		} else {
			this.dashboard.log("Going forward static dis");
			this.goForward(61);
		}
		this.dashboard.log("Im done with the FOS method, returning!");
	}

	public void bumpToCorrect() throws ConnectionLostException {
		this.driveDirect(60, 60);
		SystemClock.sleep(3000);
		stop();
		this.driveDirect(-100, -100);
		SystemClock.sleep(2000);
		stop();
	}

	public void doWallHugging(int wallDis, String side)
			throws ConnectionLostException, InterruptedException {

		lada.readSensors(Lada.SENSORS_GROUP_ID6);
		this.dashboard.log("Sensors Read");
		if (hitLeft) {
			// speak("left Bump Correction");
			this.dashboard.log("Starting left Corection");
			stop();
			this.dashboard.log("BEEP backing up...");
			this.driveDirect(-200, -200);
			SystemClock.sleep(500);
			stop();
			this.dashboard.log("Turning...");
			this.rotateRight();
			SystemClock.sleep(400);
			stop();
			this.dashboard.log("K, iv reset the bumping vars");
			hitLeft = false;
			hit = false;
			driveDirect(200, 200);
			SystemClock.sleep(1000);
		} else if (hitRight) {
			// speak("Right Bump Correction");
			this.dashboard.log("Starting bump Right Correction");
			stop();
			this.dashboard.log("BEEP backing up...");
			this.driveDirect(-200, -200);
			SystemClock.sleep(500);
			stop();
			this.dashboard.log("Turning...");
			this.rotateLeft();
			SystemClock.sleep(400);
			stop();
			this.dashboard.log("K, iv reset the bumping vars");
			hitRight = false;
			hit = false;
			driveDirect(200, 200);
			SystemClock.sleep(1000);
		}
		if (side.equalsIgnoreCase("Right")) {

			if (this.getRightDistance() > wallDis) {
				// speak("turning right");
				this.dashboard.log("turningRight...");
				turnRight();
			} else if (this.getFrontDistance() > wallDis) {

			} else if (this.getLeftDistance() > wallDis) {
				// speak("turning left");
				this.dashboard.log("turningLeft...");
				turnLeft();
			} else {
				this.dashboard.log("Turning Around...");
				turnAround();
			}

		} else if (side.equalsIgnoreCase("Left")) {
			if (this.getLeftDistance() > wallDis) {
				// speak("turning left");
				this.dashboard.log("turningLeft...");
				turnLeft();
			} else if (this.getFrontDistance() > wallDis) {

			} else if (this.getRightDistance() > wallDis) {
				// speak("turning right");
				this.dashboard.log("turningRight...");
				turnRight();
			} else {
				this.dashboard.log("Turning Around...");
				turnAround();
			}

		} else {
			dashboard.log("Error in side String");
			dashboard.log("Its Value was: " + side);
		}
		// speak("forward");
		if (this.getRightDistance() < 40 && this.getLeftDistance() < 40) {
			// aligns better by rotating
			if ((this.getRightDistance() - this.getLeftDistance()) > 4) {
				// speak("Sonic alignment right");
				rotateRight();
				SystemClock.sleep(120);
				stop();
			} else if ((this.getLeftDistance() - this.getRightDistance()) > 4) {
				// speak("Sonic alignment left");
				rotateLeft();
				SystemClock.sleep(120);
				stop();
			}
		}
		this.dashboard.log("Finished Turn!   MOVING...");
		forwardOneSpace();
		this.dashboard.log("Moved Forward");
	}

	public void turnLeft() throws ConnectionLostException {
		this.driveDirect(-150, 150);
		SystemClock.sleep(1400);
		stop();
	}

	public void turnRight() throws ConnectionLostException {
		driveDirect(150, -150);
		SystemClock.sleep(1400);
		stop();
	}

	public void turnAround() throws ConnectionLostException {
		turnRight();
		turnRight();
	}

	public void michaelsFollowStraightWall(int speed, String side, int sleepTime)
			throws ConnectionLostException, InterruptedException {
		if (side.equalsIgnoreCase("Right")) {
			// int right = this.getRightDistance();

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

	public int getIR() throws ConnectionLostException {
		this.lada.readSensors(Lada.SENSORS_GROUP_ID6);
		int ir = this.lada.getInfraredByte();
		return ir;
	}

	public void doGoldRush() throws ConnectionLostException {
		IRloop++;
		if (IRloop > 2) {
			IRloop = 0;
			if (findIR()) {
				goForward(30);
			}
		}
		goForward(30);
		if (hitRight) {
			this.driveDirect(-100, -100);
			SystemClock.sleep(1500);
			rotateLeft();
			SystemClock.sleep(1000);
			hit = false;
			hitRight = false;
			hitLeft = false;
		} else if (hitLeft) {
			this.driveDirect(-100, -100);
			SystemClock.sleep(1500);
			rotateRight();
			SystemClock.sleep(1000);
			hit = false;
			hitRight = false;
			hitLeft = false;
		} else {

		}

	}

	public boolean foundIR() throws ConnectionLostException {
		return (getIR() != 255);
	}

	public boolean findIR() throws ConnectionLostException {

		for (int j = 1; j < 25; j = j + 1) {
			this.driveDirect(150, -150);
			SystemClock.sleep(200);
			stop();
			if (foundIR()) {
				dashboard.log("found IR!");
				return true;
			}
			// return false;
		}
		return false;
	}

	public void toIR() throws ConnectionLostException {
		this.lada.readSensors(Lada.SENSORS_GROUP_ID6);
		this.driveDirect(200, 200);
		SystemClock.sleep(2000);
		if ((this.lada.getInfraredByte() == 255)) {
			findIR();
		} else {
			toIR();
		}
	}

	//
	public void maintainHeading() throws ConnectionLostException {
		driveDirect(leftSpeed, rightSpeed);
	}

	public int getLeftDistance() throws ConnectionLostException,
			InterruptedException {
		this.dashboard.log("Reading Left");
		this.sonar.read();
		SystemClock.sleep(100);
		return this.sonar.getLeftDistance();
	}

	public int getRightDistance() throws ConnectionLostException,
			InterruptedException {
		this.dashboard.log("Reading Right");
		this.sonar.read();
		SystemClock.sleep(100);
		return this.sonar.getRightDistance();
	}

	public int getFrontDistance() throws ConnectionLostException,
			InterruptedException {
		this.log("In getFrontDistance...");
		this.sonar.read();
		SystemClock.sleep(100);
		return this.sonar.getFrontDistance();
	}

}