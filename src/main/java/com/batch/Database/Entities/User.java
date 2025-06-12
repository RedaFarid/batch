package com.batch.Database.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("AuthorizationUsers")
public class User {
    @Id
    private Long id;
    @Column("UserName")
    private String userName;
    @Column("Password")
    private String password;
    @Column("AutoLogOff")
    private boolean AutoLogOff;
    @Column("LoggOfTime")
    private long LogOffTime;
    @Column("GroupName")
    private String Group;

    public User(String userName, String password, boolean autoLogOff, long logOffTime, String group) {
        this.userName = userName;
        this.password = password;
        this.AutoLogOff = autoLogOff;
        this.LogOffTime = logOffTime;
        this.Group = group;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(String userName) {
        this.userName = userName;
    }

    public User(final Long id, final String userName, final String password, final boolean AutoLogOff, final long LogOffTime, final String Group) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.AutoLogOff = AutoLogOff;
        this.LogOffTime = LogOffTime;
        this.Group = Group;
    }

    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public boolean isAutoLogOff() {
        return this.AutoLogOff;
    }

    public void setAutoLogOff(final boolean AutoLogOff) {
        this.AutoLogOff = AutoLogOff;
    }

    public long getLogOffTime() {
        return this.LogOffTime;
    }

    public void setLogOffTime(final long LogOffTime) {
        this.LogOffTime = LogOffTime;
    }

    public String getGroup() {
        return this.Group;
    }

    public void setGroup(final String Group) {
        this.Group = Group;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof User other)) {
            return false;
        } else {
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isAutoLogOff() != other.isAutoLogOff()) {
                return false;
            } else if (this.getLogOffTime() != other.getLogOffTime()) {
                return false;
            } else {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
                    return false;
                }

                Object this$userName = this.getUserName();
                Object other$userName = other.getUserName();
                if (this$userName == null) {
                    if (other$userName != null) {
                        return false;
                    }
                } else if (!this$userName.equals(other$userName)) {
                    return false;
                }

                Object this$password = this.getPassword();
                Object other$password = other.getPassword();
                if (this$password == null) {
                    if (other$password != null) {
                        return false;
                    }
                } else if (!this$password.equals(other$password)) {
                    return false;
                }

                Object this$Group = this.getGroup();
                Object other$Group = other.getGroup();
                if (this$Group == null) {
                    return other$Group == null;
                } else return this$Group.equals(other$Group);
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isAutoLogOff() ? 79 : 97);
        long $LogOffTime = this.getLogOffTime();
        result = result * 59 + (int) ($LogOffTime >>> 32 ^ $LogOffTime);
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $userName = this.getUserName();
        result = result * 59 + ($userName == null ? 43 : $userName.hashCode());
        Object $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        Object $Group = this.getGroup();
        result = result * 59 + ($Group == null ? 43 : $Group.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getId();
        return "User(id=" + var10000 + ", userName=" + this.getUserName() + ", password=" + this.getPassword() + ", AutoLogOff=" + this.isAutoLogOff() + ", LogOffTime=" + this.getLogOffTime() + ", Group=" + this.getGroup() + ")";
    }
}
