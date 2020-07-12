package org.lyl.kkb;

import org.lyl.kkb.dao.BaseDao;
import org.lyl.kkb.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {
    @org.junit.Test
    public void testQueryUser(){
        BaseDao baseDao = new BaseDao("user");
        List<User> users1 =baseDao.query("queryUserByName","lyl");
        List<User> users2 =baseDao.query("queryUserBySex",0);
        System.out.println(users1);
        System.out.println(users2);
        Map<String,Object> map = new HashMap<>();
        map.put("username","lyl");
        map.put("sex",1);
        List<User> users3 = baseDao.query("queryUserByParams",map);
        System.out.println(users3);
    }
}
