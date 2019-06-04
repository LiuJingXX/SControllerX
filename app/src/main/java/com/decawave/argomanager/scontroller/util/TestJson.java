package com.decawave.argomanager.scontroller.util;

import com.alibaba.fastjson.JSONObject;
import com.decawave.argomanager.scontroller.model.Task;

import java.util.List;

/**
 * <pre>
 *     author : shenbiao
 *     e-mail : 1105125966@qq.com
 *     time   : 2018/08/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TestJson {
    static String mJson= "[{\"id\":0,\"name\":\"0\",\"userId\":0,\"publishTime\":\"2018-08-19T17:01:01.000+0000\",\"completeTime\":null,\"state\":20},{\"id\":1,\"name\":\"1\",\"userId\":0,\"publishTime\":\"2018-08-15T07:56:27.000+0000\",\"completeTime\":null,\"state\":30}]";

    public static  void main(String[] args){
        List<Task> list = JSONObject.parseArray(mJson, Task.class);
        int aa=1;
    }
}
