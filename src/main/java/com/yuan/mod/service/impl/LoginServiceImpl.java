package com.yuan.mod.service.impl;

import com.yuan.mod.core.exception.UserNotExistsException;
import com.yuan.mod.core.pojo.User;
import com.yuan.mod.core.util.StringUtils;
import com.yuan.mod.service.LoginService;
import com.yuan.mod.service.PasswordService;
import com.yuan.mod.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 登录处理
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserService userService;


    @Override
    public User login(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new UserNotExistsException();
        }
        // 查询用户信息
        User user = userService.selectUserByLoginName(username);

        if (user == null) {
            throw new UserNotExistsException();
        }

        passwordService.validate(user, password);

        return user;
    }


}
