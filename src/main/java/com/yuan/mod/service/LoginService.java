package com.yuan.mod.service;


import com.yuan.mod.core.pojo.User;

/**
 * 登陆服务
 */
public interface LoginService {
    User login(String username, String password);
}
