
package com.batch.Database.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("RecipeTreeItemsData")
public class TreeViewItemsData {
    @Id
    private Long id;
    private String Name;
    private Long ParentID;
    @Column("Type")
    private String ItemType;
    private long RecipeID;

    public TreeViewItemsData(String Name, Long ParentID, String ItemType, long RecipeID) {
        this.Name = Name;
        this.ParentID = ParentID;
        this.ItemType = ItemType;
        this.RecipeID = RecipeID;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.Name;
    }

    public Long getParentID() {
        return this.ParentID;
    }

    public String getItemType() {
        return this.ItemType;
    }

    public long getRecipeID() {
        return this.RecipeID;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setName(final String Name) {
        this.Name = Name;
    }

    public void setParentID(final Long ParentID) {
        this.ParentID = ParentID;
    }

    public void setItemType(final String ItemType) {
        this.ItemType = ItemType;
    }

    public void setRecipeID(final long RecipeID) {
        this.RecipeID = RecipeID;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof TreeViewItemsData)) {
            return false;
        } else {
            TreeViewItemsData other = (TreeViewItemsData)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.getRecipeID() != other.getRecipeID()) {
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

                Object this$ParentID = this.getParentID();
                Object other$ParentID = other.getParentID();
                if (this$ParentID == null) {
                    if (other$ParentID != null) {
                        return false;
                    }
                } else if (!this$ParentID.equals(other$ParentID)) {
                    return false;
                }

                Object this$Name = this.getName();
                Object other$Name = other.getName();
                if (this$Name == null) {
                    if (other$Name != null) {
                        return false;
                    }
                } else if (!this$Name.equals(other$Name)) {
                    return false;
                }

                Object this$ItemType = this.getItemType();
                Object other$ItemType = other.getItemType();
                if (this$ItemType == null) {
                    if (other$ItemType != null) {
                        return false;
                    }
                } else if (!this$ItemType.equals(other$ItemType)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TreeViewItemsData;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $RecipeID = this.getRecipeID();
        result = result * 59 + (int)($RecipeID >>> 32 ^ $RecipeID);
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $ParentID = this.getParentID();
        result = result * 59 + ($ParentID == null ? 43 : $ParentID.hashCode());
        Object $Name = this.getName();
        result = result * 59 + ($Name == null ? 43 : $Name.hashCode());
        Object $ItemType = this.getItemType();
        result = result * 59 + ($ItemType == null ? 43 : $ItemType.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getId();
        return "TreeViewItemsData(id=" + var10000 + ", Name=" + this.getName() + ", ParentID=" + this.getParentID() + ", ItemType=" + this.getItemType() + ", RecipeID=" + this.getRecipeID() + ")";
    }

    public TreeViewItemsData(final Long id, final String Name, final Long ParentID, final String ItemType, final long RecipeID) {
        this.id = id;
        this.Name = Name;
        this.ParentID = ParentID;
        this.ItemType = ItemType;
        this.RecipeID = RecipeID;
    }

    public TreeViewItemsData() {
    }
}
