
package com.batch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class ApplicationContext extends Application {
    private static final Logger log = LogManager.getLogger(ApplicationContext.class);
    public static ConfigurableApplicationContext applicationContext;

    public void start(Stage stage) throws Exception {
        try {
            System.err.println("Puplishing start event");
            applicationContext.publishEvent(new GraphicsInitializerEvent(stage));
            applicationContext.start();
        } catch (Exception e) {
            log.fatal(e, e);
        }
    }

    public void init() throws Exception {
        ApplicationContextInitializer<GenericApplicationContext> initializer = (applicationContext) -> applicationContext.registerBean(Application.class, () -> this);
        applicationContext = (new SpringApplicationBuilder()).sources(BatchingApplication.class).initializers(new ApplicationContextInitializer[]{initializer}).run(this.getParameters().getRaw().toArray(new String[0]));
        applicationContext.registerShutdownHook();
//        applicationContext.addApplicationListener((event) -> Platform.exit());
        super.init();
    }

    public void stop() throws Exception {
        applicationContext.stop();
        Thread.sleep(1000L);
        System.exit(0);
    }

    public static class GraphicsInitializerEvent extends ApplicationEvent {
        public GraphicsInitializerEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return (Stage) this.getSource();
        }
    }
}
