package com.yuan.mod.core.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName body_part
 */
@Data
public class BodyPart implements Serializable {
    /**
     * 主键ID
     */
    private Long id = 0L;

    /**
     * 部位名称
     */
    private String name;

    /**
     * y轴
     */
    private Double yAxis;

    /**
     * p轴
     */
    private Double pAxis;

    /**
     * r轴
     */
    private Double rAxis;

    /**
     * 食指压力
     */
    private Integer fingerPressure;

    /**
     * 手掌压力
     */
    private Integer palmPressure;

    /**
     * 时间
     */
    private Date mTime;

    /**
     * 有效值
     */
    private Integer isture = -1;
    /**
     * 批次
     */
    private Integer batch = 0;

    @Override
    public String toString() {
        return
                name + ',' + yAxis + ',' + pAxis + ',' + rAxis;
    }
}