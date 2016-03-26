package com.smartbracelet.com.smartbracelet.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by zengjinlong on 15-12-13.
 */
@Table("homeItems")
public class ProgramItem extends BaseModel {
    @Column("query") public String query;
    @Column("translation") public String translation;
    @Column("basic") public String basic;
    @Column("web") public String web;

    public String toString() {
        return errorCode + "," + query + "," + translation+ "," + basic+ "," + web;
    }
}
