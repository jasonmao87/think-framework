package com.think.data.multis;

import com.think.data.model.ThinkTableModel;

import java.util.List;

public interface IDDLBuilder {

    default List<String> createTableDDL(ThinkTableModel tableModal){
        return this.createTableDDL(tableModal,-1);
    }
    List<String> createTableDDL(ThinkTableModel tableModal , int splitYear);


}
