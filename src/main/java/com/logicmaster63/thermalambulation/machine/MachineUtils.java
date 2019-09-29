package com.logicmaster63.thermalambulation.machine;

import com.logicmaster63.thermalambulation.ThermalAmbulation;
import org.apache.logging.log4j.Level;

import java.io.*;

public final class MachineUtils {

    public static byte[] toBytes(IMachine machine) throws IOException {
        try(ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream()) {
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOut);
            outputStream.writeObject(machine);
            outputStream.flush();
            return byteArrayOut.toByteArray();
        }
    }

    public static IMachine fromBytes(byte[] bytes) throws IOException {
        try(ByteArrayInputStream byteArray = new ByteArrayInputStream(bytes)) {
            ObjectInputStream outputStream = new ObjectInputStream(byteArray);
            return (IMachine) outputStream.readObject();
        } catch (Exception e) {
            ThermalAmbulation.logger.error("Failed to create IMachine from bytes: " + e);
            return new NullMachine();
        }
    }
}
