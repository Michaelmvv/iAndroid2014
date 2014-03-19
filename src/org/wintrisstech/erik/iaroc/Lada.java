package org.wintrisstech.erik.iaroc;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.sensors.UltraSonicSensors;

/**
 * A Lada is an implementation of the IRobotCreateInterface, inspired by Vic's
 * awesome API. It is entirely event driven.
 *
 * @author Erik
 */
public class Lada extends IRobotCreateAdapter {

    private static final String TAG = "Lada";
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;

    /**
     * Constructs a Lada, an amazing machine!
     *
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
        song(0, new int[]{58, 10});
    }

    public void initialize() throws ConnectionLostException {
        dashboard.log("===========Start===========");
        readSensors(SENSORS_GROUP_ID6);
        dashboard.log("Battery Charge = " + getBatteryCharge()
                + ", 3,000 = Full charge");
    }

    /**
     * This method is called repeatedly
     *
     * @throws ConnectionLostException
     */
    public void loop() throws ConnectionLostException {
    	driveDirect(100,  100);
        try {
            sonar.read();
        } catch (InterruptedException ex) {
        }
        dashboard.log("L: " + sonar.getLeftDistance()
                + " F: " + sonar.getFrontDistance()
                + " R: " + sonar.getRightDistance());
    }
}
