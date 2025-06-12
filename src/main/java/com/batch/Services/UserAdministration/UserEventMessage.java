
package com.batch.Services.UserAdministration;

import com.batch.Database.Entities.User;

public class UserEventMessage {
    private boolean isLoggedOn;
    private User user;
    private AllGroupsDTO allGroupsDTO;

    public boolean isLoggedOn() {
        return this.isLoggedOn;
    }

    public User getUser() {
        return this.user;
    }

    public AllGroupsDTO getAllGroupsDTO() {
        return this.allGroupsDTO;
    }

    public void setLoggedOn(final boolean isLoggedOn) {
        this.isLoggedOn = isLoggedOn;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public void setAllGroupsDTO(final AllGroupsDTO allGroupsDTO) {
        this.allGroupsDTO = allGroupsDTO;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof UserEventMessage)) {
            return false;
        } else {
            UserEventMessage other = (UserEventMessage)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isLoggedOn() != other.isLoggedOn()) {
                return false;
            } else {
                Object this$user = this.getUser();
                Object other$user = other.getUser();
                if (this$user == null) {
                    if (other$user != null) {
                        return false;
                    }
                } else if (!this$user.equals(other$user)) {
                    return false;
                }

                Object this$allGroupsDTO = this.getAllGroupsDTO();
                Object other$allGroupsDTO = other.getAllGroupsDTO();
                if (this$allGroupsDTO == null) {
                    if (other$allGroupsDTO != null) {
                        return false;
                    }
                } else if (!this$allGroupsDTO.equals(other$allGroupsDTO)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserEventMessage;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isLoggedOn() ? 79 : 97);
        Object $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : $user.hashCode());
        Object $allGroupsDTO = this.getAllGroupsDTO();
        result = result * 59 + ($allGroupsDTO == null ? 43 : $allGroupsDTO.hashCode());
        return result;
    }

    public String toString() {
        boolean var10000 = this.isLoggedOn();
        return "UserEventMessage(isLoggedOn=" + var10000 + ", user=" + this.getUser() + ", allGroupsDTO=" + this.getAllGroupsDTO() + ")";
    }

    public UserEventMessage(final boolean isLoggedOn, final User user, final AllGroupsDTO allGroupsDTO) {
        this.isLoggedOn = isLoggedOn;
        this.user = user;
        this.allGroupsDTO = allGroupsDTO;
    }

    public UserEventMessage() {
    }
}
