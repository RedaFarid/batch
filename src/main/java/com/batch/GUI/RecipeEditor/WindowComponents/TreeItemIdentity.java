
package com.batch.GUI.RecipeEditor.WindowComponents;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;


public class TreeItemIdentity extends TreeItem<String> {
    
    private long itemParent;
    private int itemLevel;
    private long itemID;

    public TreeItemIdentity(long itemParent, int itemLevel, long itemID, String value, Node graphic) {
        super(value, graphic);
        this.itemParent = itemParent;
        this.itemLevel = itemLevel;
        this.itemID = itemID;
    }

    public TreeItemIdentity() {
    }
    

    public long getItemParent() {
        return itemParent;
    }

    public void setItemParent(long itemParent) {
        this.itemParent = itemParent;
    }

    public int getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(int itemLevel) {
        this.itemLevel = itemLevel;
    }

    public long getItemID() {
        return itemID;
    }

    public void setItemID(long itemID) {
        this.itemID = itemID;
    }
    
    
}
