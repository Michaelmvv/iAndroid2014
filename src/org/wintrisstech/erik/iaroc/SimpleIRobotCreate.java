package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * A final class that provides a high level interface to the iRobot series of
 * Roomba/Create robots.
 *
 * <p>The names of most of the readSensors are derived mostly from the <a
 * href=../../../../Create%20Open%20Interface_v2.pdf> Create Open
 * Interface_v2.pdf</a>. Recommend reading this document in order to get a
 * better understanding of how to work with the robot. <p> Note: The default
 * constructor will not return until it has managed to connect with the Create
 * via the default serial interface from the IOIO to the create. This means that
 * once you have received the instance, you are ok to control the robot.
 *
 * <p> <b>NOTE:</b> This class is final; it cannot be extended. Extend
 * {@link IRobotCreateAdapter IRobotCreateAdapter} instead.
 */
public final class SimpleIRobotCreate implements IRobotCreateInterface {

    private static final String TAG = "SimpleIRobotCreate";
//    @deprecated use COMMAND_MODE_SAFE instead.
//    private static final int COMMAND_MODE_CONTROL = 130;
    /**
     * This command puts the OI into Safe mode, enabling user control of Create.
     * It turns off all LEDs. The OI can be in Passive, Safe, or Full mode to
     * accept this command.
     *
     * @see #safe()
     */
    private static final int COMMAND_MODE_SAFE = 131;
    /**
     * This command gives you complete control over Create by putting the OI
     * into Full mode, and turning off the cliff, wheel-drop and internal
     * charger safety features. That is, in Full mode, Create executes any
     * command that you send it, even if the internal charger is plugged in, or
     * the robot senses a cliff or wheel drop.
     *
     * @see #full()
     *
     */
    private static final int COMMAND_MODE_FULL = 132;
    /**
     * This command starts the requested built-in demo. Demo can be specified
     * using one of the DEMO_* constants.
     *
     * @see #demo(int)
     */
    private static final int COMMAND_DEMO = 136;
    /**
     * This command controls Create's drive wheels. It takes four data bytes,
     * interpreted as two 16-bit signed values using two's complement. The first
     * two bytes specify the average velocity of the drive wheels in millimeters
     * per second (mm/s), with the high byte being sent first. The next two
     * bytes specify the radius in millimeters at which Create will turn. The
     * longer radii make Create drive straighter, while the shorter radii make
     * Create turn more. The radius is measured from the center of the turning
     * circle to the center of Create. A Drive command with a positive velocity
     * and a positive radius makes Create drive forward while turning toward the
     * left. A negative radius makes Create turn toward the right. Special cases
     * for the radius make Create turn in place or drive straight, as specified
     * below. A negative velocity makes Create drive backward. <br> NOTE:
     * Internal and environmental restrictions may prevent Create from
     * accurately carrying out some drive commands. For example, it may not be
     * possible for Create to drive at full speed in an arc with a large radius
     * of curvature. <br> + Available in modes: Safe or Full <br> + Changes mode
     * to: No Change <br> + Velocity (-500 - 500 mm/s) <br> + Radius (-2000 -
     * 2000 mm) <br> Special cases: <br> Straight = 32768 or 32767 = hex 8000 or
     * 7FFF <br> Turn in place clockwise = hex FFFF <br> Turn in place
     * counter-clockwise = hex 0001
     *
     * @see #drive(int, int)
     */
    private static final int COMMAND_DRIVE = 137;
    /**
     * This command lets you control the three low side drivers. The state of
     * each driver is specified by one bit in the data byte. Low side drivers 0
     * and 1 can provide up to 0.5A of current. Low side driver 2 can provide up
     * to 1.5 A of current. If too much current is requested, the current is
     * limited and the overcurrent flag is set (sensor packet 14). <p> The bits
     * in the data byte are mapped to the drivers as follows (0 = off, 1 = on at
     * 100% PWM duty cycle): <p> bit 0 - Low Side Driver 0 (pin 23) <br> bit 1 -
     * Low Side Driver 1 (pin 22) <br> bit 2 - Side Driver (pin 24)
     *
     * @see #lowSideDrivers(boolean, boolean, boolean)
     */
    private static final int COMMAND_LOW_SIDE_DRIVERS = 138;
    /**
     * Mask identifying the Low Side Driver 0 bit
     */
    private static final int LOW_SIDE_DRIVER0 = 1;
    /**
     * Mask identifying the Low Side Driver 1.
     */
    private static final int LOW_SIDE_DRIVER1 = 2;
    /**
     * Mask identifying the Low Side Driver 2.
     */
    private static final int LOW_SIDE_DRIVER2 = 4;
    /**
     * This command controls the LEDs on Create. The state of the Play and
     * Advance LEDs is specified by two bits in the first data byte. The power
     * LED is specified by two data bytes: one for the color and the other for
     * the intensity. <p> This command shall be followed by 3 data bytes: <p> +
     * LED bits, identifies the LEDS (bit 1 - Play, bit 3 - Advance) <br> +
     * Power Color, (0 - 255) 0 = green, 255 = red. Intermediate values are
     * intermediate colors (orange, yellow, etc). <br> + Power Intensity, (0 -
     * 255) 0 = off, 255 = full intensity. Intermediate values are intermediate
     * intensities.
     *
     * @see #leds(int, int, boolean, boolean)
     *
     */
    private static final int COMMAND_LEDS = 139;
    /**
     * Mask identifying the Play LED.
     */
    private static final int LEDS_PLAY = 2;
    /**
     * Mask identifying the Advance LED.
     */
    private static final int LEDS_ADVANCE = 8;
    /**
     * This command lets you specify up to sixteen songs to the OI that you can
     * play at a later time. Each song is associated with a song number. The
     * Play command uses the song number to identify your song selection. Each
     * song can contain up to sixteen notes. Each note is associated with a note
     * number that uses MIDI note definitions and a duration that is specified
     * in fractions of a second. The number of data bytes varies, depending on
     * the length of the song specified. A one note song is specified by four
     * data bytes. For each additional note within a song, add two data bytes.
     *
     * @see #song(int, int[])
     * @see #song(int, int[], int, int)
     */
    private static final int COMMAND_SONG = 140;
    /**
     * This command lets you select a song to play from the songs added to
     * iRobot Create using the Song command. You must add one or more songs to
     * Create using the Song command in order for the Play command to work.
     * Also, this command does not work if a song is already playing. Wait until
     * a currently playing song is done before sending this command. Note that
     * {@link IRobotCreateInterface#SENSORS_SONG_PLAYING} can be used to check
     * whether the Create is ready to accept this command.
     *
     * @see #playSong(int)
     */
    private static final int COMMAND_PLAY_SONG = 141;
    /**
     * This command requests the OI to send a packet of sensor data bytes. There
     * are 43 different sensor data packets. Each provides a value of a specific
     * sensor or group of sensors.
     *
     * @see #readSensors(int)
     */
    private static final int COMMAND_SENSORS = 142;
    /**
     * This command lets you control the three low side drivers with variable
     * power. With each data byte, you specify the PWM duty cycle for the low
     * side driver (max 128). For example, if you want to control a driver with
     * 25% of battery voltage, choose a duty cycle of 128 * 25% = 32.
     *
     * @see pwmLowSideDrivers(int lowSideDriver0DutyCycle, int
     * lowSideDriver1DutyCycle, int lowSideDriver2DutyCycle)
     */
    private static final int COMMAND_PWM_LOW_SIDE_DRIVERS = 144;
    /**
     * This command lets you control the forward and backward motion of Create's
     * drive wheels independently. It takes four data bytes, which are
     * interpreted as two 16-bit signed values using two's complement. The first
     * two bytes specify the velocity of the right wheel in millimeters per
     * second (mm/s), with the high byte sent first. The next two bytes specify
     * the velocity of the left wheel, in the same format. A positive velocity
     * makes that wheel drive forward, while a negative velocity makes it drive
     * backward. <br> + Available in modes: Safe or Full <br> + Changes mode to:
     * No Change <br> + Right wheel velocity (-500 - 500 mm/s) <br> + Left wheel
     * velocity (-500 - 500 mm/s)
     *
     * @see driveDirect(int rightVelocity, int leftVelocity)
     */
    private static final int COMMAND_DRIVE_DIRECT = 145;
    /**
     * This command controls the state of the 3 digital output pins on the 25
     * pin Cargo Bay Connector. The digital outputs can provide up to 20 mA of
     * current.
     */
    private static final int COMMAND_DIGITAL_OUTPUTS = 147;
    /**
     * This command starts a continuous startSensorStream of data packets. The
     * list of packets requested is sent every 15 ms, which is the rate iRobot
     * Create uses to update data. This is the best method of requesting sensor
     * data if you are controlling Create over a wireless network (which has
     * poor real-time characteristics) with software running on a desktop
     * computer. <p> Not supported
     */
//    private static final int COMMAND_STREAM = 148;
    /*
     * This command lets you ask for a list of sensor packets. The result is
     * returned once, as in the Sensors command. The robot returns the packets
     * in the order you specify.
     *
     * @see readSensors(int[] packetIds)
     */
    private static final int COMMAND_QUERY_LIST = 149;
    /**
     * This command lets you stop and restart the steam without clearing the
     * list of requested packets. <p> Not supported
     */
//    private static final int COMMAND_PAUSE_RESUME_STREAM = 150;
    /**
     * This command sends the requested byte out of low side driver 1 (pin 23 on
     * the Cargo Bay Connector), using the format expected by iRobot Create�s IR
     * receiver. You must use a preload resistor (suggested value: 100 ohms) in
     * parallel with the IR LED and its resistor in order turn it on.
     *
     * @see sendIr(int byteValue)
     */
    private static final int COMMAND_SEND_IR = 151;
    private static final int COMMAND_SCRIPT = 152;
    private static final int COMMAND_PLAY_SCRIPT = 153;
    private static final int COMMAND_SHOW_SCRIPT = 154;

