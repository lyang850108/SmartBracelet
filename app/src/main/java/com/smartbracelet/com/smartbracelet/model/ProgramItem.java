package com.smartbracelet.com.smartbracelet.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by zengjinlong on 15-12-13.
 */
@Table("homeItems")
public class ProgramItem extends BaseModel {
    @Column("deviceid") public int id;

    public String toString() {
        return id + "";
    }
}
