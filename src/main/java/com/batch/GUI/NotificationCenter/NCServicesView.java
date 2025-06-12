package com.batch.GUI.NotificationCenter;

import com.batch.ApplicationContext;
import com.batch.Services.NotificationService.ErrorObject;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class NCServicesView extends BorderPane {
    private static final Map<String, ServiceContainer> allPartitions = new HashMap();
    private static VBox mainContainer;
    private NCController controller;
    private NCModel model;
    private ScrollPane scrollPane;

    public NCServicesView(Stage ownerStage) {
        this.initialize();
        this.graphicsBuild();
    }

    public static void addNewBGErrorPartition(ErrorObject errorObject) {
        Platform.runLater(() -> {
            ServiceContainer container = allPartitions.get(errorObject.getServiceName());
            BGNotification alarmNotification = new BGNotification(errorObject);
            container.addNewNotification(alarmNotification, errorObject.getErrorFamily());
        });
    }

    public static void addNewServicePart(String serviceName) {
        Platform.runLater(() -> {
            ServiceContainer serviceContainer = new ServiceContainer(serviceName);
            serviceContainer.prefWidthProperty().bind(mainContainer.widthProperty().subtract(20));
            allPartitions.put(serviceName, serviceContainer);
            mainContainer.getChildren().add(serviceContainer);
        });
    }

    public static void removeServicePart(String serviceName) {
        Platform.runLater(() -> {
            ServiceContainer container = allPartitions.get(serviceName);
            allPartitions.remove(serviceName);
            mainContainer.getChildren().remove(container);
        });
    }

    public static void removeFamilyPartition(String service, String family) {
        Platform.runLater(() -> {
            ServiceContainer serviceContainer = allPartitions.get(service);
            if (serviceContainer != null) {
                serviceContainer.removeNotification(family);
            }

        });
    }

    protected void initialize() {
        this.controller = ApplicationContext.applicationContext.getBean(NCController.class);
        this.model = this.controller.getModel();
        mainContainer = new VBox();
        this.scrollPane = new ScrollPane(mainContainer);
    }

    protected void graphicsBuild() {
        this.setPrefSize(1000.0F, 850.0F);
        mainContainer.prefWidthProperty().bind(this.scrollPane.widthProperty().subtract(20));
        mainContainer.setSpacing(10.0F);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(10.0F));
        this.setCenter(this.scrollPane);
    }

    public String toString() {
        return "Notification service";
    }
}
