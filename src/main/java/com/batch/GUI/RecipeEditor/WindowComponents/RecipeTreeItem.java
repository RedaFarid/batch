package com.batch.GUI.RecipeEditor.WindowComponents;

import com.batch.Database.Entities.Recipe;
import com.google.common.io.Resources;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class RecipeTreeItem extends TreeItemIdentity {
    private Recipe recipe;
    private TreeItemType itemType;
    private String itemValue;

    public RecipeTreeItem(String value, TreeItemType type) {
        this.itemType = type;
        this.itemValue = value;
        ImageView view = null;
        switch (type) {
            case Folder: {
                Image image = new Image(Resources.getResource("Icons/Folder.png").toString());
                view = new ImageView(image);
                break;
            }
            case Recipe: {
                Image image = new Image(Resources.getResource("Icons/Recipe.png").toString());
                view = new ImageView(image);
                view.setFitHeight(20.0F);
                view.setFitWidth(20.0F);
                break;
            }
            default:
                return;
        }

        super.setValue(value);
        super.setGraphic(view);
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public TreeItemType getItemType() {
        return this.itemType;
    }

    public void setItemType(TreeItemType itemType) {
        this.itemType = itemType;
    }

    public String getItemValue() {
        return this.itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }
}
