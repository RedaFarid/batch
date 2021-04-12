
package com.batch.GUI.RecipeEditor.WindowComponents;

import com.batch.Database.Entities.Recipe;
import com.google.common.io.Resources;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RecipeTreeItem extends TreeItemIdentity{
    private Recipe recipe;
    private TreeItemType itemType;
    private String itemValue;
    

    public RecipeTreeItem(String value, TreeItemType type) {
        itemType = type;
        this.itemValue = value;
        ImageView view = null;
        Image image;
        switch (type) {
            case Folder:
                image = new Image(Resources.getResource("Icons/Folder.png").toString());
                view = new ImageView(image);
                break;
            case Recipe:
                image = new Image(Resources.getResource("Icons/Recipe.png").toString());
                view = new ImageView(image);
                view.setFitHeight(20);
                view.setFitWidth(20);
                break;
            default:
                return;
        }
        super.setValue(value);
    super.setGraphic(view);
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public TreeItemType getItemType() {
        return itemType;
    }

    public void setItemType(TreeItemType itemType) {
        this.itemType = itemType;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }
    
}
