
package com.batch.GUI.UserAdministration;

import com.batch.Database.Entities.Group;
import com.batch.Database.Entities.User;
import com.batch.GUI.InitialWindow.InitialWindow;
import com.batch.Services.UserAdministration.UserAuthorizationService;
import com.batch.Services.UserAdministration.UserEvent;
import com.batch.Utilities.RestrictiveTextField;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
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
    private final TableView<User> table = new TableView();
    private final TableColumn<User, String> UsernameColumn = new TableColumn("User name");
    private final TableColumn<User, String> groupColumn = new TableColumn("Group");
    private final TableColumn<User, String> autoLogOffColumn = new TableColumn("Auto logoff");
    private final TableColumn<User, String> logOffTimeColumn = new TableColumn("Logoff time");
    private final Button createGroup = new Button("Create group");
    private final Button deleteGroup = new Button("Delete selected group");
    private final Button createUser = new Button("Create User");
    private final Button deleteUser = new Button("Delete User");
    private final RestrictiveTextField groupMainField = new RestrictiveTextField();
    private final RestrictiveTextField descMainField = new RestrictiveTextField();
    private final RestrictiveTextField userName = new RestrictiveTextField();
    private final PasswordField Password = new PasswordField();
    private final Spinner<Long> logOffTime = new Spinner();
    private final CheckBox isAutoLogOff = new CheckBox();
    private final ComboBox<String> userWindowGroups = new ComboBox();
    private final Label usernameLabel = new Label("User Name");
    private final Label passwordLabel = new Label("Password ");
    private final Label groupsLabel = new Label("Groups");
    private final Label logOffTimeLabel = new Label("Logg off time ");
    private LinkedHashSet<String> setToDelete = new LinkedHashSet();
    @Autowired(
            required = false
    )
    private InitialWindow window;

    @EventListener
    public void atStart(ContextStartedEvent event) {
        try {
            this.graphicsBuilder();
            this.actionHandling();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @EventListener
    public void newUserLogIn(UserEvent event) {
        Platform.runLater(this::update);
    }

    private void graphicsBuilder() {
        this.usersTab.setClosable(false);
        this.groupsTab.setClosable(false);
        this.usersTab.setContent(this.usersBorderPane);
        this.groupsTab.setContent(this.groupsBorderPane);
        this.getTabs().addAll(new Tab[]{this.usersTab, this.groupsTab});
        this.setPrefSize((double)1200.0F, (double)900.0F);
        this.groupsGraphicsBuilder();
        this.usersGraphicsBuilder();
        this.update();
    }

    private void usersGraphicsBuilder() {
        this.isAutoLogOff.setText("Is Automatic Logg Off");
        this.usersDataEntry.setVgap((double)10.0F);
        this.usersDataEntry.setHgap((double)10.0F);
        this.usersDataEntry.setPadding(new Insets((double)10.0F));
        this.usernameLabel.setPrefWidth((double)100.0F);
        this.groupsLabel.setPrefWidth((double)100.0F);
        this.logOffTimeLabel.setPrefWidth((double)100.0F);
        this.passwordLabel.setPrefWidth((double)100.0F);
        this.userName.setPrefWidth((double)200.0F);
        this.logOffTime.setPrefWidth((double)200.0F);
        this.isAutoLogOff.setFont(Font.font((double)15.0F));
        this.userWindowGroups.setMinWidth((double)250.0F);
        this.logOffTime.setValueFactory(new SpinnerValueFactory<Long>() {
            public void decrement(int steps) {
                this.setValue((Long)this.getValue() - 60L);
            }

            public void increment(int steps) {
                this.setValue((Long)this.getValue() + 60L);
            }
        });
        this.logOffTime.setEditable(true);
        this.logOffTime.getValueFactory().setValue(60L);
        this.usersDataEntry.add(this.usernameLabel, 1, 1);
        this.usersDataEntry.add(this.userName, 2, 1);
        this.usersDataEntry.add(this.groupsLabel, 4, 1);
        this.usersDataEntry.add(this.userWindowGroups, 5, 1, 2, 1);
        this.usersDataEntry.add(this.logOffTimeLabel, 1, 2);
        this.usersDataEntry.add(this.logOffTime, 2, 2);
        this.usersDataEntry.add(this.isAutoLogOff, 5, 2, 2, 1);
        this.usersDataEntry.add(this.passwordLabel, 1, 3);
        this.usersDataEntry.add(this.Password, 2, 3, 6, 1);
        this.UsernameColumn.setCellValueFactory(new PropertyValueFactory("userName"));
        this.groupColumn.setCellValueFactory(new PropertyValueFactory("Group"));
        this.autoLogOffColumn.setCellValueFactory(new PropertyValueFactory("AutoLogOff"));
        this.logOffTimeColumn.setCellValueFactory(new PropertyValueFactory("LogOffTime"));
        this.table.getColumns().addAll(new TableColumn[]{this.UsernameColumn, this.groupColumn, this.autoLogOffColumn, this.logOffTimeColumn});
        this.table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.table.prefHeightProperty().bind(this.usersBorderPane.heightProperty().subtract(this.usersControlAreaPane.heightProperty()));
        this.usersToolbar.getItems().addAll(new Node[]{this.createUser, new Separator(), this.deleteUser});
        this.usersToolbar.setPadding(new Insets((double)5.0F));
        this.usersControlAreaPane.getChildren().addAll(new Node[]{this.usersDataEntry, this.usersToolbar});
        this.usersControlAreaPane.setAlignment(Pos.CENTER);
        this.usersBorderPane.getChildren().addAll(new Node[]{this.usersControlAreaPane, this.table});
        this.usersBorderPane.setPadding(new Insets((double)10.0F));
        this.usersBorderPane.setSpacing((double)10.0F);
    }

    private void groupsGraphicsBuilder() {
        Label groupLabel = new Label("Group Name");
        groupLabel.setMinWidth((double)150.0F);
        Label groupDescLabel = new Label("Group Description");
        groupDescLabel.setMinWidth((double)150.0F);
        this.groupMainField.setPrefWidth((double)250.0F);
        this.descMainField.setPrefWidth((double)250.0F);
        GridPane GroupPane = new GridPane();
        GroupPane.setHgap((double)5.0F);
        GroupPane.setVgap((double)5.0F);
        GroupPane.add(groupLabel, 1, 1);
        GroupPane.add(this.groupMainField, 2, 1);
        GroupPane.add(groupDescLabel, 1, 2);
        GroupPane.add(this.descMainField, 2, 2);
        VBox vbox = new VBox();
        vbox.setSpacing((double)5.0F);
        vbox.setPadding(new Insets((double)5.0F));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(GroupPane);
        this.groupsDataEntry.getChildren().add(vbox);
        this.groupsToolbar.getItems().addAll(new Node[]{this.createGroup, this.deleteGroup});
        this.groupsToolbar.setPadding(new Insets((double)5.0F));
        this.groupsControlAreaPane.getChildren().addAll(new Node[]{this.groupsDataEntry, this.groupsToolbar});
        this.groupsControlAreaPane.setAlignment(Pos.CENTER);
        this.scrollPane.setContent(this.groupsPaneTable);
        this.groupsPaneTable.setPadding(new Insets((double)20.0F));
        this.groupsPaneTable.setAlignment(Pos.CENTER);
        this.groupsBorderPane.setTop(this.groupsControlAreaPane);
        this.groupsBorderPane.setCenter(this.scrollPane);
    }

    private void actionHandling() {
        this.groupsActionHandling();
        this.usersActionHandling();
    }

    private void groupsActionHandling() {
        this.createGroup.setOnMouseClicked((action) -> {
            if (!this.userAuthorizationService.checkIfGroupExist(this.groupMainField.getText())) {
                this.userAuthorizationService.createGroup(this.groupMainField.getText(), this.descMainField.getText());
                this.update();
            }

        });
        this.deleteGroup.setOnMouseClicked((action) -> {
            LinkedHashSet<String> toBeDeleted = this.setToDelete;
            UserAuthorizationService authorizationService = this.userAuthorizationService;
            Objects.requireNonNull(authorizationService);
            toBeDeleted.forEach(authorizationService::deleteGroup);
            this.update();
        });
    }

    private void usersActionHandling() {
        this.table.setOnMouseClicked((a) -> {
            ObservableList<User> selectedItems = this.table.getSelectionModel().getSelectedItems();
            if (selectedItems.size() > 0) {
                User user = (User)selectedItems.get(0);
                this.userName.setText(user.getUserName());
                this.userWindowGroups.getSelectionModel().select(user.getGroup());
                this.Password.setText(user.getPassword());
                this.isAutoLogOff.setSelected(user.isAutoLogOff());
            }

        });
        this.createUser.setOnMouseClicked((action) -> {
            this.userAuthorizationService.createUser(new User(this.userName.getText(), this.Password.getText(), this.isAutoLogOff.isSelected(), (Long)this.logOffTime.getValue(), ((String)this.userWindowGroups.getSelectionModel().getSelectedItem()).toString()));
            this.update();
        });
        this.deleteUser.setOnMouseClicked((action) -> this.userAuthorizationService.deleteUser(new User(this.userName.getText())));
    }

    public void createGroupEditor() {
        this.setCursor(Cursor.WAIT);
        LinkedHashSet<String> groupsSet = new LinkedHashSet();
        HashMap<String, String> descriptionsSet = new HashMap();
        this.groupsvbox.setSpacing((double)2.0F);
        this.groupsvbox.setPadding(new Insets((double)1.0F));
        Label labelGroup = new Label("Group name");
        labelGroup.setPrefSize((double)150.0F, (double)65.0F);
        labelGroup.setAlignment(Pos.CENTER);
        labelGroup.setStyle("-fx-background-color: #003399;-fx-text-fill:white;");
        this.groupsvbox.setAlignment(Pos.TOP_CENTER);
        this.descrepvbox.setSpacing((double)2.0F);
        this.descrepvbox.setPadding(new Insets((double)1.0F));
        this.descrepvbox.setAlignment(Pos.TOP_CENTER);
        Label labeld = new Label("Descreption");
        labeld.setPrefSize((double)200.0F, (double)65.0F);
        labeld.setAlignment(Pos.CENTER);
        labeld.setStyle("-fx-background-color: #003399;-fx-text-fill:white;");
        this.descrepvbox.setAlignment(Pos.TOP_CENTER);
        this.groupsPaneTable.setSpacing((double)5.0F);
        this.groupsPaneTable.getChildren().add(this.groupsvbox);
        this.groupsPaneTable.getChildren().add(this.descrepvbox);
        this.userAuthorizationService.getAllGroupsInStructuredForm().getList().forEach((windowGroup) -> windowGroup.getRowGroup().forEach((window, groups) -> {
            VBox windowVBox = new VBox();
            windowVBox.setSpacing((double)2.0F);
            windowVBox.setAlignment(Pos.TOP_CENTER);
            Label label = new Label(window);
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-background-color: #003399;-fx-text-fill:white;");
            label.setPrefHeight((double)65.0F);
            label.prefWidthProperty().bind(windowVBox.widthProperty());
            windowVBox.getChildren().add(label);
            this.groupsPaneTable.getChildren().add(windowVBox);
            groups.forEach((group) -> {
                HBox rolesBox = new HBox();
                rolesBox.setPrefHeight((double)65.0F);
                rolesBox.setSpacing((double)10.0F);
                rolesBox.setPadding(new Insets((double)10.0F));
                rolesBox.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.valueOf("#0099cc"), CornerRadii.EMPTY, Insets.EMPTY)}));
                windowVBox.getChildren().add(rolesBox);
                groupsSet.add(group.getGroup());
                descriptionsSet.put(group.getGroup(), group.getDescription());
                group.getRolesStatus().forEach((role, status) -> {
                    VBox rolesVBox = new VBox();
                    rolesVBox.setSpacing((double)4.0F);
                    rolesVBox.setPadding(new Insets((double)2.0F));
                    rolesVBox.setAlignment(Pos.TOP_CENTER);
                    Label roleLabel = new Label(role.name());
                    CheckBox check = new CheckBox();
                    check.setFont(Font.font((double)12.0F));
                    check.setSelected(status);
                    rolesVBox.getChildren().addAll(new Node[]{roleLabel, check});
                    rolesBox.getChildren().add(rolesVBox);
                    check.selectedProperty().addListener((observable, oldValue, newValue) -> this.userAuthorizationService.updateGroupRole(group.getGroup(), window, role.name(), newValue));
                });
            });
        }));
        if (groupsSet.size() > 0) {
            this.groupsvbox.getChildren().clear();
            this.groupsvbox.getChildren().add(labelGroup);
            this.descrepvbox.getChildren().clear();
            this.descrepvbox.getChildren().add(labeld);
        } else {
            this.groupsvbox.getChildren().clear();
            this.descrepvbox.getChildren().clear();
        }

        groupsSet.forEach((group) -> {
            Label label = new Label(group);
            label.setPrefSize((double)150.0F, (double)65.0F);
            label.setAlignment(Pos.CENTER);
            label.setFont(Font.font((double)13.0F));
            label.setStyle("-fx-text-fill:white;");
            label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.valueOf("#003399"), CornerRadii.EMPTY, Insets.EMPTY)}));
            label.setOnMouseClicked((action) -> {
                if (label.getBackground().equals(new Background(new BackgroundFill[]{new BackgroundFill(Color.valueOf("#003399"), CornerRadii.EMPTY, Insets.EMPTY)}))) {
                    label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)}));
                    this.setToDelete.add(label.getText());
                } else if (label.getBackground().equals(new Background(new BackgroundFill[]{new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)}))) {
                    label.setBackground(new Background(new BackgroundFill[]{new BackgroundFill(Color.valueOf("#003399"), CornerRadii.EMPTY, Insets.EMPTY)}));
                    this.setToDelete.remove(label.getText());
                }

            });
            this.groupsvbox.getChildren().add(label);
        });
        groupsSet.forEach((group) -> {
            TextField label = new TextField((String)descriptionsSet.get(group));
            label.setPrefSize((double)200.0F, (double)65.0F);
            label.setAlignment(Pos.CENTER);
            label.setFont(Font.font((double)13.0F));
            label.setStyle("-fx-text-fill:white;-fx-background-color: #003399;-fx-border-width:0 0 0 0;-fx-background-radius:0;");
            label.addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
                if (event.getCode() == KeyCode.ENTER) {
                    this.userAuthorizationService.updateGroupDescription(group, label.getText());
                }

            });
            this.descrepvbox.getChildren().add(label);
        });
        this.setCursor(Cursor.DEFAULT);
    }

    public void update() {
        this.groupsPaneTable.getChildren().clear();
        this.createGroupEditor();
        this.userWindowGroups.setItems(FXCollections.observableArrayList(this.userAuthorizationService.getAllGroups()));
        this.list.clear();
        this.list.addAll(this.userAuthorizationService.getAllUsers());
        this.table.setItems(this.list);
    }

    public String toString() {
        return "Users Window";
    }

    public UserAdministrationWindow(final UserAuthorizationService userAuthorizationService) {
        this.userAuthorizationService = userAuthorizationService;
    }
}
