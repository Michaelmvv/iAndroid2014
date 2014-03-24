package org.wintrisstech.erik.iaroc;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * A high level interface to the iRobot series of Roomba/Create robots. It
 * encapsulates most of the commands and sensors specified in Create Open
 * Interface_v2.pdf </a> . Reading that document is recommended in order to get
 * a better understanding of how to work with the Create.
 *
 */
public interface IRobotCreateInterface {

    /**
     * Identifies a demo where the Create attempts to cover an entire room using
     * a combination of behaviors, such as random bounce, wall following, and
     * spiraling.
     *
     * @see #demo(int)
     */
    public static final int DEMO_COVER = 0;
    /**
     * Identifies a demo similar to the Cover demo, but with one exception. If
     * the Create sees an infrared signal from an iRobot Home Base, it uses that
     * signal to dock with the Home Base and recharge itself.
     *
     * @see #demo(int)
     */
    public static final int DEMO_COVER_AND_DOCK = 1;
    /**
     * Identifies a demo where the Create covers an area around its starting
     * position by spiraling outward, then inward
     *
     * @see #demo(int)
     */
    public static final int DEMO_SPOT_COVER = 2;
    /**
     * Identifies a demo where the Create drives in search of a wall. Once a
     * wall is found, Create drives along the wall, traveling around the
     * circumference of the room.
     *
     * @see #demo(int)
     */
    public static final int DEMO_MOUSE = 3;
    /**
     * Identifies a demo where the Create continuously drives in a figure 8
     * pattern.
     *
     * @see #demo(int)
     */
    public static final int DEMO_DRIVE_FIGURE_EIGHT = 4;
    /**
     * Identifies a demo where the Create drives forward when pushed from
     * behind. If Create hits an obstacle while driving, it drives away from the
     * obstacle.
     *
     * @see #demo(int)
     */
    public static final int DEMO_WIMP = 5;
    /**
     * Identifies a demo where the Create drives toward an iRobot Virtual Wall
     * as long as the back and sides of the virtual wall receiver are blinded by
     * black electrical tape. <p> A Virtual Wall emits infrared signals that
     * Create sees with its Omnidirectional Infrared Receiver, located on top of
     * the bumper. <p> If you want the Create to home in on a Virtual Wall,
     * cover all but a small opening in the front of the infrared receiver with
     * black electrical tape. <p> Create spins to locate a virtual wall, then
     * drives toward it. Once Create hits the wall or another obstacle, it
     * stops.
     *
     * @see #demo(int)
     */
    public static final int DEMO_HOME = 6;
    /**
     * Identifies a demo identical to the Home demo, except Create drives into
     * multiple virtual walls by bumping into one, turning around, driving to
     * the next virtual wall, bumping into it and turning around to bump into
     * the next virtual wall.
     *
     * @see #demo(int)
     */
    public static final int DEMO_TAG = 7;
    /**
     * Identifies a demo where the Create plays the notes of Pachelbel's Canon
     * in sequence when cliff sensors are activated.
     *
     * @see #demo(int)
     */
    public static final int DEMO_PACHEBEL = 8;
    /**
     * Identifies a demo where the Create plays a note of a chord for each of
     * its four cliff sensors. Select the chord using the bumper, as follows:
     * <ul> <li> No bumper: G major. <br> <li> Right/left bumper: D major 7 <br>
     * <li> Both bumpers (center): C major </ul>
     *
     * @see #demo(int)
     */
    public static final int DEMO_BANJO = 9;
    /**
     * Identifier used to abort a demo. Calling
     * <code>demo(DEMO_ABORT)</code> aborts the current demo.
     *
     * @see #demo(int)
     */
    public static final int DEMO_ABORT = 255;
    /**
     * Identifies the sensor Group 0. This group includes the sensors identified
     * by the following ids: <p> SENSORS_BUMPS_AND_WHEEL_DROPS, <br>
     * SENSORS_WALL, <br> SENSORS_CLIFF_LEFT, <br> SENSORS_CLIFF_FRONT_LEFT,
     * <br> SENSORS_CLIFF_FRONT_RIGHT, <br> SENSORS_CLIFF_RIGHT, <br>
     * SENSORS_VIRTUAL_WALL, <br>
     * SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS, <br> SENSORS_DUMMY1,
     * <br> SENSORS_DUMMY2, <br> SENSORS_INFRARED_BYTE, <br> SENSORS_BUTTONS,
     * <br> SENSORS_DISTANCE, <br> SENSORS_ANGLE, <br> SENSORS_CHARGING_STATE,
     * <br> SENSORS_VOLTAGE, <br> SENSORS_CURRENT, <br>
     * SENSORS_BATTERY_TEMPERATURE, <br> SENSORS_BATTERY_CHARGE, <br>
     * SENSORS_BATTERY_CAPACITY
     */
    public static final int SENSORS_GROUP_ID0 = 0;
    /**
     * Identifies the sensor Group 1. This group includes the sensors identified
     * by the following ids: <p> SENSORS_BUMPS_AND_WHEEL_DROPS, <br>
     * SENSORS_WALL, <br> SENSORS_CLIFF_LEFT, <br> SENSORS_CLIFF_FRONT_LEFT,
     * <br> SENSORS_CLIFF_FRONT_RIGHT, <br> SENSORS_CLIFF_RIGHT, <br>
     * SENSORS_VIRTUAL_WALL, <br>
     * SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS, <br> SENSORS_DUMMY1,
     * <br> SENSORS_DUMMY22
     */
    public static final int SENSORS_GROUP_ID1 = 1;
    /**
     * Identifies the sensor Group 2. This group includes the sensors identified
     * by the following ids: <p> SENSORS_INFRARED_BYTE, <br> SENSORS_BUTTONS,
     * <br> SENSORS_DISTANCE, <br> SENSORS_ANGLE
     */
    public static final int SENSORS_GROUP_ID2 = 2;
    /**
     * Identifies the sensor Group 3. This group includes the sensors identified
     * by the following ids: <p> SENSORS_CHARGING_STATE, SENSORS_VOLTAGE, <br>
     * SENSORS_CURRENT, <br> SENSORS_BATTERY_TEMPERATURE, <br>
     * SENSORS_BATTERY_CHARGE, <br> SENSORS_BATTERY_CAPACITY
     */
    public static final int SENSORS_GROUP_ID3 = 3;
    /**
     * Identifies the sensor Group 4. This group includes the sensors identified
     * by the following ids: <p> SENSORS_WALL_SIGNAL, <br>
     * SENSORS_CLIFF_LEFT_SIGNAL, <br> SENSORS_CLIFF_FRONT_LEFT_SIGNAL, <br>
     * SENSORS_CLIFF_FRONT_RIGHT_SIGNAL, <br> SENSORS_CLIFF_RIGHT_SIGNAL, <br>
     * SENSORS_CARGO_BAY_DIGITAL_INPUTS, <br> SENSORS_CARGO_BAY_ANALOG_SIGNAL,
     * <br> SENSORS_CHARGING_SOURCES_AVAILABLE
     */
    public static final int SENSORS_GROUP_ID4 = 4;
    /**
     * Identifies the sensor Group 5. This group includes the sensors identified
     * by the following ids: <p> SENSORS_OI_MODE, <br> SENSORS_SONG_NUMBER, <br>
     * SENSORS_SONG_PLAYING, <br> SENSORS_NUMBER_OF_STREAM_PACKETS, <br>
     * SENSORS_REQUESTED_VELOCITY, <br> SENSORS_REQUESTED_RADIUS, <br>
     * SENSORS_REQUESTED_RIGHT_VELOCITY, <br> SENSORS_REQUESTED_LEFT_VELOCITY
     */
    public static final int SENSORS_GROUP_ID5 = 5;
    /**
     * Identifies the sensor Group 6. This group includes the sensors identified
     * by the following ids: <p> SENSORS_BUMPS_AND_WHEEL_DROPS, <br>
     * SENSORS_WALL, <br> SENSORS_CLIFF_LEFT, <br> SENSORS_CLIFF_FRONT_LEFT,
     * <br> SENSORS_CLIFF_FRONT_RIGHT, <br> SENSORS_CLIFF_RIGHT, <br>
     * SENSORS_VIRTUAL_WALL, <br>
     * SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS, <br> SENSORS_DUMMY1,
     * <br> SENSORS_DUMMY2, <br> SENSORS_INFRARED_BYTE, <br> SENSORS_BUTTONS,
     * <br> SENSORS_DISTANCE, <br> SENSORS_ANGLE, <br> SENSORS_CHARGING_STATE,
     * <br> SENSORS_VOLTAGE, <br> SENSORS_CURRENT, <br>
     * SENSORS_BATTERY_TEMPERATURE, <br> SENSORS_BATTERY_CHARGE, <br>
     * SENSORS_BATTERY_CAPACITY, <br> SENSORS_WALL_SIGNAL, <br>
     * SENSORS_CLIFF_LEFT_SIGNAL, <br> SENSORS_CLIFF_FRONT_LEFT_SIGNAL, <br>
     * SENSORS_CLIFF_FRONT_RIGHT_SIGNAL, <br> SENSORS_CLIFF_RIGHT_SIGNAL, <br>
     * SENSORS_CARGO_BAY_DIGITAL_INPUTS, <br> SENSORS_CARGO_BAY_ANALOG_SIGNAL,
     * <br> SENSORS_CHARGING_SOURCES_AVAILABLE, <br> SENSORS_OI_MODE, <br>
     * SENSORS_SONG_NUMBER, <br> SENSORS_SONG_PLAYING, <br>
     * SENSORS_NUMBER_OF_STREAM_PACKETS, <br> SENSORS_REQUESTED_VELOCITY, <br>
     * SENSORS_REQUESTED_RADIUS, <br> SENSORS_REQUESTED_RIGHT_VELOCITY, <br>
     * SENSORS_REQUESTED_LEFT_VELOCITY
     */
    public static final int SENSORS_GROUP_ID6 = 6;
    /**
     * Identifies the left and right bump sensor and left, right and caster
     * wheel drop sensors.
     *
     * @see #isBumpLeft()
     * @see #isBumpRight()
     * @see #isWheelDropLeft()
     * @see #isWheelDropRight()
     * @see #isWheelDropCaster()
     */
    public static final int SENSORS_BUMPS_AND_WHEEL_DROPS = 7;
    /**
     * Identifies the Wall sensor.
     *
     * @see #isWall()
     */
    public static final int SENSORS_WALL = 8;
    /**
     * Identifies the Left Cliff sensor.
     *
     * @see #isCliffLeft()
     *
     */
    public static final int SENSORS_CLIFF_LEFT = 9;
    /**
     * Identifies the Front Left Cliff sensor.
     *
     * @see #isCliffFrontLeft()
     */
    public static final int SENSORS_CLIFF_FRONT_LEFT = 10;
    /**
     * Identifies the Front Right Cliff sensor.
     *
     * @see #isCliffFrontRight()
     */
    public static final int SENSORS_CLIFF_FRONT_RIGHT = 11;
    /**
     * Identifies the Right Cliff sensor.
     *
     * @see #isCliffRight()
     */
    public static final int SENSORS_CLIFF_RIGHT = 12;
    /**
     * Identifies the Virtual Wall sensor.
     *
     * @see #isVirtualWall()
     */
    public static final int SENSORS_VIRTUAL_WALL = 13;
    /**
     * Identifies the low side driver and wheel overcurrent sensors.
     *
     * @see #isLowSideDriver0Overcurrent()
     * @see #isLowSideDriver1Overcurrent()
     * @see #isLowSideDriver2Overcurrent()
     * @see #isLeftWheelOvercurrent()
     * @see #isRightWheelOvercurrent()
     *
     */
    public static final int SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS = 14;
    /**
     * Identifies a dummy sensor.
     */
    public static final int SENSORS_DUMMY1 = 15;
    /**
     * Identifies a dummy sensor.
     */
    public static final int SENSORS_DUMMY2 = 16;
    /**
     * Identifies the Infrared sensor.
     *
     * @see #getInfraredByte()
     */
    public static final int SENSORS_INFRARED_BYTE = 17;
    /**
     * Identifies the button sensors.
     *
     * @see #isAdvanceButtonDown()
     * @see #isPlayButtonDown()
     */
    public static final int SENSORS_BUTTONS = 18;
    /**
     * Identifies the Distance sensor.
     *
     * @see #getDistance()
     */
    public static final int SENSORS_DISTANCE = 19;
    /**
     * Identifies the Angle sensor.
     *
     * @see #getAngle()
     */
    public static final int SENSORS_ANGLE = 20;
    /**
     * Identifies the charging state sensor.
     *
     * @see #getChargingState()
     */
    public static final int SENSORS_CHARGING_STATE = 21;
    /**
     * Identifies the voltage sensor.
     *
     * @see #getVoltage()
     */
    public static final int SENSORS_VOLTAGE = 22;
    /**
     * Identifies the Current sensor.
     *
     * @see #getCurrent()
     */
    public static final int SENSORS_CURRENT = 23;
    /**
     * Identifies the Battery Temperature sensor.
     *
     * @see #getBatteryTemperature()
     */
    public static final int SENSORS_BATTERY_TEMPERATURE = 24;
    /**
     * Identifies the Battery Charge sensor.
     *
     * @see #getBatteryCharge()
     */
    public static final int SENSORS_BATTERY_CHARGE = 25;
    /**
     * Identifies the Battery Capacity sensor.
     *
     * @see #getBatteryCapacity()
     */
    public static final int SENSORS_BATTERY_CAPACITY = 26;
    /**
     * Identifies the Wall Signal sensor.
     *
     * @see #getWallSignal()
     */
    public static final int SENSORS_WALL_SIGNAL = 27;
    /**
     * Identifies the Cliff Left Signal sensor.
     *
     * @see #getCliffLeftSignal()
     */
    public static final int SENSORS_CLIFF_LEFT_SIGNAL = 28;
    /**
     * Identifies the Cliff Front Left Signal sensor.
     *
     * @see #getCliffFrontLeftSignal()
     */
    public static final int SENSORS_CLIFF_FRONT_LEFT_SIGNAL = 29;
    /**
     * Identifies the Cliff Front Right Signal sensor.
     *
     * @see #getCliffFrontRightSignal()
     */
    public static final int SENSORS_CLIFF_FRONT_RIGHT_SIGNAL = 30;
    /**
     * Identifies the Cliff Right Signal sensor.
     *
     * @see #getCliffRightSignal()
     */
    public static final int SENSORS_CLIFF_RIGHT_SIGNAL = 31;
    /**
     * Identifies the Cargo Bay Digital Inputs sensors.
     *
     * @see #isCargoBayDigitalInput0High()
     * @see #isCargoBayDigitalInput1High()
     * @see #isCargoBayDigitalInput2High()
     * @see #isCargoBayDigitalInput3High()
     * @see #isCargoBayDeviceDetectBaudRateChangeHigh()
     */
    public static final int SENSORS_CARGO_BAY_DIGITAL_INPUTS = 32;
    /**
     * Identifies the Cargo Bay Analog Signal sensor.
     *
     * @see #getCargoBayAnalogSignal()
     */
    public static final int SENSORS_CARGO_BAY_ANALOG_SIGNAL = 33;
    /**
     * Identifies the Available Charging Sources sensors.
     *
     * @see #isHomeBaseChargerAvailable()
     * @see #isInternalChargerAvailable()
     */
    public static final int SENSORS_CHARGING_SOURCES_AVAILABLE = 34;
    /**
     * Identifies the OI Mode sensor
     *
     * @see #getOiMode()
     */
    public static final int SENSORS_OI_MODE = 35;
    /**
     * Identifies the Song Number sensor.
     *
     * @see #getSongNumber()
     */
    public static final int SENSORS_SONG_NUMBER = 36;
    /**
     * Identifies the Song Playing sensor.
     *
     * @see #isSongPlaying()
     */
    public static final int SENSORS_SONG_PLAYING = 37;
    /**
     * Identifies the Stream Packets sensor.
     */
    public static final int SENSORS_NUMBER_OF_STREAM_PACKETS = 38;
    /**
     * Identifies the Requested Velocity sensor.
     *
     * @see #getRequestedVelocity()
     */
    public static final int SENSORS_REQUESTED_VELOCITY = 39;
    /**
     * Identifies the Requested Radius sensor.
     *
     * @see #getRequestedRadius()
     */
    public static final int SENSORS_REQUESTED_RADIUS = 40;
    /**
     * Identifies the Requested Right Velocity.
     *
     * @see #getRequestedRightVelocity()
     */
    public static final int SENSORS_REQUESTED_RIGHT_VELOCITY = 41;
    /**
     * Identifies the Requested Left Velocity sensor.
     *
     * @see #getRequestedLeftVelocity()
     */
    public static final int SENSORS_REQUESTED_LEFT_VELOCITY = 42;
    /**
     * The maximum number of sensor ids.
     */
    public static final int SENSORS_MAX = 43;
    /**
     * Names for sensor ids. Sensor Id = X has name = SENSOR_NAMES[X]. The names
     * may be useful for debugging or tracing purposes.
     */
    public static final String[] SENSOR_NAMES = new String[]{
        "SENSORS_GROUP_ID0",
        "SENSORS_GROUP_ID1",
        "SENSORS_GROUP_ID2",
        "SENSORS_GROUP_ID3",
        "SENSORS_GROUP_ID4",
        "SENSORS_GROUP_ID5",
        "SENSORS_GROUP_ID6",
        "SENSORS_BUMPS_AND_WHEEL_DROPS",
        "SENSORS_WALL",
        "SENSORS_CLIFF_LEFT",
        "SENSORS_CLIFF_FRONT_LEFT",
        "SENSORS_CLIFF_FRONT_RIGHT",
        "SENSORS_CLIFF_RIGHT",
        "SENSORS_VIRTUAL_WALL",
        "SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS",
        "SENSORS_DUMMY1",
        "SENSORS_DUMMY2",
        "SENSORS_INFRARED_BYTE",
        "SENSORS_BUTTONS",
        "SENSORS_DISTANCE",
        "SENSORS_ANGLE",
        "SENSORS_CHARGING_STATE",
        "SENSORS_VOLTAGE",
        "SENSORS_CURRENT",
        "SENSORS_BATTERY_TEMPERATURE",
        "SENSORS_BATTERY_CHARGE",
        "SENSORS_BATTERY_CAPACITY",
        "SENSORS_WALL_SIGNAL",
        "SENSORS_CLIFF_LEFT_SIGNAL",
        "SENSORS_CLIFF_FRONT_LEFT_SIGNAL",
        "SENSORS_CLIFF_FRONT_RIGHT_SIGNAL",
        "SENSORS_CLIFF_RIGHT_SIGNAL",
        "SENSORS_CARGO_BAY_DIGITAL_INPUTS",
        "SENSORS_CARGO_BAY_ANALOG_SIGNAL",
        "SENSORS_CHARGING_SOURCES_AVAILABLE",
        "SENSORS_OI_MODE",
        "SENSORS_SONG_NUMBER",
        "SENSORS_SONG_PLAYING",
        "SENSORS_NUMBER_OF_STREAM_PACKETS",
        "SENSORS_REQUESTED_VELOCITY",
        "SENSORS_REQUESTED_RADIUS",
        "SENSORS_REQUESTED_RIGHT_VELOCITY",
        "SENSORS_REQUESTED_LEFT_VELOCITY"
    };
    /**
     * The are 7 groups of sensors identified by sensor ids 0 through 6.
     * SENSOR_GROUP[X] is the group identified by sensor id SENSORS_GROUP_IDX.
     */
    public static final int[][] SENSOR_GROUPS = new int[][]{
        //Group 0
        {
            SENSORS_BUMPS_AND_WHEEL_DROPS,
            SENSORS_WALL,
            SENSORS_CLIFF_LEFT,
            SENSORS_CLIFF_FRONT_LEFT,
            SENSORS_CLIFF_FRONT_RIGHT,
            SENSORS_CLIFF_RIGHT,
            SENSORS_VIRTUAL_WALL,
            SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS,
            SENSORS_DUMMY1,
            SENSORS_DUMMY2,
            SENSORS_INFRARED_BYTE,
            SENSORS_BUTTONS,
            SENSORS_DISTANCE,
            SENSORS_ANGLE,
            SENSORS_CHARGING_STATE,
            SENSORS_VOLTAGE,
            SENSORS_CURRENT,
            SENSORS_BATTERY_TEMPERATURE,
            SENSORS_BATTERY_CHARGE,
            SENSORS_BATTERY_CAPACITY
        },
        // Group 1
        {
            SENSORS_BUMPS_AND_WHEEL_DROPS,
            SENSORS_WALL,
            SENSORS_CLIFF_LEFT,
            SENSORS_CLIFF_FRONT_LEFT,
            SENSORS_CLIFF_FRONT_RIGHT,
            SENSORS_CLIFF_RIGHT,
            SENSORS_VIRTUAL_WALL,
            SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS,
            SENSORS_DUMMY1,
            SENSORS_DUMMY2},
        // Group 2
        {
            SENSORS_INFRARED_BYTE,
            SENSORS_BUTTONS,
            SENSORS_DISTANCE,
            SENSORS_ANGLE
        },
        // Group 3
        {
            SENSORS_CHARGING_STATE,
            SENSORS_VOLTAGE,
            SENSORS_CURRENT,
            SENSORS_BATTERY_TEMPERATURE,
            SENSORS_BATTERY_CHARGE,
            SENSORS_BATTERY_CAPACITY
        },
        //Group 4
        {
            SENSORS_WALL_SIGNAL,
            SENSORS_CLIFF_LEFT_SIGNAL,
            SENSORS_CLIFF_FRONT_LEFT_SIGNAL,
            SENSORS_CLIFF_FRONT_RIGHT_SIGNAL,
            SENSORS_CLIFF_RIGHT_SIGNAL,
            SENSORS_CARGO_BAY_DIGITAL_INPUTS,
            SENSORS_CARGO_BAY_ANALOG_SIGNAL,
            SENSORS_CHARGING_SOURCES_AVAILABLE
        },
        // Group 5
        {
            SENSORS_OI_MODE,
            SENSORS_SONG_NUMBER,
            SENSORS_SONG_PLAYING,
            SENSORS_NUMBER_OF_STREAM_PACKETS,
            SENSORS_REQUESTED_VELOCITY,
            SENSORS_REQUESTED_RADIUS,
            SENSORS_REQUESTED_RIGHT_VELOCITY,
            SENSORS_REQUESTED_LEFT_VELOCITY
        },
        // Group 6
        {
            SENSORS_BUMPS_AND_WHEEL_DROPS,
            SENSORS_WALL,
            SENSORS_CLIFF_LEFT,
            SENSORS_CLIFF_FRONT_LEFT,
            SENSORS_CLIFF_FRONT_RIGHT,
            SENSORS_CLIFF_RIGHT,
            SENSORS_VIRTUAL_WALL,
            SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS,
            SENSORS_DUMMY1,
            SENSORS_DUMMY2,
            SENSORS_INFRARED_BYTE,
            SENSORS_BUTTONS,
            SENSORS_DISTANCE,
            SENSORS_ANGLE,
            SENSORS_CHARGING_STATE,
            SENSORS_VOLTAGE,
            SENSORS_CURRENT,
            SENSORS_BATTERY_TEMPERATURE,
            SENSORS_BATTERY_CHARGE,
            SENSORS_BATTERY_CAPACITY,
            SENSORS_WALL_SIGNAL,
            SENSORS_CLIFF_LEFT_SIGNAL,
            SENSORS_CLIFF_FRONT_LEFT_SIGNAL,
            SENSORS_CLIFF_FRONT_RIGHT_SIGNAL,
            SENSORS_CLIFF_RIGHT_SIGNAL,
            SENSORS_CARGO_BAY_DIGITAL_INPUTS,
            SENSORS_CARGO_BAY_ANALOG_SIGNAL,
            SENSORS_CHARGING_SOURCES_AVAILABLE,
            SENSORS_OI_MODE,
            SENSORS_SONG_NUMBER,
            SENSORS_SONG_PLAYING,
            SENSORS_NUMBER_OF_STREAM_PACKETS,
            SENSORS_REQUESTED_VELOCITY,
            SENSORS_REQUESTED_RADIUS,
            SENSORS_REQUESTED_RIGHT_VELOCITY,
            SENSORS_REQUESTED_LEFT_VELOCITY
        }
    };