    /*
     * Time in ms to pause after sending a command to the Create.
     */
    private static final int AFTER_COMMAND_PAUSE_TIME = 20;
    private static final int DIGITAL_OUTPUT_PIN0 = 1;
    private static final int DIGITAL_OUTPUT_PIN1 = 2;
    private static final int DIGITAL_OUTPUT_PIN2 = 4;
    private static final int OI_MODE_PASSIVE = 1;
    //Indices to an array containing the sensor values.
    private static final int SENSORID_advanceButton = 0;
    private static final int SENSORID_angle = 1;
    private static final int SENSORID_batteryCapacity = 2;
    private static final int SENSORID_batteryCharge = 3;
    private static final int SENSORID_batteryTemperature = 4;
    private static final int SENSORID_bumpLeft = 5;
    private static final int SENSORID_bumpRight = 6;
    private static final int SENSORID_cargoBayAnalogSignal = 7;
    private static final int SENSORID_cargoBayDeviceDetectBaudRateChange = 8;
    private static final int SENSORID_cargoBayDigitalInput0 = 9;
    private static final int SENSORID_cargoBayDigitalInput1 = 10;
    private static final int SENSORID_cargoBayDigitalInput2 = 11;
    private static final int SENSORID_cargoBayDigitalInput3 = 12;
    private static final int SENSORID_chargingState = 13;
    private static final int SENSORID_cliffFrontLeft = 14;
    private static final int SENSORID_cliffFrontLeftSignal = 15;
    private static final int SENSORID_cliffFrontRight = 16;
    private static final int SENSORID_cliffFrontRightSignal = 17;
    private static final int SENSORID_cliffLeft = 18;
    private static final int SENSORID_cliffLeftSignal = 19;
    private static final int SENSORID_cliffRight = 20;
    private static final int SENSORID_cliffRightSignal = 21;
    private static final int SENSORID_current = 22;
    private static final int SENSORID_distance = 23;
    private static final int SENSORID_homeBaseChargerAvailable = 24;
    private static final int SENSORID_infraredByte = 25;
    private static final int SENSORID_internalChargerAvailable = 26;
    private static final int SENSORID_leftWheelOvercurrent = 27;
    private static final int SENSORID_lowSideDriver0Overcurrent = 28;
    private static final int SENSORID_lowSideDriver1Overcurrent = 29;
    private static final int SENSORID_lowSideDriver2Overcurrent = 30;
    private static final int SENSORID_numberOfStreamPackets = 31;
    private static final int SENSORID_oiMode = 32;
    private static final int SENSORID_playButton = 33;
    private static final int SENSORID_requestedLeftVelocity = 34;
    private static final int SENSORID_requestedRadius = 35;
    private static final int SENSORID_requestedRightVelocity = 36;
    private static final int SENSORID_requestedVelocity = 37;
    private static final int SENSORID_rightWheelOvercurrent = 38;
    private static final int SENSORID_songNumber = 39;
    private static final int SENSORID_songPlaying = 40;
    private static final int SENSORID_virtualWall = 41;
    private static final int SENSORID_voltage = 42;
    private static final int SENSORID_wall = 43;
    private static final int SENSORID_wallSignal = 44;
    private static final int SENSORID_wheelDropCaster = 45;
    private static final int SENSORID_wheelDropLeft = 46;
    private static final int SENSORID_wheelDropRight = 47;
    private static final int SENSORID_MAX = 48;
    private final int[] sensorValues = new int[SENSORID_MAX];
    private SerialConnection serialConnection;
    private int powerLedColor;
    private int powerLedIntensity;
    private boolean isPlayLedOn;
    private boolean isAdvanceLedOn;

