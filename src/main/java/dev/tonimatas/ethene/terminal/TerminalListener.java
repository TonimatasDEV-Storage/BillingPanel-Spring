package dev.tonimatas.ethene.terminal;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class TerminalListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerminalListener.class);
    private final ApplicationContext applicationContext;

    public TerminalListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void listenForStopCommand() {
        Thread thread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            LOGGER.info("Type 'stop' to shut down the application.");
            while (true) {
                String input = scanner.nextLine();
                if ("stop".equalsIgnoreCase(input.trim())) {
                    LOGGER.info("Shutting down the application.");
                    SpringApplication.exit(applicationContext, () -> 0);
                    break;
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