    /**
     * Commands Create to start a built-in demo.
     *
     * @param demoId one of the DEMO_* constants
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void demo(int demoId) throws ConnectionLostException;

    /**
     * Controls the Create's drive wheels. Larger values for the radius makes
     * the Create drive straighter, while the smaller values make the Create
     * turn more. The radius is measured from the center of the turning circle
     * to the center of Create. A positive velocity and a positive radius make
     * the Create drive forward while turning toward the left. A negative radius
     * makes the Create turn toward the right. Special cases for the radius make
     * Create turn in place or drive straight, as specified below. A negative
     * velocity makes Create drive backward. Available in modes: Safe or Full
     * <p> NOTE: Internal and environmental restrictions may prevent Create from
     * accurately carrying out some drive commands. For example, it may not be
     * possible for Create to drive at full speed in an arc with a large radius
     * of curvature. <p> Special cases of radius: <ul> <li> Straight 32768 or
     * 32767 = hex 8000 or 7FFF<br> <li> Turn in place clockwise = hex FFFF <br>
     * <li> Turn in place counter-clockwise = hex 0001 </ul>
     *
     * @param velocity the average velocity of the wheels in mm/s. Range is -500
     * to 500
     * @param radius the turn radius in mm. Range is -2000 to 2000<br>
     * @throws ConnectionLostException
     * @throws InterruptedException
     *
     */
    public void drive(int velocity, int radius)
            throws ConnectionLostException;

