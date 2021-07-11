package com.batch.GUI.UserAdministration;

import com.batch.Database.Entities.User;
import com.batch.GUI.InitialWindow.InitialWindow;
import com.batch.Services.UserAdministration.UserAuthorizationService;
import com.batch.Services.UserAdministration.UserEvent;
import com.batch.Utilities.RestrictiveTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashSet;

@Component
@RequiredArgsConstructor
public class UserAdministrationWindow extends TabPane {

    private final UserAuthorizationService userAuthorizationService;

    private Tab usersTab = new Tab("Users Management");
    private Tab groupsTab = new Tab("Groups Management");

    private HBox groupsPaneTable = new HBox();

    private VBox groupsControlAreaPane = new VBox();
    private VBox usersControlAreaPane = new VBox();

    private VBox groupsDataEntry = new VBox();
    private GridPane usersDataEntry = new GridPane();

    private VBox groupsvbox = new VBox();
    private VBox descrepvbox = new VBox();

    private BorderPane groupsBorderPane = new BorderPane();
    private VBox usersBorderPane = new VBox();

    private ToolBar groupsToolbar = new ToolBar();
    private ToolBar usersToolbar = new ToolBar();

    private final ScrollPane scrollPane = new ScrollPane();

    private User currentUser = null;

    private final ObservableList<User> list = FXCollections.observableArrayList();
    private final TableView<User> table = new TableView<>();
    private final TableColumn<User, String> UsernameColumn = new TableColumn<>("User name");
    private final TableColumn<User, String> groupColumn = new TableColumn<>("Group");
    private final TableColumn<User, String> autoLogOffColumn = new TableColumn<>("Auto logoff");
    private final TableColumn<User, String> logOffTimeColumn = new TableColumn<>("Logoff time");

    private final Button createGroup = new Button("Create group");
    private final Button deleteGroup = new Button("Delete selected group");

    private final Button createUser = new Button("Create User");
//    private final Button updateUser = new Button("Update User");
    private final Button deleteUser = new Button("Delete User");

    private final RestrictiveTextField groupMainField = new RestrictiveTextField();
    private final RestrictiveTextField descMainField = new RestrictiveTextField();

    private final RestrictiveTextField userName = new RestrictiveTextField();
    private final PasswordField Password = new PasswordField();
    private final Spinner<Long> logOffTime = new Spinner<>();
    private final CheckBox isAutoLogOff = new CheckBox();
    private final ComboBox<String> userWindowGroups = new ComboBox<>();

    private final Label usernameLabel = new Label("User Name");
    private final Label passwordLabel = new Label("Password ");
    private final Label groupsLabel = new Label("Groups");
    private final Label logOffTimeLabel = new Label("Logg off time ");

    private LinkedHashSet<String> setToDelete = new LinkedHashSet<>();

    @Autowired(required = false)
    private InitialWindow window;


