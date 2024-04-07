package com.yuan.mod.dao;

import com.yuan.mod.dao.mapper.BodyMapper;
import com.yuan.mod.pojo.BodyPart;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Package com.yuan.mod.dao
 * @ClassName Connect
 * @Description TODO
 * @Author YUAND
 * @Date 2024/2/26 10:05
 * @Version 1.0
 */

public class Connect {
    public int connectTo(BodyPart bodyPart) {
        int result = 0;
        try {
            InputStream is = Resources.getResourceAsStream("mapper/mybatis.xml");
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(is);
            SqlSession sqlSession = sqlSessionFactory.openSession(true);
            BodyMapper mapper = sqlSession.getMapper(BodyMapper.class);
            result = mapper.insertAll(bodyPart);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
