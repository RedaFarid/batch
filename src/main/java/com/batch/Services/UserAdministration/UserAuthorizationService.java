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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserAuthorizationService {

    private static ConfigurableApplicationContext configurableApplicationContext;
    private final List<WindowData> windows = new LinkedList<>();
    private User currentUser = new User("", "", true, 0, "");

    private final BooleanProperty isThereUserLoggedIn = new SimpleBooleanProperty();
    private final BooleanProperty requestForLogin = new SimpleBooleanProperty();
    private final BooleanProperty requestForLogOff = new SimpleBooleanProperty();
    private final BooleanProperty userTimeOut = new SimpleBooleanProperty();

    private long setPointDuration = 0;
    private long elapsedTime = 0;
    
    private final UserDaoService userDaoService;




    //main task
    public void runService() {
        //Initialization program
        requestForLogin.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                isThereUserLoggedIn.setValue(false);
                isThereUserLoggedIn.setValue(getReturnOfLoginWindow(5));
                requestForLogin.setValue(false);
            }
        });
        requestForLogOff.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                isThereUserLoggedIn.setValue(false);
                requestForLogOff.setValue(false);
            }
        });
        isThereUserLoggedIn.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                startNotification(true);
                if (currentUser.isAutoLogOff()) {
                    timeOutCalculation(currentUser.getLogOffTime() * 1000);
                }
            } else {
                currentUser.setUserName("");
                currentUser.setGroup("");
                startNotification(false);
            }
        });
        userTimeOut.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                isThereUserLoggedIn.setValue(false);
                userTimeOut.setValue(false);
            } else {
            }
        });
    }

    //request login window
    public void requestLogIn() {
        requestForLogin.setValue(true);
    }
    
    //request logoff 
    public void requestLogOff() {
        requestForLogOff.setValue(true);
    }

    //visualize login for trial numbers
    private boolean getReturnOfLoginWindow(int trialNumber) {
        if (trialNumber > 0 && trialNumber < 10) {
            LoginWindow.GetInstance().showAndReturnUser().ifPresent(user -> {
                currentUser = user;
            });
            if (currentUser.getUserName().equals("")) {
                return false;
            } else {
                if (userDaoService.isUserExist(currentUser)) {
                    try {
                        User userDB = userDaoService.getUserByID(currentUser.getUserName());
                        boolean x = HashingAlgorithm.validatePassword(currentUser.getPassword(), userDB.getPassword());
                        if (x) {
                            currentUser = userDB;
                            return true;
                        } else {
                            return getReturnOfLoginWindow(trialNumber - 1);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return getReturnOfLoginWindow(trialNumber - 1);
                    }
                } else {
                    return getReturnOfLoginWindow(trialNumber - 1);
                }
            }
        } else {
            return false;
        }
    }

    //login time out calculations
    private void timeOutCalculation(long duration) {
        userTimeOut.setValue(false);
        setPointDuration = duration;
        elapsedTime = 0;
    }

    //functions
    public AllGroupsDTO getAllGroupsInStructuredForm() {
        AllGroupsDTO allGroups = new AllGroupsDTO();

        List<Group> list = userDaoService.GetAllGroups();

        List<String> distinctWindows = list.stream().map(Group::getWindow).distinct().collect(Collectors.toList());
        distinctWindows.forEach(window -> {
            List<Group> groupListTemp = new ArrayList<>();
            list.stream().filter(group -> group.getWindow().equals(window)).forEach(groupListTemp::add);
            WindowGroupsDTO windowGroup = new WindowGroupsDTO();
            windowGroup.getRowGroup().put(window, groupListTemp);
            allGroups.getList().add(windowGroup);
        });
        return allGroups;
    }
    public List<String> getAllGroups() {
        List<Group> list = userDaoService.GetAllGroups();
        return list.stream().map(Group::getGroup).distinct().collect(Collectors.toList());
    }
    public List<User> getAllUsers() {
        return userDaoService.GetAllUsers();
    }
    public void updateGroupRole(String group, String window, String role, boolean roleStatus) {
        userDaoService.GetAllGroups().stream().filter(item -> item.getGroup().equals(group)).filter(item -> item.getWindow().equals(window))
                .findAny().ifPresent(groupEntity -> {
                    groupEntity.getRolesStatus().replace(Roles.valueOf(role), roleStatus);
                    userDaoService.saveGroup(groupEntity);
        });
    }
    public void updateGroupDescription(String group, String desc) {
        userDaoService.updateGroupDescByData(group, desc);
    }
    public void updateUser(User user) {

    }
    public void deleteGroup(String Group) {
        if (! Group.equals("Administrators")) {
            userDaoService.deleteGroup(Group);
        }
    }
    public void deleteUser(User user) {
        if (! user.getUserName().equals("Administrator")) {
            userDaoService.deleteUser(user.getUserName());
        }
    }
    public boolean checkIfGroupExist(String Group) {
        return userDaoService.isGroupExists(Group);
    }
    public boolean checkIfUserExist(String User) {
        return userDaoService.isUserExist(new User(User, ""));
    }
    public void createGroup(String group, String Description) {
        windows.forEach((WindowData window) -> {
            try {
                Group tempGroup = new Group();

                tempGroup.setGroup(group);
                tempGroup.setDescription(Description);
                tempGroup.setWindow(window.toString());

                HashMap<Roles, Boolean> map = new HashMap<>();
                Stream.of(Roles.values()).forEach(role -> map.put(role, Boolean.FALSE));
                tempGroup.getRolesStatus().putAll(map);

                userDaoService.saveGroup(tempGroup);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void createGroupForAdministrators(String group, String Description) {
        windows.forEach((WindowData window) -> {
            try {
                Group tempGroup = new Group();

                tempGroup.setGroup(group);
                tempGroup.setDescription(Description);
                tempGroup.setWindow(window.toString());

                HashMap<Roles, Boolean> map = new HashMap<>();
                Stream.of(Roles.values()).forEach(role -> map.put(role, Boolean.TRUE));
                tempGroup.getRolesStatus().putAll(map);

                userDaoService.saveGroup(tempGroup);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void createUser(User user) {
        try {
            user.setPassword(HashingAlgorithm.StrongHash(user.getPassword()));
            userDaoService.saveUser(user);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(UserAuthorizationService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void checkAdministratorUser(){
        if (!checkIfUserExist("Administrator") || !checkIfGroupExist("Administrators")) {
            deleteGroup("Administrators");
            userDaoService.deleteUser("Administrator");
            createGroupForAdministrators("Administrators", "Highest Authority");
            createUser(new User("Administrator", "1", false, 0, "Administrators"));
        }
    }
    public void setMainWindow(Stage mainWindow) {
        LoginWindow.GetInstance().setMainWindow(mainWindow);
    }

    @Scheduled(fixedDelay = 1000, initialDelay = 2000)
    public void run() {
        if (!currentUser.isAutoLogOff()) {
            setPointDuration = 0;
            elapsedTime = 0;
        }
        if (setPointDuration >= 10) {
            elapsedTime += 1000;
            if (elapsedTime > setPointDuration) {
                Platform.runLater(() -> {
                    userTimeOut.setValue(true);
                    setPointDuration = 0;
                });
            }
        }
    }

    @EventListener
    public void arRefreshed(ApplicationReadyEvent event){
    }

    @EventListener
    public void atStart(ContextStartedEvent event) {
        configurableApplicationContext = ApplicationContext.applicationContext;
        startNotification(false);
        checkAdministratorUser();
        runService();
    }

    //start window notifications
    private void startNotification(boolean status) {
        UserEventMessage message = new UserEventMessage(status, currentUser, getAllGroupsInStructuredForm());
        configurableApplicationContext.publishEvent(new UserEvent(message));
    }

    public void registerWindow(WindowData windowData){
        windows.add(windowData);
    }
}
