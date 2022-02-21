package com.group8.dao;

import com.group8.dto.UserQueryCondition;
import com.group8.entity.LgNormalUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author laiyong
 * @date 2022/2/17 12:15 星期四
 * @apiNote
 */
public interface NormalUserDao {

    int addNormalUser(@Param("normalUser") LgNormalUser lgNormalUser);

    //验证激活码
    LgNormalUser checkActiveCode(String code);

    int updateUserStatus(int userId);

    LgNormalUser findByUsernameAndPwd(@Param("userName") String userName, @Param("password") String password);

    List<LgNormalUser> findByCondition(@Param("lgNormalUser") LgNormalUser lgNormalUser);

    int update(@Param("normalUser") LgNormalUser lgNormalUser);

    int deleteById(@Param("id") int id);

    LgNormalUser checkUserName(@Param("userName") String userName);

    int updateHeadImg(@Param("id") int id, @Param("url") String url);

    LgNormalUser findById(@Param("id") int id);
}