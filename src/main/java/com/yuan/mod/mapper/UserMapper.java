package com.yuan.mod.mapper;


import com.yuan.mod.core.pojo.User;


/**
 * 用户表 数据层
 */
public interface UserMapper {
    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    User selectUserByLoginName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    User selectUserById(Long userId);
}
