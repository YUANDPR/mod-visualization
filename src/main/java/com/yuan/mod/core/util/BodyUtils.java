package com.yuan.mod.core.util;


import com.yuan.mod.core.pojo.BodyPart;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Date;

/**
 * 计算mod工具类
 */
public class BodyUtils implements ApplicationEventPublisher {
    private static double rightBatch = 0;
    private static double leftBatch = 0;

    /**
     * 计算mod值
     *
     * @param bodyParts
     * @return
     */
    public int caMod(BodyPart[] bodyParts) {
        int mod = 0;
        //判断M1
        if ((bodyParts[0].getIsture() == 1 || bodyParts[1].getIsture() == 1)
                && bodyParts[2].getIsture() == 0 && bodyParts[3].getIsture() == 0
                && bodyParts[4].getIsture() == 0 && bodyParts[5].getIsture() == 0) {
            mod = mod + 1;
        }
        //判断M2
        if ((bodyParts[0].getIsture() == 2 || bodyParts[1].getIsture() == 2)
                && bodyParts[2].getIsture() == 0 && bodyParts[3].getIsture() == 0
                && bodyParts[4].getIsture() == 0 && bodyParts[5].getIsture() == 0) {
            mod = mod + 2;
        }
        //判断M3
        if (bodyParts[2].getIsture() == 1 && bodyParts[3].getIsture() == 1
                && bodyParts[4].getIsture() == 0 && bodyParts[5].getIsture() == 0) {
            mod = mod + 3;
        }
        //判断M4
        if (bodyParts[4].getIsture() == 1 && bodyParts[8].getIsture() == 0
                && Math.pow(Math.pow(bodyParts[2].getYAxis() - bodyParts[4].getYAxis(), 2)
                + Math.pow(bodyParts[2].getPAxis() - bodyParts[4].getPAxis(), 2)
                + Math.pow(bodyParts[2].getRAxis() - bodyParts[4].getRAxis(), 2), 1 / 3) < 20) {
            mod = mod + 4;
        }
        //判断M5
        if (bodyParts[4].getIsture() == 1 && bodyParts[8].getIsture() == 0
                && Math.pow(Math.pow(bodyParts[2].getYAxis() - bodyParts[4].getYAxis(), 2)
                + Math.pow(bodyParts[2].getPAxis() - bodyParts[4].getPAxis(), 2)
                + Math.pow(bodyParts[2].getRAxis() - bodyParts[4].getRAxis(), 2), 1 / 3) >= 20) {
            mod = mod + 5;
        }
        //判断G0
        if ((bodyParts[0].getPalmPressure() > 0 && leftBatch <= 1)
                || (bodyParts[1].getPalmPressure() > 0 && rightBatch <= 1)) {
            mod = mod + 0;
        }
        //判断G1
        if ((bodyParts[0].getPalmPressure() > 0 && isInRange(leftBatch, 1, 3))
                || (bodyParts[1].getPalmPressure() > 0 && isInRange(rightBatch, 1, 3))) {
            mod = mod + 1;
        }
        //判断G3
        if ((bodyParts[0].getPalmPressure() > 0 && leftBatch > 3)
                || (bodyParts[1].getPalmPressure() > 0 && rightBatch > 3)) {
            mod = mod + 3;
        }
        //判断P0
        if ((bodyParts[0].getPalmPressure() == 0 && leftBatch < 2)
                || (bodyParts[1].getPalmPressure() == 0 && rightBatch < 2)) {
            mod = mod + 0;
        }
        //判断P2
        if ((bodyParts[0].getPalmPressure() == 0 && isInRange(leftBatch, 2, 5))
                || (bodyParts[1].getPalmPressure() == 0 && isInRange(rightBatch, 2, 5))) {
            mod = mod + 2;
        }
        //判断P5
        if ((bodyParts[0].getPalmPressure() == 0 && leftBatch > 5)
                || (bodyParts[1].getPalmPressure() == 0 && rightBatch > 5)) {
            mod = mod + 5;
        }
        //判断F3
        if (bodyParts[6].getIsture() == 1 || bodyParts[7].getIsture() == 1) {
            mod = mod + 3;
        }
        //判断W5
        if (bodyParts[6].getIsture() == 2 || bodyParts[7].getIsture() == 2) {
            mod = mod + 5;
        }
        //判断B17
        if (bodyParts[8].getIsture() == 1 && bodyParts[9].getIsture() == 0) {
            mod = mod + 17;
        }
        //判断S30
        if (bodyParts[8].getIsture() == 1 && bodyParts[9].getIsture() == 1) {
            mod = mod + 30;
        }
        return mod;
    }

