package com.smartbracelet.com.smartbracelet.network;

import com.smartbracelet.com.smartbracelet.model.ProgramItem;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Yang on 16/5/18.
 * 这个接口类用于网络交互请求
 * 暂时没有用到
 * 后续可以用注解的方式完成网络交互请求，可以简化代码复杂度
 * 我懒了 请后面的童鞋补上
 */
public interface RetrofitService {
    @GET("/data/{startNum}")
    List<ProgramItem> getProgramList(@Path("startNum") int num);
}
