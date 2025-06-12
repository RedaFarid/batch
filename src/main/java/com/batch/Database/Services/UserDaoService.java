package com.batch.Database.Services;

import com.batch.Database.Entities.Group;
import com.batch.Database.Entities.User;
import com.batch.Database.Repositories.GroupRepository;
import com.batch.Database.Repositories.UserRepository;
import com.batch.Utilities.Roles;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDaoService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public UserDaoService(final GroupRepository groupRepository, final UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public boolean isUserExist(User currentUser) {
        return this.userRepository.existsByUserName(currentUser.getUserName());
    }

    public void deleteGroup(String group) {
        this.groupRepository.deleteByGroup(group);
    }

    public User getUserByID(String userName) {
        return this.userRepository.findByUserName(userName);
    }

    public List<Group> GetAllGroups() {
        return Lists.newArrayList(this.groupRepository.findAll()).stream().peek((item) -> {
            LinkedHashMap<Roles, Boolean> rolesStatus = item.getRolesStatus();
            boolean deleting = item.isDeleting();
            boolean editing = item.isEditing();
            boolean monitoring = item.isMonitoring();
            boolean updating = item.isUpdating();
            rolesStatus.put(Roles.Deleting, deleting);
            rolesStatus.put(Roles.Updating, updating);
            rolesStatus.put(Roles.Editing, editing);
            rolesStatus.put(Roles.Monitoring, monitoring);
        }).collect(Collectors.toList());
    }

    public List<User> GetAllUsers() {
        return Lists.newArrayList(this.userRepository.findAll());
    }

    public void updateGroupDescByData(String group, String desc) {
        this.groupRepository.updateDescriptionByGroup(group, desc);
    }

    public void deleteUser(String userName) {
        this.userRepository.deleteByUserName(userName);
    }

    public boolean isGroupExists(String group) {
        return this.groupRepository.existsByGroup(group);
    }

    public void saveGroup(Group tempGroup) {
        LinkedHashMap<Roles, Boolean> rolesStatus = tempGroup.getRolesStatus();
        Boolean deleting = rolesStatus.get(Roles.Deleting);
        Boolean updating = rolesStatus.get(Roles.Updating);
        Boolean editing = rolesStatus.get(Roles.Editing);
        Boolean monitoring = rolesStatus.get(Roles.Monitoring);
        tempGroup.setDeleting(deleting);
        tempGroup.setUpdating(updating);
        tempGroup.setEditing(editing);
        tempGroup.setMonitoring(monitoring);
        this.groupRepository.save(tempGroup);
    }

    public void saveUser(User user) {
        this.userRepository.save(user);
    }
}