    /**
     * This method lets you control the forward and backward motion of the
     * Create by specifying the velocity of each wheel independently. A positive
     * velocity makes that wheel drive forward, while a negative velocity makes
     * it drive backward. Available in modes: Safe or Full
     *
     * @param rightVelocity Right wheel velocity in mm/s. Range is -500 to 500
     * @param leftVelocity Left wheel velocity in mm/s. Range is -500 to 500
     * @throws ConnectionLostException
     * @throws InterruptedException
     *
     */
    public void driveDirect(int rightVelocity, int leftVelocity)
            throws ConnectionLostException;

    /**
     * This method gives you complete control over the Create by putting the OI
     * into Full mode, and turning off the cliff, wheel-drop and internal
     * charger safety features. That is, in Full mode, the Create executes any
     * command that you send it, even if the internal charger is plugged in, or
     * the robot senses a cliff or wheel drop. Available in modes: Passive,
     * Safe, or Full <br>
     *
     * Note: Before invoking this method a connection to the Create must have
     * been established.
     *
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void full() throws ConnectionLostException;

    /**
     * Gets the angle in degrees that the Create has turned from the time before
     * last that the angle sensor was read to the last time the angle sensor was
     * read. Counter-clockwise angles are positive and clockwise angles are
     * negative. If the value is not polled frequently enough, it is capped at
     * its minimum or maximum, which are -32768 and 32767 respectively. <p>
     * <b>NOTE:</b> The Create uses wheel encoders to measure distance and
     * angle. If the wheels slip, the actual distance or angle traveled may
     * differ from the Create's measurements. <p> <b>NOTE:</b> This method
     * returns a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return The angle in degrees that the Create has turned.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getAngle();

    /**
     * Gets the estimated charge capacity of the Create's battery in
     * milliamphours (mAh). Note that this value is inaccurate if you are using
     * the alkaline battery pack. <p> <b>NOTE:</b> This method returns a locally
     * stored value previously read from the Create. It is the client's
     * responsibility to read the sensor values from the Create prior to calling
     * this method in order to ensure fresh values.
     *
     * @return The estimated charge capacity of the Create's battery in
     * milliamphours (mAh).
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getBatteryCapacity();

    /**
     * The current charge of Create's battery in milliamp-hours (mAh). The
     * charge value decreases as the battery is depleted during running and
     * increases when the battery is charged. <br> Note that this value will not
     * be accurate if you are using the alkaline battery pack. <p> <b>NOTE:</b>
     * This method returns a locally stored value previously read from the
     * Create. It is the client's responsibility to read the sensor values from
     * the Create prior to calling this method in order to ensure fresh values.
     *
     * @return The battery capacity in mAh.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getBatteryCharge();

    /**
     * The temperature of Create's battery in degrees Celsius. <br> <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return The battery temperature in degrees Celsius.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getBatteryTemperature();

    /**
     * The 10-bit value of the analog input on the 25-pin Cargo Bay Connector is
     * returned. 0 = 0 volts; 1023 = 5 volts. The analog input is on pin 4. <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return The analog input signal on the 25-pin Cargo Bay Connector in
     * 5/1023 fractions of one volt.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getCargoBayAnalogSignal();

    /**
     * This code indicates Create's current charging state. The state is encoded
     * as follows: <ul> <li> 0 -- Not charging <li> 1 -- Reconditioning charging
     * <li> 2 -- Full charging <li> 3 -- Trickle chraging <li> 4 -- Waiting <li>
     * 5 -- Fault condition </ul>
     *
     * <p> <b>NOTE:</b> This method returns a locally stored value previously
     * read from the Create. It is the client's responsibility to read the
     * sensor values from the Create prior to calling this method in order to
     * ensure fresh values.
     *
     * @return The Create's charging state.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getChargingState();

    /**
     * Gets the strength of the front left cliff sensor's signal. The strength
     * is returned as a value between 0 and 4095.
     *
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return the strength of the front left cliff sensor signal.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getCliffFrontLeftSignal();

    /**
     * Gets the strength of the front right cliff sensor's signal. The strength
     * is returned as a value between 0 and 4095.
     *
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return the strength of the front left cliff sensor signal.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getCliffFrontRightSignal();

    /**
     * Gets the strength of the left cliff sensor's signal. The strength is
     * returned as a value between 0 and 4095. <b>NOTE:</b> This method returns
     * a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return the strength of the left cliff sensor signal.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getCliffLeftSignal();

    /**
     * Gets the strength of the right cliff sensor's signal. The strength is
     * returned as a value between 0 and 4095. <b>NOTE:</b> This method returns
     * a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return the strength of the right cliff sensor signal.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getCliffRightSignal();

    /**
     * Gets the current, in milliAmps, flowing into or out of the Create's
     * battery. Negative currents indicate that the current is flowing out of
     * the battery, as during normal running. Positive currents indicate that
     * the current is flowing into the battery, as during charging. <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return The current in mA
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getCurrent();

    /**
     * Gets the distance in mm that the Create has travelled from the time
     * before last that the distance sensor was read to the last time the
     * distance sensor was read. This is the same as the sum of the distance
     * traveled by both wheels divided by two. Positive values indicate travel
     * in the forward direction; negative values indicate travel in the reverse
     * direction. If the value is not polled frequently enough, it is capped at
     * its minimum or maximum, which are -32768 amd 32767 respectively. <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return the travelled distance in mm
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getDistance();

    /**
     * Gets the value of the IR byte received by the Create. The value is
     * encoded as follows: <p> Remote control values: <ul> <li> 129 Left <li>
     * 130 Forward <li> 131 Right <li> 132 Spot <li> 133 Max <li> 134 Small <li>
     * 135 Medium <li> 136 Large / Clean <li> 137 PAUSE <li> 138 Power <li> 139
     * arc-forward-left <li> 140 arc-forward-right <li> 141 drive-stop </ul>
     * Remote scheduling values: <ul> <li> 142 Send All <li> 143 Seek Dock </ul>
     * Home base values <ul> <li> 240 Reserved <li> 248 Red Buoy <li> 244 Green
     * Buoy <li> 242 Force Field <li> 252 Red Buoy and Green Buoy <li> 250 Red
     * Buoy and Force Field <li> 246 Green Buoy and Force Field <li> 254 Red
     * Buoy, Green Buoy and Force Field <li> 255 No value received </ul> <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return One of the values above.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getInfraredByte();

    /**
     * Gets the Create's OI mode. The returned value is encoded as follows: <ul>
     * <li> 0 - Off <li> 1 - Passive <li> 2 - Safe <li> 3 - Full <ul>
     *
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return The Create's OI mode.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getOiMode();

    /**
     * Gets the left wheel velocity in mm/s most recently requested using the
     * driveDirect() or drive() method. <p> <b>NOTE:</b> This method returns a
     * locally stored value previously read from the Create. It is the client's
     * responsibility to read the sensor values from the Create prior to calling
     * this method in order to ensure fresh values.
     *
     * @return The most recently requested left wheel velocity in mm/s
     * @see #driveDirect(int leftVelocity, int rightVelocity)
     * @see #drive(int velocity, int radius)
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getRequestedLeftVelocity();

    /**
     * Gets the turn radius in mm most recently requested using the drive()
     * method. <p> <b>NOTE:</b> This method returns a locally stored value
     * previously read from the Create. It is the client's responsibility to
     * read the sensor values from the Create prior to calling this method in
     * order to ensure fresh values.
     *
     * @return The most recently requested turn radius in mm.
     * @see #drive(int velocity, int radius)
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getRequestedRadius();

    /**
     * Gets the right wheel velocity in mm/s most recently requested using the
     * driveDirect() or drive() method. <p> <b>NOTE:</b> This method returns a
     * locally stored value previously read from the Create. It is the client's
     * responsibility to read the sensor values from the Create prior to calling
     * this method in order to ensure fresh values.
     *
     * @return The most recently requested right wheel velocity in mm/s.
     * @see #driveDirect(int leftVelocity, int rightVelocity)
     * @see #drive(int velocity, int radius)
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getRequestedRightVelocity();

    /**
     * Gets the average wheel velocity in mm/s most recently requested using the
     * driveDirect() or drive() method. <p> <b>NOTE:</b> This method returns a
     * locally stored value previously read from the Create. It is the client's
     * responsibility to read the sensor values from the Create prior to calling
     * this method in order to ensure fresh values.
     *
     * @return The most recently requested average wheel velocity in mm/s.
     * @see #driveDirect(int leftVelocity, int rightVelocity)
     * @see #drive(int velocity, int radius)
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getRequestedVelocity();

    /**
     * Gets the currently selected OI song song number. <p> <b>NOTE:</b> This
     * method returns a locally stored value previously read from the Create. It
     * is the client's responsibility to read the sensor values from the Create
     * prior to calling this method in order to ensure fresh values.
     *
     * @return the song number
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getSongNumber();

    /**
     * Gets the voltage of Create's battery in millivolts (mV). <p> <b>NOTE:</b>
     * This method returns a locally stored value previously read from the
     * Create. It is the client's responsibility to read the sensor values from
     * the Create prior to calling this method in order to ensure fresh values.
     *
     * @return the battery voltage in mV
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getVoltage();

    /**
     * Gets the strength of the wall sensor's signal as a number between 0 and
     * 4095. <p> <b>NOTE:</b> This method returns a locally stored value
     * previously read from the Create. It is the client's responsibility to
     * read the sensor values from the Create prior to calling this method in
     * order to ensure fresh values.
     *
     * @return the wall signal's strength
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public int getWallSignal();

    /**
     * Gets the state of the Advance button. <p> <b>NOTE:</b> This method
     * returns a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return true if the Advance button is pressed.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isAdvanceButtonDown();

    /**
     * Gets the state of the left bumper. <p> <b>NOTE:</b> This method returns a
     * locally stored value previously read from the Create. It is the client's
     * responsibility to read the sensor values from the Create prior to calling
     * this method in order to ensure fresh values.
     *
     * @return true is the left bumper is depressed.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isBumpLeft();

    /**
     * Gets the state of the right bumper. <p> <b>NOTE:</b> This method returns
     * a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return true is the right bumper is depressed.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isBumpRight();

    /**
     * Gets the state of the Device Detetct / Baud Rate Change pin. <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return true is the pin is high.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCargoBayDeviceDetectBaudRateChangeHigh();

    /**
     * Gets the state of the Cargo Bay Digital Input 0 pin. <p> <b>NOTE:</b>
     * This method returns a locally stored value previously read from the
     * Create. It is the client's responsibility to read the sensor values from
     * the Create prior to calling this method in order to ensure fresh values.
     *
     * @return true is the pin is high (5V)
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCargoBayDigitalInput0High();

    /**
     * Gets the state of the Cargo Bay Digital Input 1 pin. <p> <b>NOTE:</b>
     * This method returns a locally stored value previously read from the
     * Create. It is the client's responsibility to read the sensor values from
     * the Create prior to calling this method in order to ensure fresh values.
     *
     * @return true is the pin is high (5V)
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCargoBayDigitalInput1High();

    /**
     * Gets the state of the Cargo Bay Digital Input 2 pin. <p> <b>NOTE:</b>
     * This method returns a locally stored value previously read from the
     * Create. It is the client's responsibility to read the sensor values from
     * the Create prior to calling this method in order to ensure fresh values.
     *
     * @return true is the pin is high
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCargoBayDigitalInput2High();

    /**
     * Gets the state of the Cargo Bay Digital Input 3 pin. <p> <b>NOTE:</b>
     * This method returns a locally stored value previously read from the
     * Create. It is the client's responsibility to read the sensor values from
     * the Create prior to calling this method in order to ensure fresh values.
     *
     * @return true is the pin is high
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCargoBayDigitalInput3High();

    /**
     * Gets the state of the front left cliff sensor. <p> <b>NOTE:</b> This
     * method returns a locally stored value previously read from the Create. It
     * is the client's responsibility to read the sensor values from the Create
     * prior to calling this method in order to ensure fresh values.
     *
     * @return true if a cliff has been detected
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCliffFrontLeft();

    /**
     * Gets the state of the front right cliff sensor. <p> <b>NOTE:</b> This
     * method returns a locally stored value previously read from the Create. It
     * is the client's responsibility to read the sensor values from the Create
     * prior to calling this method in order to ensure fresh values.
     *
     * @return true if a cliff has been detected
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCliffFrontRight();

    /**
     * Gets the state of the left cliff sensor. <p> <b>NOTE:</b> This method
     * returns a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return true if a cliff has been detected
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCliffLeft();

    /**
     * Gets the state of the right cliff sensor. <p> <b>NOTE:</b> This method
     * returns a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return true if a cliff has been detected
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isCliffRight();

    /**
     * Gets the availability of the home base charger. <p> <b>NOTE:</b> This
     * method returns a locally stored value previously read from the Create. It
     * is the client's responsibility to read the sensor values from the Create
     * prior to calling this method in order to ensure fresh values.
     *
     * @return true if the home base charger is available
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isHomeBaseChargerAvailable();

    /**
     * Gets the availability of the internal charger. <p> <b>NOTE:</b> This
     * method returns a locally stored value previously read from the Create. It
     * is the client's responsibility to read the sensor values from the Create
     * prior to calling this method in order to ensure fresh values.
     *
     * @return true if the internal charger is available
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isInternalChargerAvailable();

    /**
     * Gets the state of the left wheel overcurrent sensor. <p> <b>NOTE:</b>
     * This method returns a locally stored value previously read from the
     * Create. It is the client's responsibility to read the sensor values from
     * the Create prior to calling this method in order to ensure fresh values.
     *
     * @return true if too much current (> 1A) is requested.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isLeftWheelOvercurrent();

    /**
     * Gets the state of the Low Side Driver 0 overcurrent sensor. <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return true if too much current (> 0.5A) is requested.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isLowSideDriver0Overcurrent();

    /**
     * Gets the state of the Low Side Driver 1 overcurrent sensor. <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return true if too much current (> 0.5A) is requested.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isLowSideDriver1Overcurrent();

    /**
     * Gets the state of the Low Side Driver 2 overcurrent sensor. <p>
     * <b>NOTE:</b> This method returns a locally stored value previously read
     * from the Create. It is the client's responsibility to read the sensor
     * values from the Create prior to calling this method in order to ensure
     * fresh values.
     *
     * @return true if too much current (> 1.6A) is requested.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isLowSideDriver2Overcurrent();

    /**
     * Gets the state of the Play button <p> <b>NOTE:</b> This method returns a
     * locally stored value previously read from the Create. It is the client's
     * responsibility to read the sensor values from the Create prior to calling
     * this method in order to ensure fresh values.
     *
     * @return true if the Play button is depressed.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isPlayButtonDown();

    /**
     * Gets the state of the right wheel overcurrent sensor. <p> <b>NOTE:</b>
     * This method returns a locally stored value previously read from the
     * Create. It is the client's responsibility to read the sensor values from
     * the Create prior to calling this method in order to ensure fresh values.
     *
     * @return true if too much current (> 1A) is requested.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isRightWheelOvercurrent();

    /**
     * Gets the state of the OI song player.
     *
     * <p> <b>NOTE:</b> This method returns a locally stored value previously
     * read from the Create. It is the client's responsibility to read the
     * sensor values from the Create prior to calling this method in order to
     * ensure fresh values.
     *
     * @return true if the Create is playing a song.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     * @see #playSong(int)
     */
    public boolean isSongPlaying();

