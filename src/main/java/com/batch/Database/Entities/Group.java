package com.batch.Database.Entities;

import com.batch.Utilities.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedHashMap;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AuthorizationGroups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(name = "GroupName")
    private String group;
    @Column(name = "Descreption")
    private String description;
    @Column(name = "Window")
    private String window;

    @Column(name = "Monitoring")
    private boolean monitoring;
    @Column(name = "Editing")
    private boolean editing;
    @Column(name = "Deleting")
    private boolean deleting;
    @Column(name = "Updating")
    private boolean updating;

    @Transient
    private LinkedHashMap<Roles, Boolean> RolesStatus = new LinkedHashMap<>();
}