    @EventListener
    public void atStart(ContextStartedEvent event){
        try {
            graphicsBuilder();
            actionHandling();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @EventListener
    public void newUserLogIn(UserEvent event){
        Platform.runLater(this::update);
    }

    //graphics builder
    private void graphicsBuilder() {

        //tabs management
        usersTab.setClosable(false);
        groupsTab.setClosable(false);

        usersTab.setContent(usersBorderPane);
        groupsTab.setContent(groupsBorderPane);

        getTabs().addAll(usersTab, groupsTab);
        setPrefSize(1200, 900);

        groupsGraphicsBuilder();
        usersGraphicsBuilder();
        update();

    }
    private void usersGraphicsBuilder() {
        //filling control items
        isAutoLogOff.setText("Is Automatic Logg Off");
        usersDataEntry.setVgap(10);
        usersDataEntry.setHgap(10);
        usersDataEntry.setPadding(new Insets(10));

        usernameLabel.setPrefWidth(100);
        groupsLabel.setPrefWidth(100);
        logOffTimeLabel.setPrefWidth(100);
        passwordLabel.setPrefWidth(100);
        userName.setPrefWidth(200);
        logOffTime.setPrefWidth(200);
        isAutoLogOff.setFont(Font.font(15));
        userWindowGroups.setMinWidth(250);
        //duration spinner configuration
        logOffTime.setValueFactory(new SpinnerValueFactory<Long>() {
            @Override
            public void decrement(int steps) {
                setValue(getValue() - 60);
            }

            @Override
            public void increment(int steps) {
                setValue(getValue() + 60);
            }
        });
        logOffTime.setEditable(true);
        logOffTime.getValueFactory().setValue(60L);

        usersDataEntry.add(usernameLabel, 1, 1);
        usersDataEntry.add(userName, 2, 1);

        usersDataEntry.add(groupsLabel, 4, 1);
        usersDataEntry.add(userWindowGroups, 5, 1, 2, 1);

        usersDataEntry.add(logOffTimeLabel, 1, 2);
        usersDataEntry.add(logOffTime, 2, 2);

        usersDataEntry.add(isAutoLogOff, 5, 2, 2, 1);

        usersDataEntry.add(passwordLabel, 1, 3);
        usersDataEntry.add(Password, 2, 3, 6, 1);

        //table configuration
        UsernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
        groupColumn.setCellValueFactory(new PropertyValueFactory<>("Group"));
        autoLogOffColumn.setCellValueFactory(new PropertyValueFactory<>("AutoLogOff"));
        logOffTimeColumn.setCellValueFactory(new PropertyValueFactory<>("LogOffTime"));

        table.getColumns().addAll(UsernameColumn, groupColumn, autoLogOffColumn, logOffTimeColumn);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.prefHeightProperty().bind(usersBorderPane.heightProperty().subtract(usersControlAreaPane.heightProperty()));

        //panes
        usersToolbar.getItems().addAll(createUser, new Separator(), deleteUser);
        usersToolbar.setPadding(new Insets(5));

        usersControlAreaPane.getChildren().addAll(usersDataEntry, usersToolbar);
        usersControlAreaPane.setAlignment(Pos.CENTER);

        usersBorderPane.getChildren().addAll(usersControlAreaPane, table);
        usersBorderPane.setPadding(new Insets(10));
        usersBorderPane.setSpacing(10);

    }
    private void groupsGraphicsBuilder() {

        Label groupLabel = new Label("Group Name");
        groupLabel.setMinWidth(150);

        Label groupDescLabel = new Label("Group Description");
        groupDescLabel.setMinWidth(150);

        groupMainField.setPrefWidth(250);

        descMainField.setPrefWidth(250);

        GridPane GroupPane = new GridPane();
        GroupPane.setHgap(5);
        GroupPane.setVgap(5);
        GroupPane.add(groupLabel, 1, 1);
        GroupPane.add(groupMainField, 2, 1);
        GroupPane.add(groupDescLabel, 1, 2);
        GroupPane.add(descMainField, 2, 2);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(5));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(GroupPane);

        groupsDataEntry.getChildren().add(vbox);

        groupsToolbar.getItems().addAll(createGroup, deleteGroup);
        groupsToolbar.setPadding(new Insets(5));

        groupsControlAreaPane.getChildren().addAll(groupsDataEntry, groupsToolbar);
        groupsControlAreaPane.setAlignment(Pos.CENTER);

        scrollPane.setContent(groupsPaneTable);

        groupsPaneTable.setPadding(new Insets(20));
        groupsPaneTable.setAlignment(Pos.CENTER);

        groupsBorderPane.setTop(groupsControlAreaPane);
        groupsBorderPane.setCenter(scrollPane);
    }

    //action handling
    private void actionHandling() {
        groupsActionHandling();
        usersActionHandling();
    }
    private void groupsActionHandling() {
        createGroup.setOnMouseClicked(action -> {
            if (!userAuthorizationService.checkIfGroupExist(groupMainField.getText())) {
                userAuthorizationService.createGroup(groupMainField.getText(), descMainField.getText());
                update();
            }

        });
        deleteGroup.setOnMouseClicked(action -> {
            setToDelete.forEach(userAuthorizationService::deleteGroup);
            update();
        });
    }
    private void usersActionHandling() {
        table.setOnMouseClicked( (a) -> {
            final ObservableList<User> selectedItems = table.getSelectionModel().getSelectedItems();
            if (selectedItems.size() > 0){
                final User user = selectedItems.get(0);

                userName.setText(user.getUserName());
                userWindowGroups.getSelectionModel().select(user.getGroup());
                Password.setText(user.getPassword());
                isAutoLogOff.setSelected(user.isAutoLogOff());
            }
        });
        createUser.setOnMouseClicked(action -> {
            userAuthorizationService.createUser(new User(userName.getText(), Password.getText(), isAutoLogOff.isSelected(), logOffTime.getValue(), userWindowGroups.getSelectionModel().getSelectedItem().toString()));
            update();
        });
        deleteUser.setOnMouseClicked(action -> userAuthorizationService.deleteUser(new User(userName.getText())));

    }

    //functions
    public void createGroupEditor() {

        setCursor(Cursor.WAIT);

        LinkedHashSet<String> groupsSet = new LinkedHashSet<>();
        HashMap<String, String> descriptionsSet = new HashMap<>();

        groupsvbox.setSpacing(2);
        groupsvbox.setPadding(new Insets(1));
        Label labelGroup = new Label("Group name");
        labelGroup.setPrefSize(150, 65);
        labelGroup.setAlignment(Pos.CENTER);
        labelGroup.setStyle("-fx-background-color: #003399;-fx-text-fill:white;");
        groupsvbox.setAlignment(Pos.TOP_CENTER);

        descrepvbox.setSpacing(2);
        descrepvbox.setPadding(new Insets(1));
        descrepvbox.setAlignment(Pos.TOP_CENTER);
        Label labeld = new Label("Descreption");
        labeld.setPrefSize(200, 65);
        labeld.setAlignment(Pos.CENTER);
        labeld.setStyle("-fx-background-color: #003399;"
                + "-fx-text-fill:white;");
        descrepvbox.setAlignment(Pos.TOP_CENTER);

        groupsPaneTable.setSpacing(5);
        groupsPaneTable.getChildren().add(groupsvbox);
        groupsPaneTable.getChildren().add(descrepvbox);

        userAuthorizationService
                .getAllGroupsInStructuredForm()
                .getList()
                .forEach(windowGroup -> {
                    //all groups for all windows
                    windowGroup
                            .getRowGroup()
                            .forEach((window, groups) -> {
                                //window and its groups
                                VBox windowVBox = new VBox();
                                windowVBox.setSpacing(2);
                                windowVBox.setAlignment(Pos.TOP_CENTER);
                                Label label = new Label(window);
                                label.setAlignment(Pos.CENTER);
                                label.setStyle("-fx-background-color: #003399;-fx-text-fill:white;");
                                label.setPrefHeight(65);
                                label.prefWidthProperty().bind(windowVBox.widthProperty());
                                windowVBox.getChildren().add(label);
                                groupsPaneTable.getChildren().add(windowVBox);
                                groups.forEach(group -> {
                                            HBox rolesBox = new HBox();
                                            rolesBox.setPrefHeight(65);
                                            rolesBox.setSpacing(10);
                                            rolesBox.setPadding(new Insets(10));
                                            rolesBox.setBackground(new Background(new BackgroundFill(Color.valueOf("#0099cc"), CornerRadii.EMPTY, Insets.EMPTY)));
                                            windowVBox.getChildren().add(rolesBox);
                                            groupsSet.add(group.getGroup());
                                            descriptionsSet.put(group.getGroup(), group.getDescription());
                                            //all groups for that window
                                            group.getRolesStatus().forEach((role, status) -> {
                                                //roles for that group for that window
                                                VBox rolesVBox = new VBox();
                                                rolesVBox.setSpacing(4);
                                                rolesVBox.setPadding(new Insets(2));
                                                rolesVBox.setAlignment(Pos.TOP_CENTER);
                                                Label roleLabel = new Label(role.name());
                                                CheckBox check = new CheckBox();
                                                check.setFont(Font.font(12));
                                                check.setSelected(status);
                                                rolesVBox.getChildren().addAll(roleLabel, check);
                                                rolesBox.getChildren().add(rolesVBox);
                                                check.selectedProperty().addListener((observable, oldValue, newValue) -> userAuthorizationService.updateGroupRole(group.getGroup(), window, role.name(), newValue));
                                            });
                                        });
                            });
                });

        if (groupsSet.size() > 0) {
            groupsvbox.getChildren().clear();
            groupsvbox.getChildren().add(labelGroup);
            descrepvbox.getChildren().clear();
            descrepvbox.getChildren().add(labeld);
        } else {
            groupsvbox.getChildren().clear();
            descrepvbox.getChildren().clear();
        }

        groupsSet.forEach(group -> {
            Label label = new Label(group);
            label.setPrefSize(150, 65);
            label.setAlignment(Pos.CENTER);
            label.setFont(Font.font(13));
            label.setStyle("-fx-text-fill:white;");
            label.setBackground(new Background(new BackgroundFill(Color.valueOf("#003399"), CornerRadii.EMPTY, Insets.EMPTY)));
            label.setOnMouseClicked(action -> {
                if (label.getBackground().equals(new Background(new BackgroundFill(Color.valueOf("#003399"), CornerRadii.EMPTY, Insets.EMPTY)))) {
                    label.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                    setToDelete.add(label.getText());
                } else if (label.getBackground().equals(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)))) {
                    label.setBackground(new Background(new BackgroundFill(Color.valueOf("#003399"), CornerRadii.EMPTY, Insets.EMPTY)));
                    setToDelete.remove(label.getText());
                }

            });
            groupsvbox.getChildren().add(label);
        });
        groupsSet.forEach(group -> {
            TextField label = new TextField(descriptionsSet.get(group));
            label.setPrefSize(200, 65);
            label.setAlignment(Pos.CENTER);
            label.setFont(Font.font(13));
            label.setStyle("-fx-text-fill:white;"
                    + "-fx-background-color: #003399;"
                    + "-fx-border-width:0 0 0 0;"
                    + "-fx-background-radius:0;");
            label.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    userAuthorizationService.updateGroupDescription(group, label.getText());
                }
            });
            descrepvbox.getChildren().add(label);
        });

        setCursor(Cursor.DEFAULT);
    }

    public void update() {
        groupsPaneTable.getChildren().clear();
        createGroupEditor();

        userWindowGroups.setItems(FXCollections.observableArrayList(userAuthorizationService.getAllGroups()));

        list.clear();
        list.addAll(userAuthorizationService.getAllUsers());
        table.setItems(list);
    }

    @Override
    public String toString() {
        return "Users Window";
    }
}
