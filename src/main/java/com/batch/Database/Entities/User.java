package com.batch.Database.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AuthorizationUsers")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(name = "UserName")
    private String userName ;
    @Column(name = "Password")
    private String password ;
    @Column(name = "AutoLogOff")
    private boolean AutoLogOff;
    @Column(name = "LoggOfTime")
    private long LogOffTime;
    @Column(name = "GroupName")
    private String Group;

    public User(String userName, String password, boolean autoLogOff, long logOffTime, String group) {
        this.userName = userName;
        this.password = password;
        AutoLogOff = autoLogOff;
        LogOffTime = logOffTime;
        Group = group;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(String userName) {
        this.userName = userName;
    }
}
