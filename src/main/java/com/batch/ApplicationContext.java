package com.batch;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.GenericApplicationContext;

@Log4j2
public class ApplicationContext extends Application {

    public static ConfigurableApplicationContext applicationContext;

    @Override
    public void start(Stage stage) throws Exception {
        applicationContext.publishEvent(new GraphicsInitializerEvent(stage));
        applicationContext.start();
    }

    @Override
    public void init() throws Exception {
        ApplicationContextInitializer<GenericApplicationContext> initializer = applicationContext -> {
            applicationContext.registerBean(Application.class, () -> ApplicationContext.this);
        };
        ApplicationContext.applicationContext = new SpringApplicationBuilder()
                .sources(BatchingApplication.class)
                .initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));
        applicationContext.registerShutdownHook();
        applicationContext.addApplicationListener((ApplicationListener <ContextClosedEvent>) event -> Platform.exit());
        super.init();
    }

    @Override
    public void stop() throws Exception {
        applicationContext.stop();
        Thread.sleep(500);
        System.exit(0);
    }

    public static class GraphicsInitializerEvent extends ApplicationEvent {
        public GraphicsInitializerEvent(Stage stage) {
            super(stage);
        }
        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}