    /**
     * Gets the state of the virtual wall sensor. <p> <b>NOTE:</b> This method
     * returns a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return true if virtual wall is detected.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isVirtualWall();

    /**
     * Gets the state of the wall sensor. <p> <b>NOTE:</b> This method returns a
     * locally stored value previously read from the Create. It is the client's
     * responsibility to read the sensor values from the Create prior to calling
     * this method in order to ensure fresh values.
     *
     * @return true if the wall is detected.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isWall();

    /**
     * Gets the state of the caster wheel sensor <p> <b>NOTE:</b> This method
     * returns a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return true if the caster wheel has dropped.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isWheelDropCaster();

    /**
     * Gets the state of the left wheel sensor <p> <b>NOTE:</b> This method
     * returns a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return true if the left wheel has dropped.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isWheelDropLeft();

    /**
     * Gets the state of the right wheel sensor <p> <b>NOTE:</b> This method
     * returns a locally stored value previously read from the Create. It is the
     * client's responsibility to read the sensor values from the Create prior
     * to calling this method in order to ensure fresh values.
     *
     * @return true if the right wheel has dropped.
     * @see #readSensors(int sensorId)
     * @see #readSensors(int[] sensorIds)
     */
    public boolean isWheelDropRight();

    /**
     * This method controls the LEDs on the Create. The state of the Power, Play
     * and Advance LEDs is specified by true or false. Power LED on will use
     * full green to match other LEDs. Available in modes: Safe or Full <br>
     *
     * @param powerLedOn when true turns Power LED on
     * @param playLedOn when true turns Play LED on
     * @param advanceLedOn when true turns Advance LED on
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void leds(boolean powerLedOn, boolean playLedOn, boolean advanceLedOn)
            throws ConnectionLostException;

    /**
     * This method controls the LEDs on the Create. The state of the Play and
     * Advance LEDs is specified by true or false. The Power LED is specified by
     * two values: one for the color and the other for the intensity. Available
     * in modes: Safe or Full <p> Advance and Play LEDs use green LEDs. false =
     * off, true = on <br> Power uses a bicolor (red/green) LED. The intensity
     * and color of this LED can be controlled with 8-bit resolution.
     *
     * @param powerColor Power LED Color (0 - 255) 0 = green, 255 = red.
     * Intermediate values are intermediate colors (orange, yellow, etc).
     * @param powerIntensity Power LED Intensity (0 - 255) 0 = off, 255 = full
     * intensity. Intermediate values are intermediate intensities.
     * @param playLedOn when true turn Play LED on.
     * @param advanceLedOn when true turn Advance LED on.
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void leds(int powerColor, int powerIntensity, boolean playLedOn, boolean advanceLedOn)
            throws ConnectionLostException;

    /**
     * Toggles the Power, Play and Advance LEDs.
     *
     * @param togglePower if true toggles the intensity of the Power LED
     * @param togglePlay if true toggles the on-off state of the Play LED
     * @param toggleAdvance if true toggles the on-off state of the Advance LED
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void ledsToggle(boolean togglePower, boolean togglePlay, boolean toggleAdvance)
            throws ConnectionLostException;

    /**
     * This method lets you control the three low side drivers. The state of
     * each driver is specified by true or false. Low side drivers 0 and 1 can
     * provide up to 0.5A of current. Low side driver 2 can provide up to 1.5 A
     * of current. If too much current is requested, the current is limited and
     * the overcurrent flag is set (sensor packet 14). Available in modes: Safe
     * or Full <br>
     *
     * @param lowSideDriver0On true turns Low Side Driver 0 on.
     * @param lowSideDriver1On true turns Low Side Driver 1 on.
     * @param lowSideDriver2On true turns Side Driver 2 on.
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void lowSideDrivers(boolean lowSideDriver0On, boolean lowSideDriver1On, boolean lowSideDriver2On)
            throws ConnectionLostException;

    /**
     * Stops or restarts the steam without clearing the list of requested
     * packets. Available in modes: Passive, Safe, or Full
     *
     * @param pause true pauses the sensor stream, false resumes the sensor
     * stream
     * @see #startSensorStream(int[])
     */
//    public void pauseResumeSensorStream(boolean pause);
    /**
     * This command loads a script into the serial input queue for playback.
     * Available in modes: Passive, Safe, or Full.
     *
     * @param script a byte array of commands and their respective arguments.
     * @param immediateReturn true if this method returns immediately, false if
     * this method is blocked while the Create is executing the script.
     * @throws ConnectionLostException
     * @throws InterruptedException
     * @see <a href=../../../../resources/Create%20Open%20Interface_v2.pdf>
     * Create Open Interface_v2.pdf</a>
     */
    public void playScript(byte[] script, boolean immediateReturn) throws ConnectionLostException;

