package com.yuan.mod.service;


import com.yuan.mod.core.pojo.User;

/**
 * 密码处理服务
 */
public interface PasswordService {

    void validate(User user, String password);

    boolean matches(User user, String newPassword);

    void clearLoginRecordCache(String loginName);

    String encryptPassword(String loginName, String password, String salt);
}
