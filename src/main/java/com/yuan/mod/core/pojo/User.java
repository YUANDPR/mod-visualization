package com.yuan.mod.core.pojo;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 用户表
 */
@Data
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录名称
     */
    private String loginName;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐加密
     */
    private String salt;

}