    /**
     * Constructor that uses the IOIO instance to communicate with the iRobot
     * Create. Equivalent to using
     * <code>SimpleIRobotCreate(ioio, false, true, true)</code>
     *
     * @param ioio The IOIO instance used to communicate with the Create
     *
     * @throws ConnectionLostException
     * @throws InterruptedException
     * @see #SimpleIRobotCreate(boolean, boolean, boolean)
     */
    public SimpleIRobotCreate(IOIO ioio)
            throws ConnectionLostException {
        this(ioio, false, true, true);
    }

    /**
     * Constructor that uses the IOIO instance to communicate with the iRobot
     * Create.
     *
     * @param ioio The IOIO instance used to communicate with the Create
     * @param debugSerial if true will create a default serial connection with
     * debug true
     * @param fullMode if true enter full mode, otherwise enter safe mode
     * @param waitButton if true wait until play button is pressed
     * @throws ConnectionLostException
     * @throws InterruptedException
     */
    public SimpleIRobotCreate(IOIO ioio, boolean debugSerial, boolean fullMode, boolean waitButton)
            throws ConnectionLostException {
        this(SerialConnection.getInstance(ioio, debugSerial), fullMode, waitButton);
    }

    /**
     * Constructor that uses a given serial connection as its means of sending
     * and reading data to and from the Create.
     *
     * @param serialConnection user-specified serial connection.
     * @param fullMode if true enter full mode, otherwise enter safe mode
     * @param waitButton if true wait until play button is pressed
     * @throws ConnectionLostException
     */
    SimpleIRobotCreate(SerialConnection sc, boolean fullMode, boolean waitButton)
            throws ConnectionLostException {
        this.serialConnection = sc;
        if (fullMode) {
            full();
        } else {
            safe();
        }
        if (waitButton) {
            waitButtonPressed(true, true);
        }
    }

