
package com.batch.Database.Entities;

import com.batch.Utilities.Roles;
import java.util.LinkedHashMap;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("AuthorizationGroups")
public class Group {
    @Id
    private Long id;
    @Column("GroupName")
    private String group;
    @Column("Descreption")
    private String description;
    @Column("Window")
    private String window;
    @Column("Monitoring")
    private boolean monitoring;
    @Column("Editing")
    private boolean editing;
    @Column("Deleting")
    private boolean deleting;
    @Column("Updating")
    private boolean updating;
    @Transient
    private LinkedHashMap<Roles, Boolean> RolesStatus = new LinkedHashMap();

    public Long getId() {
        return this.id;
    }

    public String getGroup() {
        return this.group;
    }

    public String getDescription() {
        return this.description;
    }

    public String getWindow() {
        return this.window;
    }

    public boolean isMonitoring() {
        return this.monitoring;
    }

    public boolean isEditing() {
        return this.editing;
    }

    public boolean isDeleting() {
        return this.deleting;
    }

    public boolean isUpdating() {
        return this.updating;
    }

    public LinkedHashMap<Roles, Boolean> getRolesStatus() {
        return this.RolesStatus;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setWindow(final String window) {
        this.window = window;
    }

    public void setMonitoring(final boolean monitoring) {
        this.monitoring = monitoring;
    }

    public void setEditing(final boolean editing) {
        this.editing = editing;
    }

    public void setDeleting(final boolean deleting) {
        this.deleting = deleting;
    }

    public void setUpdating(final boolean updating) {
        this.updating = updating;
    }

    public void setRolesStatus(final LinkedHashMap<Roles, Boolean> RolesStatus) {
        this.RolesStatus = RolesStatus;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Group)) {
            return false;
        } else {
            Group other = (Group)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isMonitoring() != other.isMonitoring()) {
                return false;
            } else if (this.isEditing() != other.isEditing()) {
                return false;
            } else if (this.isDeleting() != other.isDeleting()) {
                return false;
            } else if (this.isUpdating() != other.isUpdating()) {
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

                Object this$group = this.getGroup();
                Object other$group = other.getGroup();
                if (this$group == null) {
                    if (other$group != null) {
                        return false;
                    }
                } else if (!this$group.equals(other$group)) {
                    return false;
                }

                Object this$description = this.getDescription();
                Object other$description = other.getDescription();
                if (this$description == null) {
                    if (other$description != null) {
                        return false;
                    }
                } else if (!this$description.equals(other$description)) {
                    return false;
                }

                Object this$window = this.getWindow();
                Object other$window = other.getWindow();
                if (this$window == null) {
                    if (other$window != null) {
                        return false;
                    }
                } else if (!this$window.equals(other$window)) {
                    return false;
                }

                Object this$RolesStatus = this.getRolesStatus();
                Object other$RolesStatus = other.getRolesStatus();
                if (this$RolesStatus == null) {
                    if (other$RolesStatus != null) {
                        return false;
                    }
                } else if (!this$RolesStatus.equals(other$RolesStatus)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Group;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isMonitoring() ? 79 : 97);
        result = result * 59 + (this.isEditing() ? 79 : 97);
        result = result * 59 + (this.isDeleting() ? 79 : 97);
        result = result * 59 + (this.isUpdating() ? 79 : 97);
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $group = this.getGroup();
        result = result * 59 + ($group == null ? 43 : $group.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Object $window = this.getWindow();
        result = result * 59 + ($window == null ? 43 : $window.hashCode());
        Object $RolesStatus = this.getRolesStatus();
        result = result * 59 + ($RolesStatus == null ? 43 : $RolesStatus.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getId();
        return "Group(id=" + var10000 + ", group=" + this.getGroup() + ", description=" + this.getDescription() + ", window=" + this.getWindow() + ", monitoring=" + this.isMonitoring() + ", editing=" + this.isEditing() + ", deleting=" + this.isDeleting() + ", updating=" + this.isUpdating() + ", RolesStatus=" + this.getRolesStatus() + ")";
    }

    public Group(final Long id, final String group, final String description, final String window, final boolean monitoring, final boolean editing, final boolean deleting, final boolean updating, final LinkedHashMap<Roles, Boolean> RolesStatus) {
        this.id = id;
        this.group = group;
        this.description = description;
        this.window = window;
        this.monitoring = monitoring;
        this.editing = editing;
        this.deleting = deleting;
        this.updating = updating;
        this.RolesStatus = RolesStatus;
    }

    public Group() {
    }
}
