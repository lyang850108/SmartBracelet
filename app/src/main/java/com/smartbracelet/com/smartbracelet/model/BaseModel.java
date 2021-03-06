package com.smartbracelet.com.smartbracelet.model;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * 模板类
 */
public class BaseModel implements Serializable {
    @PrimaryKey(AssignType.AUTO_INCREMENT) @Column("_id") protected long id;
}
