package com.yuan.mod.service.impl;

import com.yuan.mod.core.constant.ShiroConstants;
import com.yuan.mod.core.exception.UserPasswordNotMatchException;
import com.yuan.mod.core.pojo.User;
import com.yuan.mod.service.PasswordService;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Package com.acc.service.impl
 * @ClassName PasswordServiceImpl
 * @Description
 * @Author YUAND
 * @Date 2024/8/26 9:41
 * @Version 1.0
 */
@Service
public class PasswordServiceImpl implements PasswordService {


    @Autowired
    private CacheManager cacheManager;

    private Cache<String, AtomicInteger> loginRecordCache;


    @PostConstruct
    public void init() {
        loginRecordCache = cacheManager.getCache(ShiroConstants.LOGIN_RECORD_CACHE);
    }

    /**
     * 用户验证
     *
     * @param user     用户
     * @param password 密码
     */
    @Override
    public void validate(User user, String password) {
        String loginName = user.getLoginName();

        AtomicInteger retryCount = loginRecordCache.get(loginName);

        if (!matches(user, password)) {
            loginRecordCache.put(loginName, retryCount);
            throw new UserPasswordNotMatchException();
        } else {
            clearLoginRecordCache(loginName);
        }
    }

    @Override
    public boolean matches(User user, String newPassword) {
        return user.getPassword().equals(encryptPassword(user.getLoginName(), newPassword, user.getSalt()));
    }

    @Override
    public void clearLoginRecordCache(String loginName) {
        loginRecordCache.remove(loginName);
    }

    @Override
    public String encryptPassword(String loginName, String password, String salt) {

        return new Md5Hash(loginName + password + salt).toHex();
    }
}
