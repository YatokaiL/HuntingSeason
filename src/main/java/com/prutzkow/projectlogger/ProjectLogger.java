package com.prutzkow.projectlogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ProjectLogger {
    public static final Logger logger = ProjectLogger.configureLogger();

    private static Logger configureLogger() {
        var logger = Logger.getLogger("HuntingSeason");
        logger.setLevel(Level.CONFIG);
        logger.setUseParentHandlers(false);

        Handler handler;
        handler = ProjectLogger.configureConsoleHandler();
        logger.addHandler(handler);


        return logger;
    }

    private static Handler configureFileHandler() {
        Handler handler = null;
        try {
            Files.createDirectories(Paths.get("logs"));

            var formatter = new SimpleFormatter();
            handler = new FileHandler("logs/huntingseason%u.log", 1024 * 1024, 3, true);
            handler.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
        return handler;
    }

    private static Handler configureConsoleHandler() {
        var handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        return handler;
    }


}