package com.batch.Services.UserAdministration;

import com.batch.Database.Entities.Group;

import java.util.LinkedHashMap;
import java.util.List;


public class WindowGroupsDTO {
    private LinkedHashMap<String, List<Group>> rowGroup = new LinkedHashMap<>();

    public WindowGroupsDTO() {
    }

    public WindowGroupsDTO(LinkedHashMap<String, List<Group>> rowGroup) {
        this.rowGroup = rowGroup;
    }

    
    public LinkedHashMap<String, List<Group>> getRowGroup() {
        return rowGroup;
    }

    public void setRowGroup(LinkedHashMap<String, List<Group>> rowGroup) {
        this.rowGroup = rowGroup;
    }
    
}
