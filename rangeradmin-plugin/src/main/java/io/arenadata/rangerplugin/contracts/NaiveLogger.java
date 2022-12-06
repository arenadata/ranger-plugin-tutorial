package io.arenadata.rangerplugin.contracts;
import io.arenadata.rangerplugin.system.ManagedException;

public interface NaiveLogger {
    void writeLogMessage(String message) throws ManagedException;
}
