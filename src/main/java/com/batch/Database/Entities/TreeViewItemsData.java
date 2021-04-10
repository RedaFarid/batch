
package com.batch.Database.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "RecipeTreeItemsData")
public class TreeViewItemsData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String Name;
    private Long ParentID;
    @Column(name = "Type")
    private String ItemType;
    private long RecipeID;

    public TreeViewItemsData(String Name, Long ParentID, String ItemType, long RecipeID) {
        this.Name = Name;
        this.ParentID = ParentID;
        this.ItemType = ItemType;
        this.RecipeID = RecipeID;
    }

}