    /**
     * Selects a song to play from the songs previously added to the Create
     * using one of the song() methods. This method does nothing if a song is
     * already playing. Note that the {@link #isSongPlaying()
     * isSongPlaying()} method can be used to check whether the Create is ready
     * to accept this command. Available in modes: Safe or Full.
     *
     * @param songNumber the number of the song the Create is to play.
     * @throws ConnectionLostException
     * @throws InterruptedException
     * @see #song(int, int[])
     * @see #song(int, int[], int, int)
     */
    public void playSong(int songNumber) throws ConnectionLostException;

    /**
     * Controls the three low side drivers with variable power. You specify the
     * PWM duty cycle for the low side driver (max 128). For example, if you
     * want to control a driver with 25% of battery voltage, choose a duty cycle
     * of 128 * 25% = 32. Available in modes: Safe or Full <br>
     *
     * @param lowSideDriver0DutyCycle Duty cycle for low side driver 0 (0 - 128)
     * @param lowSideDriver1DutyCycle Duty cycle for low side driver 1 (0 - 128)
     * @param lowSideDriver2DutyCycle Duty cycle for low side driver 2 (0 - 128)
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void pwmLowSideDrivers(int lowSideDriver0DutyCycle, int lowSideDriver1DutyCycle, int lowSideDriver2DutyCycle)
            throws ConnectionLostException;

    /**
     * Retrieves one or more sensor values from the Create and stores the values
     * locally. The values can later be accessed using any of the access
     * methods. The sensors that are to be read are specified using a sensor id.
     * There are 43 different sensor Ids; see the SENSORS_* constants. Each
     * identifies a specific sensor or group of sensors.
     *
     * @param sensorId One of the SENSORS_* constants
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void readSensors(int sensorId) throws ConnectionLostException;

    /**
     * Retrieves one or more sensor values from the Create and stores the values
     * locally. The values can later be accessed using any of the access
     * methods. The sensors that are to be read are specified using a sensor id.
     * There are 43 different sensor Ids; see the SENSORS_* constants. Each
     * sensor Id identifies a specific sensor or group of sensors.
     *
     * @param sensorIds An array of the SENSORS_* constants.
     * @throws IllegalArgumentException if the array of sensorIds is too long.
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void readSensors(int[] sensorIds)
            throws IllegalArgumentException, ConnectionLostException;

    /**
     * Puts the OI into Safe mode, enabling user control of the Create. It turns
     * off all LEDs. The OI can be in Passive, Safe, or Full mode to accept this
     * command.
     *
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void safe() throws ConnectionLostException;

    /**
     * This method sends the requested byte out of the Low Side Driver 1 (pin 23
     * on the Cargo Bay Connector), using the format expected by iRobot Create's
     * IR receiver. Available in modes: Safe or Full
     *
     * @param irValue A value to send (0 - 255)
     * @throws ConnectionLostException
     * @throws InterruptedException
     * @see #getInfraredByte()
     */
    public void sendIr(int irValue) throws ConnectionLostException;

