

package com.batch.Database.Entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("Log")
public class Log {
    @Id
    private long id;
    private String identifier = "";
    private String source = "";
    private String Event = "";
    @CreatedDate
    private LocalTime time;
    @CreatedDate
    private LocalDate Date;
    private String userName;
    @Column("GroupName")
    private String groupName;

    public Log(String identifier, String source, String event) {
        this.identifier = identifier;
        this.source = source;
        this.Event = event;
    }

    public Log(String identifier, String event) {
        this.identifier = identifier;
        this.Event = event;
    }

    public String toString() {
        return String.format("[%-7S]     %-15s     %-80S                 %-14S      %-10S ", this.identifier.toUpperCase(Locale.UK).trim(), this.source, this.Event, this.Date, this.time);
    }

    public long getId() {
        return this.id;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getSource() {
        return this.source;
    }

    public String getEvent() {
        return this.Event;
    }

    public LocalTime getTime() {
        return this.time;
    }

    public LocalDate getDate() {
        return this.Date;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public void setEvent(final String Event) {
        this.Event = Event;
    }

    public void setTime(final LocalTime time) {
        this.time = time;
    }

    public void setDate(final LocalDate Date) {
        this.Date = Date;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Log)) {
            return false;
        } else {
            Log other = (Log)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getId() != other.getId()) {
                return false;
            } else {
                Object this$identifier = this.getIdentifier();
                Object other$identifier = other.getIdentifier();
                if (this$identifier == null) {
                    if (other$identifier != null) {
                        return false;
                    }
                } else if (!this$identifier.equals(other$identifier)) {
                    return false;
                }

                Object this$source = this.getSource();
                Object other$source = other.getSource();
                if (this$source == null) {
                    if (other$source != null) {
                        return false;
                    }
                } else if (!this$source.equals(other$source)) {
                    return false;
                }

                Object this$Event = this.getEvent();
                Object other$Event = other.getEvent();
                if (this$Event == null) {
                    if (other$Event != null) {
                        return false;
                    }
                } else if (!this$Event.equals(other$Event)) {
                    return false;
                }

                Object this$time = this.getTime();
                Object other$time = other.getTime();
                if (this$time == null) {
                    if (other$time != null) {
                        return false;
                    }
                } else if (!this$time.equals(other$time)) {
                    return false;
                }

                Object this$Date = this.getDate();
                Object other$Date = other.getDate();
                if (this$Date == null) {
                    if (other$Date != null) {
                        return false;
                    }
                } else if (!this$Date.equals(other$Date)) {
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

                Object this$groupName = this.getGroupName();
                Object other$groupName = other.getGroupName();
                if (this$groupName == null) {
                    if (other$groupName != null) {
                        return false;
                    }
                } else if (!this$groupName.equals(other$groupName)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Log;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $id = this.getId();
        result = result * 59 + (int)($id >>> 32 ^ $id);
        Object $identifier = this.getIdentifier();
        result = result * 59 + ($identifier == null ? 43 : $identifier.hashCode());
        Object $source = this.getSource();
        result = result * 59 + ($source == null ? 43 : $source.hashCode());
        Object $Event = this.getEvent();
        result = result * 59 + ($Event == null ? 43 : $Event.hashCode());
        Object $time = this.getTime();
        result = result * 59 + ($time == null ? 43 : $time.hashCode());
        Object $Date = this.getDate();
        result = result * 59 + ($Date == null ? 43 : $Date.hashCode());
        Object $userName = this.getUserName();
        result = result * 59 + ($userName == null ? 43 : $userName.hashCode());
        Object $groupName = this.getGroupName();
        result = result * 59 + ($groupName == null ? 43 : $groupName.hashCode());
        return result;
    }

    public Log(final long id, final String identifier, final String source, final String Event, final LocalTime time, final LocalDate Date, final String userName, final String groupName) {
        this.id = id;
        this.identifier = identifier;
        this.source = source;
        this.Event = Event;
        this.time = time;
        this.Date = Date;
        this.userName = userName;
        this.groupName = groupName;
    }

    public Log() {
    }
}
