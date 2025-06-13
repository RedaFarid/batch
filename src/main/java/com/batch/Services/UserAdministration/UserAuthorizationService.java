package com.batch.Services.UserAdministration;

import com.batch.ApplicationContext;
import com.batch.Database.Entities.Group;
import com.batch.Database.Entities.User;
import com.batch.Database.Services.UserDaoService;
import com.batch.GUI.UserAdministration.LoginWindow;
import com.batch.Utilities.HashingAlgorithm;
import com.batch.Utilities.Roles;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserAuthorizationService {
    private static ConfigurableApplicationContext configurableApplicationContext;
    private final List<WindowData> windows = new LinkedList();
    private final BooleanProperty isThereUserLoggedIn = new SimpleBooleanProperty();
    private final BooleanProperty requestForLogin = new SimpleBooleanProperty();
    private final BooleanProperty requestForLogOff = new SimpleBooleanProperty();
    private final BooleanProperty userTimeOut = new SimpleBooleanProperty();
    private final UserDaoService userDaoService;
    private User currentUser = new User("", "", true, 0L, "");
    private long setPointDuration = 0L;
    private long elapsedTime = 0L;

    public UserAuthorizationService(final UserDaoService userDaoService) {
        this.userDaoService = userDaoService;
    }

    public void runService() {
        this.requestForLogin.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.isThereUserLoggedIn.setValue(false);
                this.isThereUserLoggedIn.setValue(this.getReturnOfLoginWindow(5));
                this.requestForLogin.setValue(false);
            }

        });
        this.requestForLogOff.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.isThereUserLoggedIn.setValue(false);
                this.requestForLogOff.setValue(false);
            }

        });
        this.isThereUserLoggedIn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.startNotification(true);
                if (this.currentUser.isAutoLogOff()) {
                    this.timeOutCalculation(this.currentUser.getLogOffTime() * 1000L);
                }
            } else {
                this.currentUser.setUserName("");
                this.currentUser.setGroup("");
                this.startNotification(false);
            }

        });
        this.userTimeOut.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.isThereUserLoggedIn.setValue(false);
                this.userTimeOut.setValue(false);
            }

        });
    }

    public void requestLogIn() {
        this.requestForLogin.setValue(true);
    }

    public void requestLogOff() {
        this.requestForLogOff.setValue(true);
    }

    private boolean getReturnOfLoginWindow(int trialNumber) {
        if (trialNumber > 0 && trialNumber < 10) {
            LoginWindow.GetInstance().showAndReturnUser().ifPresent((user) -> this.currentUser = user);
            if (this.currentUser.getUserName().equals("")) {
                return false;
            } else if (this.userDaoService.isUserExist(this.currentUser)) {
                try {
                    User userDB = this.userDaoService.getUserByID(this.currentUser.getUserName());
                    boolean x = HashingAlgorithm.validatePassword(this.currentUser.getPassword(), userDB.getPassword());
                    if (x) {
                        this.currentUser = userDB;
                        return true;
                    } else {
                        return this.getReturnOfLoginWindow(trialNumber - 1);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return this.getReturnOfLoginWindow(trialNumber - 1);
                }
            } else {
                return this.getReturnOfLoginWindow(trialNumber - 1);
            }
        } else {
            return false;
        }
    }

    private void timeOutCalculation(long duration) {
        this.userTimeOut.setValue(false);
        this.setPointDuration = duration;
        this.elapsedTime = 0L;
    }

    public AllGroupsDTO getAllGroupsInStructuredForm() {
        AllGroupsDTO allGroups = new AllGroupsDTO();
        List<Group> list = this.userDaoService.GetAllGroups();
        List<String> distinctWindows = list.stream().map(Group::getWindow).distinct().collect(Collectors.toList());
        distinctWindows.forEach((window) -> {
            List<Group> groupListTemp = new ArrayList();
            Stream<Group> items = list.stream().filter((group) -> group.getWindow().equals(window));

            Objects.requireNonNull(groupListTemp);
            items.forEach(groupListTemp::add);

            WindowGroupsDTO windowGroup = new WindowGroupsDTO();
            windowGroup.getRowGroup().put(window, groupListTemp);
            allGroups.getList().add(windowGroup);
        });
        return allGroups;
    }

    public List<String> getAllGroups() {
        List<Group> list = this.userDaoService.GetAllGroups();
        return list.stream().map(Group::getGroup).distinct().collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return this.userDaoService.GetAllUsers();
    }

    public void updateGroupRole(String group, String window, String role, boolean roleStatus) {
        this.userDaoService.GetAllGroups().stream().filter((item) -> item.getGroup().equals(group)).filter((item) -> item.getWindow().equals(window)).findAny().ifPresent((groupEntity) -> {
            groupEntity.getRolesStatus().replace(Roles.valueOf(role), roleStatus);
            this.userDaoService.saveGroup(groupEntity);
        });
    }

    public void updateGroupDescription(String group, String desc) {
        this.userDaoService.updateGroupDescByData(group, desc);
    }

    public void updateUser(User user) {
    }

    public void deleteGroup(String Group) {
        if (!Group.equals("Administrators")) {
            this.userDaoService.deleteGroup(Group);
        }

    }

    public void deleteUser(User user) {
        if (!user.getUserName().equals("Administrator")) {
            this.userDaoService.deleteUser(user.getUserName());
        }

    }

    public boolean checkIfGroupExist(String Group) {
        return this.userDaoService.isGroupExists(Group);
    }

    public boolean checkIfUserExist(String User) {
        return this.userDaoService.isUserExist(new User(User, ""));
    }

    public void createGroup(String group, String Description) {
        this.windows.forEach((window) -> {
            try {
                Group tempGroup = new Group();
                tempGroup.setGroup(group);
                tempGroup.setDescription(Description);
                tempGroup.setWindow(window.toString());
                HashMap<Roles, Boolean> map = new HashMap();
                Stream.of(Roles.values()).forEach((role) -> map.put(role, Boolean.FALSE));
                tempGroup.getRolesStatus().putAll(map);
                this.userDaoService.saveGroup(tempGroup);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void createGroupForAdministrators(String group, String Description) {
        this.windows.forEach((window) -> {
            try {
                Group tempGroup = new Group();
                tempGroup.setGroup(group);
                tempGroup.setDescription(Description);
                tempGroup.setWindow(window.toString());
                HashMap<Roles, Boolean> map = new HashMap();
                Stream.of(Roles.values()).forEach((role) -> map.put(role, Boolean.TRUE));
                tempGroup.getRolesStatus().putAll(map);
                this.userDaoService.saveGroup(tempGroup);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void createUser(User user) {
        try {
            user.setPassword(HashingAlgorithm.StrongHash(user.getPassword()));
            this.userDaoService.saveUser(user);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(UserAuthorizationService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void checkAdministratorUser() {
        if (!this.checkIfUserExist("Administrator") || !this.checkIfGroupExist("Administrators")) {
            this.deleteGroup("Administrators");
            this.userDaoService.deleteUser("Administrator");
            this.createGroupForAdministrators("Administrators", "Highest Authority");
            this.createUser(new User("Administrator", "1", false, 0L, "Administrators"));
        }

    }

    public void setMainWindow(Stage mainWindow) {
        LoginWindow.GetInstance().setMainWindow(mainWindow);
    }

    @Scheduled(
            fixedDelay = 1000L,
            initialDelay = 2000L
    )
    public void run() {
        if (!this.currentUser.isAutoLogOff()) {
            this.setPointDuration = 0L;
            this.elapsedTime = 0L;
        }

        if (this.setPointDuration >= 10L) {
            this.elapsedTime += 1000L;
            if (this.elapsedTime > this.setPointDuration) {
                Platform.runLater(() -> {
                    this.userTimeOut.setValue(true);
                    this.setPointDuration = 0L;
                });
            }
        }

    }

    @EventListener
    public void arRefreshed(ApplicationReadyEvent event) {
    }

    @Async
    @EventListener
    public void atStart(ContextStartedEvent event) {
        configurableApplicationContext = ApplicationContext.applicationContext;
        this.startNotification(false);
        this.checkAdministratorUser();
        this.runService();
    }

    private void startNotification(boolean status) {
        UserEventMessage message = new UserEventMessage(status, this.currentUser, this.getAllGroupsInStructuredForm());
        configurableApplicationContext.publishEvent(new UserEvent(message));
    }

    public void registerWindow(WindowData windowData) {
        this.windows.add(windowData);
    }
}