    /**
     * Controls the state of the 3 digital output pins on the 25 pin Cargo Bay
     * Connector. The digital outputs can provide up to 20 mA of current.
     *
     * @param pin0High if true sets the output of the first digital output to
     * high
     * @param pin1High if true sets the output of the second digital output to
     * high
     * @param pin2High if true sets the output of the third digital output to
     * high
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void setDigitalOutputs(boolean pin0High, boolean pin1High, boolean pin2High)
            throws ConnectionLostException;

    /**
     * Returns a previously stored script as an int array containing the
     * script's commands and data bytes.
     *
     * @return an int array containing the script.
     * @throws ConnectionLostException
     * @throws InterruptedException
     * @see #playScript(byte[])
     * @see <a href=../../../../Create%20Open%20Interface_v2.pdf> Create Open
     * Interface_v2.pdf</a>
     *
     */
    public int[] showScript() throws ConnectionLostException;

    /**
     * This method lets you specify a song that you can play at a later time. Up
     * to 16 songs can be specified. Each song is associated with a song number.
     * Each song can contain up to sixteen notes. Each note is associated with a
     * note number (in the range of (31 - 127) that uses MIDI note definitions
     * and a duration that is specified in fractions of a second. The Create
     * considers all musical notes outside the range of (31 - 127) as rest
     * notes, and will make no sound during the duration of those notes.
     * Available in modes: Passive, Safe, or Full <br>
     *
     * @param songNumber The song number associated with the specific song. Must
     * be in the range 0 through 15. If you send a second Song command, using
     * the same song number, the old song is overwritten.
     * @param notesAndDurations An int array of even length. Every even index
     * represents a note, every odd index is the duration of the preceding note
     * in units of 1/64 second. Example: a half-second long musical note has a
     * duration value of 32. Each entry in the array must be a number between 0
     * and 255 and the total length of the array must be 32 or less (max 16
     * notes).
     * @throws ConnectionLostException
     * @throws InterruptedException
     *
     * @see #playSong(int)
     * @see #song(int, int[])
     * @see #song(int, int[], int, int)
     * @see <a href=../../../../resources/Create%20Open%20Interface_v2.pdf>
     * Create Open Interface_v2.pdf</a>
     */
    public void song(int songNumber, int[] notesAndDurations)
            throws ConnectionLostException;

