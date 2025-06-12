

package com.batch.Services.UserAdministration;

import java.util.ArrayList;
import java.util.List;

public class AllGroupsDTO {
    private List<WindowGroupsDTO> list = new ArrayList();

    public List<WindowGroupsDTO> getList() {
        return this.list;
    }

    public void setList(final List<WindowGroupsDTO> list) {
        this.list = list;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AllGroupsDTO)) {
            return false;
        } else {
            AllGroupsDTO other = (AllGroupsDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$list = this.getList();
                Object other$list = other.getList();
                if (this$list == null) {
                    if (other$list != null) {
                        return false;
                    }
                } else if (!this$list.equals(other$list)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AllGroupsDTO;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $list = this.getList();
        result = result * 59 + ($list == null ? 43 : $list.hashCode());
        return result;
    }

    public String toString() {
        return "AllGroupsDTO(list=" + this.getList() + ")";
    }

    public AllGroupsDTO(final List<WindowGroupsDTO> list) {
        this.list = list;
    }

    public AllGroupsDTO() {
    }
}
