package com.smartbracelet.com.smartbracelet.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by Yangli on 16-05-06.
 * 数据载体，用于103推送消息
 */
@Table("msg")
public class ProgramItem extends BaseModel {
    public static final String TITLE = "title";
    public static final String ID = "_id";
    @Column("title") public String title;
    @Column("author") public String author;
    @Column("body") public String body;
    @Column("timeStamp") public String timeStamp;

    public static ProgramItem getData(int index) {
        ProgramItem programItem = new ProgramItem();
        programItem.title = "title" + index;
        programItem.author = "author" + index;
        programItem.body = "body" + index;

        return  programItem;

    }

    public String toString() {
        return title + "," + author + "," + body+ "," + timeStamp;
    }
}
