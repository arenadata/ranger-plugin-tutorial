package io.arenadata.rangerplugin.controllers;
import io.arenadata.rangerplugin.contracts.NaiveLogger;
import io.arenadata.rangerplugin.system.ManagedException;
import io.arenadata.rangerplugin.system.ManagedExceptionType;
import java.io.*;
import java.time.LocalDate;

public final class NaiveLoggerImpl implements NaiveLogger {
    private static final String DEFAULT_TEMP_LOGFILE_DIR  = "/tmp/";
    private static final String DEFAULT_TEMP_LOGFILE_NAME = "ranger-plugin-test.log";
    private static NaiveLoggerImpl instance = null;

    private NaiveLoggerImpl() { }

    public void writeLogMessage(String message) throws ManagedException {
        if (message == null || message.trim().isEmpty())
            throw new ManagedException(ManagedExceptionType.INPUT_STRING_NULL_OR_EMPTY, "Can't write the log message from naive logger, due the given message is null or empty.");

        try {
            final File file = new File(String.format("%s%s_%s", DEFAULT_TEMP_LOGFILE_DIR, LocalDate.now(), DEFAULT_TEMP_LOGFILE_NAME));
            final FileOutputStream fileStream = new FileOutputStream(file, true);
            final PrintWriter writer = new PrintWriter(fileStream);
            writer.println(message);
            writer.close();
            fileStream.close();
        } catch (IOException fileException) {
            throw new ManagedException(ManagedExceptionType.CONFIG_FILE_IO_EXCEPTION, fileException.getMessage());
        }
    }

    public static synchronized NaiveLoggerImpl getInstance() {
        if (instance == null)
            instance = new NaiveLoggerImpl();

        return instance;
    }
}
