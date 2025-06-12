package com.batch.GUI.UserAdministration;

import com.batch.Database.Entities.User;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public class LoginWindow extends Stage {
    private static LoginWindow singleton = null;
    private static final VBox root = new VBox();
    private static final Scene scene;
    private static final Button LogIn;
    private static final Button Cancel;
    private static final Label mainlabel;
    private static final TextField username;
    private static final PasswordField password;
    private static final User user;
    private static Optional<User> returnValue;

    static {
        scene = new Scene(root);
        LogIn = new Button("Log in");
        Cancel = new Button("Cancel");
        mainlabel = new Label("System Authorization\nSign in");
        username = new TextField("Administrator");
        password = new PasswordField();
        user = new User();
        returnValue = Optional.ofNullable(user);
    }

    private LoginWindow() {
        this.graphicsBuilder();
    }

    public static LoginWindow GetInstance() {
        if (singleton == null) {
            synchronized (LoginWindow.class) {
                singleton = new LoginWindow();
            }
        }

        return singleton;
    }

    private void graphicsBuilder() {
        password.setText("Admin123456");
        LogIn.setPrefWidth(300.0F);
        LogIn.setFont(Font.font(13.0F));
        Cancel.setMaxWidth(300.0F);
        Cancel.setFont(Font.font(13.0F));
        Cancel.setCancelButton(true);
        mainlabel.setPadding(new Insets(20.0F));
        mainlabel.setStyle("-fx-font: 30px Tahoma;\n    -fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, aqua 0%, red 50%);\n    -fx-stroke: black;\n    -fx-stroke-width: 1;");
        DropShadow shadow = new DropShadow(2.0F, 2.0F, 2.0F, Color.GRAY);
        mainlabel.setEffect(shadow);
        mainlabel.setTextAlignment(TextAlignment.CENTER);
        username.setMaxWidth(300.0F);
        username.setPromptText("Enter Username");
        username.setMinHeight(25.0F);
        password.setMaxWidth(300.0F);
        password.setPromptText("Enter Password");
        password.setMinHeight(25.0F);
        root.getChildren().addAll(mainlabel, username, password, new Pane(), LogIn, Cancel);
        root.setSpacing(5.0F);
        root.setPadding(new Insets(0.0F, 20.0F, 20.0F, 20.0F));
        root.setAlignment(Pos.CENTER);
        this.setTitle("Log in");
        this.setScene(scene);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.UTILITY);
        this.initModality(Modality.NONE);
        this.setOnCloseRequest(Event::consume);
    }

    public void setMainWindow(Stage mainWindow) {
        this.initOwner(mainWindow);
    }

    public Optional<User> showAndReturnUser() {
        password.setText("");
        LogIn.setText("Log in");
        username.setEditable(true);
        password.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                user.setUserName(username.getText());
                user.setPassword(password.getText());
                returnValue = Optional.ofNullable(user);
                this.hide();
            }

        });
        LogIn.setOnMouseClicked((action) -> {
            user.setUserName(username.getText());
            user.setPassword(password.getText());
            returnValue = Optional.ofNullable(user);
            this.hide();
        });
        Cancel.setOnMouseClicked((action) -> {
            user.setUserName("");
            user.setPassword("");
            returnValue = Optional.ofNullable(user);
            this.hide();
        });
        password.requestFocus();
        this.showAndWait();
        return returnValue;
    }

    public void showAndUpdatePassword(String user) {
        password.setText("");
        username.setText(user);
        username.setEditable(false);
        LogIn.setText("Save");
        Cancel.setOnMouseClicked((action) -> this.hide());
        this.showAndWait();
    }
}
