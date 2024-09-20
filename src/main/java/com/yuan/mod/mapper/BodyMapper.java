package com.yuan.mod.mapper;

import com.yuan.mod.core.pojo.BodyPart;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Package com.yuan.mod.mapper
 * @ClassName BodyMapper
 * @Description
 * @Author YUAND
 * @Date 2024/2/25 20:58
 * @Version 1.0
 */
@Mapper
public interface BodyMapper {
    int insertAll(BodyPart bodyPart);
}
