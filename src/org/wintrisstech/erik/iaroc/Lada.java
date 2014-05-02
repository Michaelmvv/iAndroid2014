package org.wintrisstech.erik.iaroc;

/**************************************************************************
 * Happy version...ultrasonics working...Version 140427A...mods by Vic
 **************************************************************************/
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.sensors.UltraSonicSensors;
import android.os.SystemClock;

/**
 * A Lada is an implementation of the IRobotCreateInterface, inspired by Vic's
 * awesome API. It is entirely event driven.
 * 
 * @author Erik
 */
public class Lada extends IRobotCreateAdapter {
	private final Dashboard dashboard;
	public UltraSonicSensors sonar;

	/**
	 * Constructs a Lada, an amazing machine!
	 * 
	 * @param ioio
	 *            the IOIO instance that the Lada can use to communicate with
	 *            other peripherals such as sensors
	 * @param create
	 *            an implementation of an iRobot
	 * @param dashboard
	 *            the Dashboard instance that is connected to the Lada
	 * @throws ConnectionLostException
	 */
	public Lada(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard)
			throws ConnectionLostException {
		super(create);
		sonar = new UltraSonicSensors(ioio);
		this.dashboard = dashboard;
		// song(0, new int[]{58, 10});
	}

	public void initialize() throws ConnectionLostException {
		dashboard.log("iAndroid2014 happy version 140427A");
	}

	/**
	 * This method is called repeatedly
	 * 
	 * @throws ConnectionLostException
	 */
	public void loop() throws ConnectionLostException {
		try {
			sonar.read();
		} catch (InterruptedException ex) {
		}
		dashboard.log("L: " + sonar.getLeftDistance() + " F: "
				+ sonar.getFrontDistance() + " R: " + sonar.getRightDistance());
		SystemClock.sleep(100);
		if (sonar.getLeftDistance() < 20) {
			driveDirect(0, 100);
			SystemClock.sleep(1000);
		}
		if (sonar.getRightDistance() < 20) {
			driveDirect(100, 0);
			SystemClock.sleep(1000);
		}
		if (sonar.getFrontDistance() < 20) {
			driveDirect(-500, -500);
			SystemClock.sleep(1000);
		}
		
		driveDirect(0, 0);
	}
}