    public synchronized void demo(int demoType)
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_DEMO);
        serialConnection.writeByte(demoType);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void setDigitalOutputs(boolean pin0High, boolean pin1High, boolean pin2High)
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_DIGITAL_OUTPUTS);
        serialConnection.writeByte(
                (pin0High ? DIGITAL_OUTPUT_PIN0 : 0)
                | (pin1High ? DIGITAL_OUTPUT_PIN1 : 0)
                | (pin2High ? DIGITAL_OUTPUT_PIN2 : 0));
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void drive(int velocity, int radius)
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_DRIVE);
        serialConnection.writeSignedWord(velocity);
        serialConnection.writeSignedWord(radius);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void driveDirect(int rightVelocity, int leftVelocity)
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_DRIVE_DIRECT);
        serialConnection.writeSignedWord(rightVelocity);
        serialConnection.writeSignedWord(leftVelocity);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void full() throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_MODE_FULL);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized int getAngle() {
        return getSensorInteger(SENSORID_angle);
    }

    public synchronized int getBatteryCapacity() {
        return getSensorInteger(SENSORID_batteryCapacity);
    }

    public synchronized int getBatteryCharge() {
        return getSensorInteger(SENSORID_batteryCharge);
    }

    public synchronized int getBatteryTemperature() {
        return getSensorInteger(SENSORID_batteryTemperature);
    }

    public synchronized int getCargoBayAnalogSignal() {
        return getSensorInteger(SENSORID_cargoBayAnalogSignal);
    }

    public synchronized int getChargingState() {
        return getSensorInteger(SENSORID_chargingState);
    }

    public synchronized int getCliffFrontLeftSignal() {
        return getSensorInteger(SENSORID_cliffFrontLeftSignal);
    }

    public synchronized int getCliffFrontRightSignal() {
        return getSensorInteger(SENSORID_cliffFrontRightSignal);
    }

    public synchronized int getCliffLeftSignal() {
        return getSensorInteger(SENSORID_cliffLeftSignal);
    }

    public synchronized int getCliffRightSignal() {
        return getSensorInteger(SENSORID_cliffRightSignal);
    }

    public synchronized int getCurrent() {
        return getSensorInteger(SENSORID_current);
    }

    public synchronized int getDistance() {
        return getSensorInteger(SENSORID_distance);
    }

    public synchronized int getInfraredByte() {
        return getSensorInteger(SENSORID_infraredByte);
    }

    public synchronized int getOiMode() {
        return getSensorInteger(SENSORID_oiMode);
    }

    public synchronized int getRequestedLeftVelocity() {
        return getSensorInteger(SENSORID_requestedLeftVelocity);
    }

    public synchronized int getRequestedRadius() {
        return getSensorInteger(SENSORID_requestedRadius);
    }

    public synchronized int getRequestedRightVelocity() {
        return getSensorInteger(SENSORID_requestedRightVelocity);
    }

    public synchronized int getRequestedVelocity() {
        return getSensorInteger(SENSORID_requestedVelocity);
    }

    private boolean getSensorBoolean(int sensorId) {
        return sensorValues[sensorId] != 0;
    }

    private int getSensorInteger(int sensorId) {
        return sensorValues[sensorId];
    }

    public synchronized int getSongNumber() {
        return getSensorInteger(SENSORID_songNumber);
    }

    public synchronized int getVoltage() {
        return getSensorInteger(SENSORID_voltage);
    }

    public synchronized int getWallSignal() {
        return getSensorInteger(SENSORID_wallSignal);
    }

    public synchronized boolean isAdvanceButtonDown() {
        return getSensorBoolean(SENSORID_advanceButton);
    }

    public synchronized boolean isBumpLeft() {
        return getSensorBoolean(SENSORID_bumpLeft);
    }

    public synchronized boolean isBumpRight() {
        return getSensorBoolean(SENSORID_bumpRight);
    }

    public synchronized boolean isCargoBayDeviceDetectBaudRateChangeHigh() {
        return getSensorBoolean(SENSORID_cargoBayDeviceDetectBaudRateChange);
    }

    public synchronized boolean isCargoBayDigitalInput0High() {
        return getSensorBoolean(SENSORID_cargoBayDigitalInput0);
    }

    public synchronized boolean isCargoBayDigitalInput1High() {
        return getSensorBoolean(SENSORID_cargoBayDigitalInput1);
    }

    public synchronized boolean isCargoBayDigitalInput2High() {
        return getSensorBoolean(SENSORID_cargoBayDigitalInput2);
    }

    public synchronized boolean isCargoBayDigitalInput3High() {
        return getSensorBoolean(SENSORID_cargoBayDigitalInput3);
    }

    public synchronized boolean isCliffFrontLeft() {
        return getSensorBoolean(SENSORID_cliffFrontLeft);
    }

    public synchronized boolean isCliffFrontRight() {
        return getSensorBoolean(SENSORID_cliffFrontRight);
    }

    public synchronized boolean isCliffLeft() {
        return getSensorBoolean(SENSORID_cliffLeft);
    }

    public synchronized boolean isCliffRight() {
        return getSensorBoolean(SENSORID_cliffRight);
    }

    public synchronized boolean isHomeBaseChargerAvailable() {
        return getSensorBoolean(SENSORID_homeBaseChargerAvailable);
    }

    public synchronized boolean isInternalChargerAvailable() {
        return getSensorBoolean(SENSORID_internalChargerAvailable);
    }

    public synchronized boolean isLeftWheelOvercurrent() {
        return getSensorBoolean(SENSORID_leftWheelOvercurrent);
    }

    public synchronized boolean isLowSideDriver0Overcurrent() {
        return getSensorBoolean(SENSORID_lowSideDriver0Overcurrent);
    }

    public synchronized boolean isLowSideDriver1Overcurrent() {
        return getSensorBoolean(SENSORID_lowSideDriver1Overcurrent);
    }

    public synchronized boolean isLowSideDriver2Overcurrent() {
        return getSensorBoolean(SENSORID_lowSideDriver2Overcurrent);
    }

    public synchronized boolean isPlayButtonDown() {
        return getSensorBoolean(SENSORID_playButton);
    }

    public synchronized boolean isRightWheelOvercurrent() {
        return getSensorBoolean(SENSORID_rightWheelOvercurrent);
    }

    public synchronized boolean isSongPlaying() {
        return getSensorBoolean(SENSORID_songPlaying);
    }

    public synchronized boolean isVirtualWall() {
        return getSensorBoolean(SENSORID_virtualWall);
    }

    public synchronized boolean isWall() {
        return getSensorBoolean(SENSORID_wall);
    }

    public synchronized boolean isWheelDropCaster() {
        return getSensorBoolean(SENSORID_wheelDropCaster);
    }

    public synchronized boolean isWheelDropLeft() {
        return getSensorBoolean(SENSORID_wheelDropLeft);
    }

    public synchronized boolean isWheelDropRight() {
        return getSensorBoolean(SENSORID_wheelDropRight);
    }

    public synchronized void leds(boolean powerLedOn, boolean playLedOn, boolean advanceLedOn) throws ConnectionLostException {
        leds(0, powerLedOn ? 255 : 0, playLedOn, advanceLedOn);
    }

    public synchronized void leds(int powerColor, int powerIntensity, boolean playLedOn, boolean advanceLedOn) throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_LEDS);
        serialConnection.writeByte((advanceLedOn ? LEDS_ADVANCE : 0) | (playLedOn ? LEDS_PLAY : 0));
        serialConnection.writeByte(powerColor);
        serialConnection.writeByte(powerIntensity);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
        powerLedColor = powerColor;
        powerLedIntensity = powerIntensity;
        isPlayLedOn = playLedOn;
        isAdvanceLedOn = advanceLedOn;
    }

    public synchronized void ledsToggle(boolean togglePower, boolean togglePlay, boolean toggleAdvance)
            throws ConnectionLostException {
        if (togglePower) {
            powerLedIntensity = powerLedIntensity ^ 0xFF;
        }
        if (togglePlay) {
            isPlayLedOn = !isPlayLedOn;
        }
        if (toggleAdvance) {
            isAdvanceLedOn = !isAdvanceLedOn;
        }
        leds(powerLedColor, powerLedIntensity, isPlayLedOn, isAdvanceLedOn);
    }

    public synchronized void lowSideDrivers(boolean lowSideDriver0On, boolean lowSideDriver1On, boolean lowSideDriver2On)
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_LOW_SIDE_DRIVERS);
        serialConnection.writeByte(
                (lowSideDriver0On ? LOW_SIDE_DRIVER0 : 0)
                | (lowSideDriver1On ? LOW_SIDE_DRIVER1 : 0)
                | (lowSideDriver2On ? LOW_SIDE_DRIVER2 : 0));
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void playScript(byte[] script, boolean immediateReturn)
            throws ConnectionLostException {
        int count = script.length;
        serialConnection.writeByte(COMMAND_SCRIPT);
        serialConnection.writeByte(count);
        serialConnection.writeBytes(script, 0, count);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
        serialConnection.writeByte(COMMAND_PLAY_SCRIPT);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
        if (!immediateReturn) {
            readSensors(SENSORS_GROUP_ID2);
        }
    }

    public synchronized void playSong(int songNumber)
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_PLAY_SONG);
        serialConnection.writeByte(songNumber);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void pwmLowSideDrivers(int lowSideDriver0DutyCycle, int lowSideDriver1DutyCycle, int lowSideDriver2DutyCycle)
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_PWM_LOW_SIDE_DRIVERS);
        serialConnection.writeByte(lowSideDriver2DutyCycle);
        serialConnection.writeByte(lowSideDriver1DutyCycle);
        serialConnection.writeByte(lowSideDriver0DutyCycle);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void readSensors(int[] sensorIds)
            throws ConnectionLostException {
        if (sensorIds.length > serialConnection.getMaxCommandSize() - 2) {
            throw new IllegalArgumentException("Argument contains too many bytes.");
        }
        serialConnection.writeByte(COMMAND_QUERY_LIST);
        serialConnection.writeByte(sensorIds.length);
        serialConnection.writeBytes(sensorIds, 0, sensorIds.length);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
        for (int packetId = 0; packetId < sensorIds.length; packetId++) {
            readSensorData(packetId);
        }
    }

    public synchronized void readSensors(int sensorId)
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_SENSORS);
        serialConnection.writeByte(sensorId);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
        readSensorData(sensorId);
    }

    private void readSensorData(int sensorId) throws ConnectionLostException {
        if (SENSORS_GROUP_ID0 <= sensorId && sensorId <= SENSORS_GROUP_ID6) {
            int[] group = SENSOR_GROUPS[sensorId];
            for (int sensor = 0; sensor < group.length; sensor++) {
                readSensorDataPrim(group[sensor]);
            }
        } else {
            readSensorDataPrim(sensorId);
        }
    }

    private void readSensorDataPrim(int sensorId) throws ConnectionLostException {
        int dataByte, dataWord;

        switch (sensorId) {
            case SENSORS_BUMPS_AND_WHEEL_DROPS:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_bumpRight, (dataByte & 0x01) != 0);
                setSensorBoolean(SENSORID_bumpLeft, (dataByte & 0x02) != 0);
                setSensorBoolean(SENSORID_wheelDropRight, (dataByte & 0x04) != 0);
                setSensorBoolean(SENSORID_wheelDropLeft, (dataByte & 0x08) != 0);
                setSensorBoolean(SENSORID_wheelDropCaster, (dataByte & 0x10) != 0);
                break;
            case SENSORS_WALL:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_wall, (dataByte & 0x01) != 0);
                break;
            case SENSORS_CLIFF_LEFT:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_cliffLeft, (dataByte & 0x01) != 0);
                break;
            case SENSORS_CLIFF_FRONT_LEFT:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_cliffFrontLeft, (dataByte & 0x01) != 0);
                break;
            case SENSORS_CLIFF_FRONT_RIGHT:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_cliffFrontRight, (dataByte & 0x01) != 0);
                break;
            case SENSORS_CLIFF_RIGHT:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_cliffRight, (dataByte & 0x01) != 0);
                break;
            case SENSORS_VIRTUAL_WALL:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_virtualWall, (dataByte & 0x01) != 0);
                break;
            case SENSORS_LOWS_SIDE_DRIVER_AND_WHEEL_OVERCURRENTS:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_lowSideDriver0Overcurrent, (dataByte & 0x02) != 0);
                setSensorBoolean(SENSORID_lowSideDriver1Overcurrent, (dataByte & 0x01) != 0);
                setSensorBoolean(SENSORID_lowSideDriver2Overcurrent, (dataByte & 0x04) != 0);
                setSensorBoolean(SENSORID_rightWheelOvercurrent, (dataByte & 0x08) != 0);
                setSensorBoolean(SENSORID_leftWheelOvercurrent, (dataByte & 0x10) != 0);
                break;
            case SENSORS_DUMMY1:
                serialConnection.readUnsignedByte();
                break;
            case SENSORS_DUMMY2:
                serialConnection.readUnsignedByte();
                break;
            case SENSORS_INFRARED_BYTE:
                setSensorInteger(SENSORID_infraredByte, serialConnection.readUnsignedByte());
                break;
            case SENSORS_BUTTONS:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_playButton, (dataByte & 0x01) != 0);
                setSensorBoolean(SENSORID_advanceButton, (dataByte & 0x04) != 0);
                break;
            case SENSORS_DISTANCE:
                dataWord = serialConnection.readSignedWord();
                setSensorInteger(SENSORID_distance, dataWord);
                break;
            case SENSORS_ANGLE:
                dataWord = serialConnection.readSignedWord();
                setSensorInteger(SENSORID_angle, dataWord);
                break;
            case SENSORS_CHARGING_STATE:
                setSensorInteger(SENSORID_chargingState, serialConnection.readUnsignedByte());
                break;
            case SENSORS_VOLTAGE:
                setSensorInteger(SENSORID_voltage, serialConnection.readUnsignedWord());
                break;
            case SENSORS_CURRENT:
                setSensorInteger(SENSORID_current, serialConnection.readSignedWord());
                break;
            case SENSORS_BATTERY_TEMPERATURE:
                setSensorInteger(SENSORID_batteryTemperature, serialConnection.readSignedByte());
                break;
            case SENSORS_BATTERY_CHARGE:
                setSensorInteger(SENSORID_batteryCharge, serialConnection.readUnsignedWord());
                break;
            case SENSORS_BATTERY_CAPACITY:
                setSensorInteger(SENSORID_batteryCapacity, serialConnection.readUnsignedWord());
                break;
            case SENSORS_WALL_SIGNAL:
                setSensorInteger(SENSORID_wallSignal, serialConnection.readUnsignedWord());
                break;
            case SENSORS_CLIFF_LEFT_SIGNAL:
                setSensorInteger(SENSORID_cliffLeftSignal, serialConnection.readUnsignedWord());
                break;
            case SENSORS_CLIFF_FRONT_LEFT_SIGNAL:
                setSensorInteger(SENSORID_cliffFrontLeftSignal, serialConnection.readUnsignedWord());
                break;
            case SENSORS_CLIFF_FRONT_RIGHT_SIGNAL:
                setSensorInteger(SENSORID_cliffFrontRightSignal, serialConnection.readUnsignedWord());
                break;
            case SENSORS_CLIFF_RIGHT_SIGNAL:
                setSensorInteger(SENSORID_cliffRightSignal, serialConnection.readUnsignedWord());
                break;
            case SENSORS_CARGO_BAY_DIGITAL_INPUTS:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_cargoBayDigitalInput0, (dataByte & 0x01) != 0);
                setSensorBoolean(SENSORID_cargoBayDigitalInput1, (dataByte & 0x02) != 0);
                setSensorBoolean(SENSORID_cargoBayDigitalInput2, (dataByte & 0x04) != 0);
                setSensorBoolean(SENSORID_cargoBayDigitalInput3, (dataByte & 0x08) != 0);
                setSensorBoolean(SENSORID_cargoBayDeviceDetectBaudRateChange, (dataByte & 0x10) != 0);
                break;
            case SENSORS_CARGO_BAY_ANALOG_SIGNAL:
                setSensorInteger(SENSORID_cargoBayAnalogSignal, serialConnection.readUnsignedWord());
                break;
            case SENSORS_CHARGING_SOURCES_AVAILABLE:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_internalChargerAvailable, (dataByte & 0x01) != 0);
                setSensorBoolean(SENSORID_homeBaseChargerAvailable, (dataByte & 0x02) != 0);
                break;
            case SENSORS_OI_MODE:
                setSensorInteger(SENSORID_oiMode, serialConnection.readUnsignedByte());
                break;
            case SENSORS_SONG_NUMBER:
                setSensorInteger(SENSORID_songNumber, serialConnection.readUnsignedByte());
                break;
            case SENSORS_SONG_PLAYING:
                dataByte = serialConnection.readUnsignedByte();
                setSensorBoolean(SENSORID_songPlaying, (dataByte & 0x01) != 0);
                break;
            case SENSORS_NUMBER_OF_STREAM_PACKETS:
                setSensorInteger(SENSORID_numberOfStreamPackets, serialConnection.readUnsignedByte());
                break;
            case SENSORS_REQUESTED_VELOCITY:
                setSensorInteger(SENSORID_requestedVelocity, serialConnection.readSignedWord());
                break;
            case SENSORS_REQUESTED_RADIUS:
                setSensorInteger(SENSORID_requestedRadius, serialConnection.readSignedWord());
                break;
            case SENSORS_REQUESTED_RIGHT_VELOCITY:
                setSensorInteger(SENSORID_requestedRightVelocity, serialConnection.readSignedWord());
                break;
            case SENSORS_REQUESTED_LEFT_VELOCITY:
                setSensorInteger(SENSORID_requestedLeftVelocity, serialConnection.readSignedWord());
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(sensorId));
        }
    }

    public synchronized void safe()
            throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_MODE_SAFE);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void sendIr(int byteValue) throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_SEND_IR);
        serialConnection.writeByte(byteValue);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    private void setSensorBoolean(int sensorId, boolean value) {
        sensorValues[sensorId] = value ? 1 : 0;
    }

    private void setSensorInteger(int sensorId, int value) {
        sensorValues[sensorId] = value;
    }

    public synchronized int[] showScript() throws ConnectionLostException {
        serialConnection.writeByte(COMMAND_SHOW_SCRIPT);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
        int size = serialConnection.readUnsignedByte();
        int[] script = new int[size];
        serialConnection.readUnsignedBytes(script, 0, size);
        return script;
    }

    public synchronized void song(int songNumber, int[] notesAndDurations)
            throws ConnectionLostException {
        song(songNumber, notesAndDurations, 0, notesAndDurations.length);
    }

    public synchronized void song(int songNumber, int[] notesAndDurations, int startIndex, int length) throws ConnectionLostException {
        if (songNumber < 0 || songNumber > 15) {
            throw new IllegalArgumentException("songNumber " + songNumber);
        }
        if ((length & 0x01) == 0x01) {
            throw new IllegalArgumentException("length " + songNumber + "must be even");
        }
        if (length < 1 || length > (256 - (songNumber * 16 * 2))) {
            throw new IllegalArgumentException("length " + length);
        }
        serialConnection.writeByte(COMMAND_SONG);
        serialConnection.writeByte(songNumber);
        serialConnection.writeByte(length >> 1);
        serialConnection.writeBytes(notesAndDurations, startIndex, length);
        SystemClock.sleep(AFTER_COMMAND_PAUSE_TIME);
    }

    public synchronized void waitButtonPressed(boolean playButton, boolean beep) throws ConnectionLostException {
        int startingPowerLedIntensity = powerLedIntensity;
        int startingPowerLedColor = powerLedColor;
        boolean startingPlayLedState = isPlayLedOn;
        boolean startingAdvanceLedState = isAdvanceLedOn;
        int totalTimeWaiting = 0;
        boolean gotButtonDown = false;
        final int noteDuration = 16;
        if (beep) {
            song(0, new int[]{58, noteDuration, 62, noteDuration});
        }
        while (true) {
            readSensors(SENSORS_BUTTONS);
            if (gotButtonDown && playButton && !isPlayButtonDown()) {
                break;
            }
            if (gotButtonDown && !playButton && !isAdvanceButtonDown()) {
                break;
            }
            if (playButton && isPlayButtonDown()) {
                gotButtonDown = true;
            }
            if (!playButton && isAdvanceButtonDown()) {
                gotButtonDown = true;
            }
            SystemClock.sleep(noteDuration);
            totalTimeWaiting += noteDuration;
            if (totalTimeWaiting > 500) {
                if (beep) {
                    playSong(0);
                }
                ledsToggle(false, playButton, !playButton);
                totalTimeWaiting = 0;
            }
        }
        leds(startingPowerLedColor, startingPowerLedIntensity, startingPlayLedState, startingAdvanceLedState);
    }

    public void closeConnection() {
        if (serialConnection != null) {
            serialConnection.close();
        }
    }
}

