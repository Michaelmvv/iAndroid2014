package org.wintrisstech.erik.iaroc;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.sensors.UltraSonicSensors;

/**
 * A Lada is an implementation of the IRobotCreateInterface, inspired by Vic's
 * awesome API. It is entirely event driven. Version 140404A...mods by Vic
 * @author Erik
 */
public class Lada extends IRobotCreateAdapter {

	private final Dashboard dashboard;
	public UltraSonicSensors sonar;

	/**
	 * Constructs a Lada, an amazing machine!
	 * @param ioio the IOIO instance that the Lada can use to communicate with
	 * other peripherals such as sensors
	 * @param create an implementation of an iRobot
	 * @param dashboard the Dashboard instance that is connected to the Lada
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
		dashboard.log("===========Start===========");
		readSensors(SENSORS_GROUP_ID6);
		dashboard.log("iAndroid2014 version 140404A");
		dashboard.log("Battery Charge = " + getBatteryCharge()
				+ ", 3,000 = Full charge");
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
		int lDistance = sonar.getLeftDistance();
		if (lDistance != 0) {
			dashboard.log("L: " + sonar.getLeftDistance());
		} else {
			dashboard.log("......");
		}
	}
}
