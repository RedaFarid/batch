package com.batch.Database.Services;

import com.batch.Database.Entities.Group;
import com.batch.Database.Entities.User;
import com.batch.Database.Repositories.GroupRepository;
import com.batch.Database.Repositories.UserRepository;
import com.batch.Utilities.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDaoService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public boolean isUserExist(User currentUser) {
        return userRepository.existsByUserName(currentUser.getUserName());
    }

    public void deleteGroup(String group) {
        groupRepository.deleteByGroup(group);
    }

    public User getUserByID(String userName) {
        return userRepository.findByUserName(userName);
    }

    public List<Group> GetAllGroups() {
        return groupRepository.findAll().stream()
                .peek(item -> {
                    final LinkedHashMap<Roles, Boolean> rolesStatus = item.getRolesStatus();
                    final boolean deleting = item.isDeleting();
                    final boolean editing = item.isEditing();
                    final boolean monitoring = item.isMonitoring();
                    final boolean updating = item.isUpdating();

                    rolesStatus.put(Roles.Deleting, deleting);
                    rolesStatus.put(Roles.Updating, updating);
                    rolesStatus.put(Roles.Editing, editing);
                    rolesStatus.put(Roles.Monitoring, monitoring);
                }).collect(Collectors.toList());
    }

    public List<User> GetAllUsers() {
        return userRepository.findAll();
    }

    public void updateGroupDescByData(String group, String desc) {
        groupRepository.updateDescriptionByGroup(group, desc);
    }

    public void deleteUser(String userName) {
        userRepository.deleteByUserName(userName);
    }

    public boolean isGroupExists(String group) {
        return groupRepository.existsByGroup(group);
    }

    public void saveGroup(Group tempGroup) {
        final LinkedHashMap<Roles, Boolean> rolesStatus = tempGroup.getRolesStatus();
        final Boolean deleting = rolesStatus.get(Roles.Deleting);
        final Boolean updating = rolesStatus.get(Roles.Updating);
        final Boolean editing = rolesStatus.get(Roles.Editing);
        final Boolean monitoring = rolesStatus.get(Roles.Monitoring);
        tempGroup.setDeleting(deleting);
        tempGroup.setUpdating(updating);
        tempGroup.setEditing(editing);
        tempGroup.setMonitoring(monitoring);
        groupRepository.save(tempGroup);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
