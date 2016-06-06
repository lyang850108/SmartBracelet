package com.smartbracelet.com.smartbracelet.util;

import java.lang.reflect.Field;
import java.util.TimerTask;

/**
 * Created by Yang on 16/5/14.
 * 反射机制
 * 用于修改Timetask的周期
 */
public abstract class ReschedulableTimerTask extends TimerTask {
    public void setPeriod(long period) {
        //缩短周期，执行频率就提高
        setDeclaredField(TimerTask.class, this, "period", period);
    }

    //通过反射修改字段的值
    static boolean setDeclaredField(Class<?> clazz, Object obj,
                                    String name, Object value) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