    /**
     * This method lets you specify a song that you can play at a later time. Up
     * to 16 songs can be specified. Each song is associated with a song number.
     * Each song can contain up to sixteen notes. Each note is associated with a
     * note number (in the range of (31 - 127) that uses MIDI note definitions
     * and a duration that is specified in fractions of a second. The Create
     * considers all musical notes outside the range of (31 - 127) as rest
     * notes, and will make no sound during the duration of those notes.
     * Available in modes: Passive, Safe, or Full <br>
     *
     * @param songNumber The song number associated with the specific song. Must
     * be in the range 0 through 15. On a second invocation of the song()
     * method, using the same song number, the old song is overwritten.
     * @param notesAndDurations An int array of even length. Every even index
     * represents a note, every odd index is the duration of the preceding note
     * in units of 1/64 second. Example: a half-second long musical note has a
     * duration value of 32. Each entry in the array must be a number between 0
     * and 255.
     * @param startIndex an index into an int array specifying the first note
     * @param length an even number less than or equal to 32 (max 16 notes).
     * @throws ConnectionLostException
     * @throws InterruptedException
     * @see #playSong(int)
     * @see #song(int, int[])
     * @see <a href=../../../../Create%20Open%20Interface_v2.pdf> Create Open
     * Interface_v2.pdf</a>
     *
     */
    public void song(int songNumber, int[] notesAndDurations, int startIndex, int length)
            throws ConnectionLostException;

    /**
     * Commands the Create to transmit the sensor values for a specified list of
     * sensors every 15 ms, which is the rate iRobot Create uses to update data.
     * <p> This is the best method of requesting sensor data if you are
     * controlling the Create over a wireless network (which has poor real-time
     * characteristics) with software running on a remote computer.
     *
     * @param sensorIds an array of sensor ids as specified by the SENSOR_*
     * constants.
     * @see #pauseResumeSensorStream(boolean)
     */
//    public void startSensorStream(int[] sensorIds);
    /**
     * Pause Java execution until either the play or the advance button is
     * pressed. While waiting, blink the LED next to the desired button.
     *
     * @param playButton If true wait for the play button, otherwise wait for
     * the advance button
     * @param beep
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public void waitButtonPressed(boolean playButton, boolean beep)
            throws ConnectionLostException;

    /**
     * Closes the serial connection to the Create
     */
    public void closeConnection();
}

