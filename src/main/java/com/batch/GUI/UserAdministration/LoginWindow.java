package com.batch.GUI.UserAdministration;

import com.batch.Database.Entities.User;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.util.Optional;

public class LoginWindow extends Stage {

    private static LoginWindow singleton = null;

    private static VBox root = new VBox();
    private static Scene scene = new Scene(root);

    private static Button LogIn = new Button("Log in");
    private static Button Cancel = new Button("Cancel");

    private static Label mainlabel = new Label("System Authorization\nSign in");

    private static TextField username = new TextField("Administrator");
    private static PasswordField password = new PasswordField();

    private static User user = new User();
    private static Optional<User> returnValue = Optional.ofNullable(user);

    private LoginWindow() {
        graphicsBuilder();
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

        LogIn.setPrefWidth(300);
        LogIn.setFont(Font.font(13));

        Cancel.setMaxWidth(300);
        Cancel.setFont(Font.font(13));
        Cancel.setCancelButton(true);

        mainlabel.setPadding(new Insets(20));
        mainlabel.setStyle("-fx-font: 30px Tahoma;\n"
                + "    -fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, aqua 0%, red 50%);\n"
                + "    -fx-stroke: black;\n"
                + "    -fx-stroke-width: 1;");

        DropShadow shadow = new DropShadow(2, 2, 2, Color.GRAY);
        mainlabel.setEffect(shadow);
        mainlabel.setTextAlignment(TextAlignment.CENTER);

        username.setMaxWidth(300);
        username.setPromptText("Enter Username");
        username.setMinHeight(25);

        password.setMaxWidth(300);
        password.setPromptText("Enter Password");
        password.setMinHeight(25);

        root.getChildren().addAll(mainlabel, username, password, new Pane(), LogIn, Cancel);
        root.setSpacing(5);
        root.setPadding(new Insets(0, 20, 20, 20));
        root.setAlignment(Pos.CENTER);

//        scene.getStylesheets().add("/Com/MainGraphicalUserInterface/StylingSheets/loginWindow.css");
        setTitle("Log in");
        setScene(scene);
        setResizable(false);
        setAlwaysOnTop(true);
//        getIcons().add(new Image("/Com/MainGraphicalUserInterface/Icons/loginwindow.png"));
        initModality(Modality.NONE);

        setOnCloseRequest(a -> {
            a.consume();
        });
    }

    public void setMainWindow(Stage mainWindow) {
        initOwner(mainWindow);
    }

    public Optional<User> showAndReturnUser() {
        password.setText("");
        LogIn.setText("Log in");
        username.setEditable(true);
        
        password.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    user.setUserName(username.getText());
                    user.setPassword(password.getText());
                    returnValue = Optional.ofNullable(user);
                    hide();
                }
            }
        });
        LogIn.setOnMouseClicked(action -> {
            user.setUserName(username.getText());
            user.setPassword(password.getText());
            returnValue = Optional.ofNullable(user);
            hide();
        });
        Cancel.setOnMouseClicked(action -> {
            user.setUserName("");
            user.setPassword("");
            returnValue = Optional.ofNullable(user);
            hide();
        });
        showAndWait();
        return returnValue;
    }
    
    public void showAndUpdatePassword(String user){
        password.setText("");
        username.setText(user);
        username.setEditable(false);
        LogIn.setText("Save");
//        LogIn.setOnMouseClicked(action -> {
//            UserAuthorizationService.getService().changePassword(user, password.getText());
//            hide();
//        });
        Cancel.setOnMouseClicked(action -> {
            hide();
        });
        showAndWait();
    }
}