    /**
     * 将接收的数据转换成BodyPart对象
     *
     * @param readBuffer
     * @return
     */
    public BodyPart transBody(byte[] readBuffer) {
        BodyPart bodyPart = new BodyPart();
        String result = new String(readBuffer);
        result = result.trim();
        String[] arr1 = result.split(" ");
        bodyPart.setName(arr1[0]);
        bodyPart.setYAxis(Double.parseDouble(arr1[1]));
        bodyPart.setPAxis(Double.parseDouble(arr1[2]));
        bodyPart.setRAxis(Double.parseDouble(arr1[3]));
        bodyPart.setFingerPressure(Integer.parseInt(arr1[4]));
        bodyPart.setPalmPressure(Integer.parseInt(arr1[5]));
        bodyPart.setMTime(new Date());
        bodyPart.setIsture(isEffective(bodyPart));
        bodyPart.setBatch(Integer.parseInt(arr1[6]));
        if (bodyPart.getName().equals("lefthand") && bodyPart.getFingerPressure() == 1) {
            leftBatch = leftBatch + 1;
        }
        if (bodyPart.getName().equals("righthand") && bodyPart.getFingerPressure() == 1) {
            rightBatch = rightBatch + 1;
        }
        if (bodyPart.getName().equals("lefthand") && bodyPart.getFingerPressure() == 0) {
            leftBatch = 0;
        }
        if (bodyPart.getName().equals("righthand") && bodyPart.getFingerPressure() == 0) {
            rightBatch = 0;
        }
        return bodyPart;
    }

