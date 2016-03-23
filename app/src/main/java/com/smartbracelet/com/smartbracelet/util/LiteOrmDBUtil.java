package com.smartbracelet.com.smartbracelet.util;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.smartbracelet.com.smartbracelet.BuildConfig;


import java.util.ArrayList;
import java.util.List;

public class LiteOrmDBUtil {

    public static final String DB_NAME = "abroadEasy.db";
    public static LiteOrm sLiteOrm;

    public static void init(Context context) {
        sLiteOrm = LiteOrm.newSingleInstance(context, DB_NAME);
        if (BuildConfig.DEBUG) {
            sLiteOrm.setDebugged(true);
        }
//        sLiteOrm = LiteOrm.newCascadeInstance(context, DB_NAME);
//        sLiteOrm.setDebugged(true);
    }

    public static LiteOrm getLiteOrm() {
        return sLiteOrm;
    }

    /**
     * 插入一条记录
     *
     * @param t
     */
    public static <T> void insert(T t) {
        sLiteOrm.save(t);
    }

    /**
     * 插入所有记录
     *
     * @param list
     */
    public static <T> void insertAll(List<T> list) {
        sLiteOrm.save(list);
    }

    /**
     * 查询所有
     *
     * @param cla
     * @return
     */
    public static <T> List<T> getQueryAll(Class<T> cla) {
        return sLiteOrm.query(cla);
    }

    /**
     * 查询  某字段 等于 Value的值
     *
     * @param cla
     * @param field
     * @param value
     * @return
     */
    public static <T> List<T> getQueryByWhere(Class<T> cla, String field, String[] value) {
        return sLiteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }

    /**
     * 查询  某字段 等于 Value的值  可以指定从1-20，就是分页
     *
     * @param cla
     * @param field
     * @param value
     * @param start
     * @param length
     * @return
     */
    public static <T> List<T> getQueryByWhereLength(Class<T> cla, String field, String[] value, int start, int length) {
        return sLiteOrm.<T>query(new QueryBuilder(cla).where(field + "=?", value).limit(start, length));
    }

    /**
     * 删除所有 某字段等于 Vlaue的值
     *
     * @param cla
     * @param field
     * @param value
     */
    public static <T> void deleteWhere(Class<T> cla, String field, String[] value) {
        sLiteOrm.delete(cla, WhereBuilder.create(cla).where(field + "=?", value));
    }

    /**
     * 删除所有
     *
     * @param cla
     */
    public static <T> void deleteAll(Class<T> cla) {
        sLiteOrm.deleteAll(cla);
    }

    /**
     * 仅在以存在时更新
     *
     * @param t
     */
    public static <T> void update(T t) {
        sLiteOrm.update(t, ConflictAlgorithm.Replace);
    }

    public static <T> void updateALL(List<T> list) {
        sLiteOrm.update(list);
    }

    private static int sTestNum = 0;

}
