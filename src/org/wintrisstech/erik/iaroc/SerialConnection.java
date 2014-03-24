package org.wintrisstech.erik.iaroc;

import android.os.SystemClock;
import android.util.Log;
import ioio.lib.api.IOIO;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class represents the communication channel between the IOIO board and
 * the iRobot Create. At most one instance of this class may be instantiated.
 * Use the
 * {@link #getInstance(IOIO, boolean) getInsatnce(IOIO ioio, boolean debug)}
 * method to get that instance.
 *
 */
final class SerialConnection { //Note: Package-private class

    private static final String TAG = "SerialConnection";
    private static final int OI_MODE_PASSIVE = 1;
    private static final int SENSORS_OI_MODE = 35;
    private static final int SENSOR_COMMAND = 142;
    private static final int BAUD_RATE = 57600;
    private IOIO ioio;
    private InputStream input;
    private OutputStream output;
    private static final int CREATE_RX_PIN = 13;
    private static final int CREATE_TX_PIN = 14;
    private Uart uart;
    private boolean debug = false;
    private byte[] uartBuffer = new byte[1000]; //buffer used in read and write operations
    private static final int MAX_COMMAND_SIZE = 26; //max number of bytes that can be sent in 15 ms at baud rate 19,200.
    private static final int COMMAND_START = 128; //Starts the OI. Must be the first command sent.
    private static final SerialConnection theConnection = new SerialConnection();

//  Constructor of a serial connection between the Create and the IOIO board
    private SerialConnection() {
    }

    /**
     * Gets a default serial connection to the Create. This method returns after
     * a connection between the IOIO and the Create has been established.
     *
     * @param ioio the ioio instance used to connect to the Create
     * @param debug if true establishes a connection that prints out debugging
     * information.
     * @return a serial connection to the Create
     */
    static SerialConnection getInstance(IOIO ioio, boolean debug)
            throws ConnectionLostException {
        theConnection.ioio = ioio;
        theConnection.debug = debug;
//        new BootloaderListener().start();
        try {
            theConnection.connectToCreate();
        } catch (Exception e) {
            if (debug) {
                Log.d(TAG, "Try connecting one more time in case user forgot to "
                        + "turn on the Create");
            }
            SystemClock.sleep(2500);
            theConnection.connectToCreate();
        }

        return theConnection;
    }

//     Sends the start command to the Create
    private void connectToCreate() throws ConnectionLostException {
        uart = theConnection.ioio.openUart(CREATE_RX_PIN, CREATE_TX_PIN, BAUD_RATE, Uart.Parity.NONE, Uart.StopBits.ONE);
        input = uart.getInputStream();
        output = uart.getOutputStream();
        final int numberOfStartsToSend = MAX_COMMAND_SIZE;
//        final int numberOfStartsToSend = 1;
        for (int i = 0; i < numberOfStartsToSend; i++) {
            uartBuffer[i] = (byte) COMMAND_START;
        }
        writeBytes(uartBuffer, 0, numberOfStartsToSend);
        if (debug) {
            Log.d(TAG, "Waiting for the Create to get into passive mode");
        }
        while (true) {
            uartBuffer[0] = (byte) SENSOR_COMMAND;
            uartBuffer[1] = (byte) SENSORS_OI_MODE;
            writeBytes(uartBuffer, 0, 2);
            SystemClock.sleep(100);
            int mode = readUnsignedByte();
            if (mode == OI_MODE_PASSIVE) {
                break;
            }
        }
    }

    /**
     * The maximum number of bytes that can be transmitted in a command to the
     * Create
     *
     * @return the max size in bytes
     */
    int getMaxCommandSize() {
        return MAX_COMMAND_SIZE;
    }

    /**
     * Sets the debugging mode
     *
     * @param debug if true generates printouts to Log.
     */
    void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Reads a byte received from the Create over the serial connection and
     * interprets it as a signed byte, i.e., value is in the range -128 - 127.
     *
     * @return the value as an int
     *
     * @throws ConnectionLostException
     */
    int readSignedByte() throws ConnectionLostException {
        try {
            int result = input.read();
            if (result > 0x7F) {
                result -= 0x100;
            }
            if (debug) {
                Log.d(TAG, String.format("Read signed byte: %d", result));
            }
            return result;
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Reads a byte received from the Create over the serial connection and
     * interprets it as an unsigned byte, i.e., value is in range 0 - 255.
     *
     * @return the value as an int
     * @throws ConnectionLostException
     */
    int readUnsignedByte() throws ConnectionLostException {
        try {
            int result = input.read();
            if (debug) {
                Log.d(TAG, String.format("Read unsigned byte: %d", result));
            }
            return result;
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Reads 2 bytes received from the Create over the serial connection and
     * interprets them as a signed word, i.e., value is in range -32768 - 32767.
     *
     * @return the value as an int
     * @throws ConnectionLostException
     */
    int readSignedWord() throws ConnectionLostException {
        try {
            int high = input.read(); // 0 <= high <= 0xFF
            int low = input.read(); // 0 <= low <= 0xFF
            int signed = (high << 8) | low; // 0 <= signed <= 0xFFFF
            // Convert signed to a signed value:
            if (signed > 0x7FFF) {
                signed -= 0x10000;
            }
            if (debug) {
                Log.d(TAG, String.format("Read signed word: %d|%d = %d", high, low, signed));
            }
            return signed;
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Reads several bytes received from the Create over the serial connection
     * and interprets each as an unsigned byte, i.e., each value is in the range
     * 0 - 255.
     *
     * @param buffer an array to store the read bytes
     * @param start offset into buffer
     * @param length the maximum bytes to read
     * @return the number of bytes received
     * @throws ConnectionLostException
     */
    int readUnsignedBytes(int[] buffer, int start, int length) throws ConnectionLostException {
        try {
            if (debug) {
                Log.d(TAG, String.format("Read unsigned bytes: %d", length));
            }
            if (length > uartBuffer.length) {
                uartBuffer = new byte[length];
            }
            int readCount = input.read(uartBuffer, 0, length);
            for (int i = 0; i < length; i++) {
                buffer[start + i] = uartBuffer[i] & 0xFF;
            }
            if (debug) {
                for (int i = 0; i < length; i++) {
                    Log.d(TAG, String.format("[%d] = %d", i, buffer[start + i]));
                }
            }
            return readCount;
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Reads 2 bytes received from the Create over the serial connection and
     * interprets them as an unsigned word, i.e., value is in range 0 - 65535.
     *
     * @return the value as an int
     * @throws ConnectionLostException
     */
    int readUnsignedWord() throws ConnectionLostException {
        try {
            int high = input.read(); // 0 <= high <= 0xFF
            int low = input.read(); // 0 <= low <= 0xFF
            int unsigned = (high << 8) | low; // 0 <= unsigned <= 0xFFFF
            if (debug) {
                Log.d(TAG, String.format("Read unsigned word: %d|%d = %", high, low, unsigned));
            }
            return unsigned;
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Sends a byte over the serial connection to the Create.
     *
     * @param b the byte sent
     * @throws ConnectionLostException
     */
    void writeByte(int b) throws ConnectionLostException {
        try {
            if (debug) {
                Log.d(TAG, String.format("Sending byte: %d", b));
            }
            output.write(b);
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Sends several bytes over the serial connection to the Create
     *
     * @param bytes an array of bytes
     * @param start the position of first byte to be sent in the array
     * @param length the number of bytes sent.
     * @throws ConnectionLostException
     */
    void writeBytes(byte[] bytes, int start, int length) throws ConnectionLostException {
        try {
            if (debug) {
                Log.d(TAG, String.format("Sending bytes byte[] length: %d", length));
                for (int i = 0; i < length; i++) {
                    Log.d(TAG, String.format("[%d] = %d", i, bytes[start + i]));
                }
            }
            output.write(bytes, start, length);
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Sends several bytes over the serial connection to the Create
     *
     * @param ints an array of ints that are cast to byte before sending
     * @param start the position of first byte to be sent in the array
     * @param length the number of bytes sent.
     * @throws ConnectionLostException
     */
    void writeBytes(int[] ints, int start, int length) throws ConnectionLostException {
        try {
            if (debug) {
                Log.d(TAG, String.format("Sending bytes byte[] length: %d", length));
                for (int i = 0; i < length; i++) {
                    Log.d(TAG, String.format("[%d] = %d", i, ints[start + i]));
                }
            }
            if (length > uartBuffer.length) {
                uartBuffer = new byte[length];
            }
            for (int i = 0; i < length; i++) {
                uartBuffer[i] = (byte) ints[start + i];
            }
            output.write(uartBuffer, 0, length);
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Sends a signed word to the Create over the serial connection as two
     * bytes, high byte first.
     *
     * @param value an int in the range -32768 - 32767.
     * @throws ConnectionLostException
     */
    public void writeSignedWord(int value) throws ConnectionLostException {
        try {
            // Java bit representation is already two's complement
            uartBuffer[0] = (byte) (value >> 8);
            uartBuffer[1] = (byte) (value & 0xFF);
            output.write(uartBuffer, 0, 2);
            if (debug) {
                Log.d(TAG, "Sending signed word" + value);
            }
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Sends an unsigned word to the Create over the serial connection as two
     * bytes, high byte first.
     *
     * @param value an int in the range 0 - 65535.
     * @throws ConnectionLostException
     */
    public void writeUnsignedWord(int value) throws ConnectionLostException {
        try {
            uartBuffer[0] = (byte) (value >> 8);
            uartBuffer[1] = (byte) (value & 0xFF);
            output.write(uartBuffer, 0, 2);
            if (debug) {
                Log.d(TAG, "Sending unsigned word" + value);
            }
        } catch (IOException ex) {
            throw new ConnectionLostException(ex);
        }
    }

    /**
     * Closes the serial connection
     */
    public void close() {
        Log.i(TAG, "Closing connection");
        if (uart != null) {
            uart.close();
        }
        uart = null;
    }
}