    /**
     * 判断数据是否有效
     *
     * @param bodyPart
     * @return
     */
    public Integer isEffective(BodyPart bodyPart) {
        String s = "right|left";
        String name = bodyPart.getName().replaceAll(s, "");
        int result = 0;
        switch (name) {
            case "hand":
                if (isInRange(bodyPart.getYAxis(), -0.24, 24.22)
                        && isInRange(bodyPart.getPAxis(), -0.68, 4.01)
                        && isInRange(bodyPart.getRAxis(), -3.27, 5.43)
                        && bodyPart.getFingerPressure() == 0
                        && bodyPart.getPalmPressure() == 0) {
                    result = 0;
                } else if (isInRange(bodyPart.getYAxis(), 24.22, 67.65)
                        && isInDouRange(bodyPart.getPAxis(), -34.19, -0.68, 4.01, 25.62)
                        && isInDouRange(bodyPart.getRAxis(), -8.98, -3.27, 5.43, 10.46)
                        && (bodyPart.getFingerPressure() == 1 || bodyPart.getPalmPressure() == 1)) {
                    result = 1;
                } else if (isInRange(bodyPart.getYAxis(), -19.83, -0.24)
                        && isInDouRange(bodyPart.getPAxis(), -42.23, -34.19, 25.62, 31.06)
                        && isInDouRange(bodyPart.getRAxis(), -40.03, -8, 10.46, 15.94)
                        && (bodyPart.getFingerPressure() == 1 || bodyPart.getPalmPressure() == 1)) {
                    result = 2;
                } else {
                    result = -1;
                }
                break;
            case "forearm":
                if (isInRange(bodyPart.getYAxis(), -24.04, 0.03)
                        && isInRange(bodyPart.getPAxis(), -0.28, 1.39)
                        && isInRange(bodyPart.getRAxis(), -4.86, 0.24)) {
                    result = 0;
                } else if (isInRange(bodyPart.getYAxis(), 0.03, 80.25)
                        && isInDouRange(bodyPart.getPAxis(), -29.21, -0.28, 1.39, 64.48)
                        && isInDouRange(bodyPart.getRAxis(), -29.48, -4.86, 0.24, 70.92)) {
                    result = 1;
                } else {
                    result = -1;
                }
                break;
            case "upperarm":
                if (isInRange(bodyPart.getYAxis(), -4.77, 0)
                        && isInRange(bodyPart.getPAxis(), -0.34, 0.76)
                        && isInRange(bodyPart.getRAxis(), -2.67, 0.53)) {
                    result = 0;
                } else if (isInRange(bodyPart.getYAxis(), 0, 108.69)
                        && isInDouRange(bodyPart.getPAxis(), -42.33, -0.34, 0.76, 7.81)
                        && isInDouRange(bodyPart.getRAxis(), -52.8, -2.67, 0.53, 81.12)) {
                    result = 1;
                } else {
                    result = -1;
                }
                break;
            case "ankle":
                if (isInRange(bodyPart.getYAxis(), -11.83, -0.01)
                        && isInRange(bodyPart.getPAxis(), -0.01, 3.99)
                        && isInRange(bodyPart.getRAxis(), -1.12, 1.43)) {
                    result = 0;
                } else if (isInDouRange(bodyPart.getYAxis(), -22.95, -11.83, -0.01, 0.01)
                        && isInRange(bodyPart.getPAxis(), -12.51, -0.01)
                        && isInDouRange(bodyPart.getRAxis(), -5.17, -1.12, 1.43, 2.83)) {
                    result = 1;
                } else if (isInDouRange(bodyPart.getYAxis(), -174.42, -22.95, 0.01, 136.83)
                        && isInDouRange(bodyPart.getPAxis(), -121.17, -12.51, 3.99, 5.2)
                        && isInDouRange(bodyPart.getRAxis(), -132.61, -5, 2.83, 10.57)) {
                    result = 2;
                } else {
                    result = -1;
                }
                break;
            case "abdomen":
                if (isInRange(bodyPart.getYAxis(), -4.3, -0.01)
                        && isInRange(bodyPart.getPAxis(), -2.03, 0.61)
                        && isInRange(bodyPart.getRAxis(), -1.21, 1.96)) {
                    result = 0;
                } else if (isInDouRange(bodyPart.getYAxis(), -8.19, -4.3, -0.01, 4.09)
                        && isInDouRange(bodyPart.getPAxis(), -11.26, -2.03, 0.61, 2.31)
                        && isInDouRange(bodyPart.getRAxis(), -33.59, -1.21, 1.96, 44.57)) {
                    result = 1;
                } else {
                    result = -1;
                }
                break;
            case "hip":
                if (isInRange(bodyPart.getYAxis(), 0.01, 2.89)
                        && isInRange(bodyPart.getPAxis(), -0.49, 0.57)
                        && isInRange(bodyPart.getRAxis(), -0.24, 0.99)) {
                    result = 0;
                } else if (isInDouRange(bodyPart.getYAxis(), -20.01, 0.01, 2.89, 8.41)
                        && isInDouRange(bodyPart.getPAxis(), -3.28, -0.49, 0.57, 16.27)
                        && isInDouRange(bodyPart.getRAxis(), -7.48, -0.24, 0.99, 19.72)) {
                    result = 1;
                } else {
                    result = -1;
                }
                break;
        }
        return result;
    }

    private boolean isInDouRange(Double num, double min1, double max1, double min2, double max2) {
        if (isInRange(num, min1, max1) || isInRange(num, min2, max2)) {
            return true;
        } else return false;
    }

    private boolean isInRange(Double num, double min, double max) {
        if (num > min && num < max) {
            return true;
        } else return false;
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        ApplicationEventPublisher.super.publishEvent(event);
    }

    @Override
    public void publishEvent(Object event) {

    }
}
