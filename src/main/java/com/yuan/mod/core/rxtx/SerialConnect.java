package com.yuan.mod.core.rxtx;


import com.yuan.mod.core.pojo.BodyPart;
import com.yuan.mod.core.util.BodyUtils;
import com.yuan.mod.core.websocket.WebSocketServer;
import com.yuan.mod.mapper.BodyMapper;
import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * SerialConnect类用于通过继承Thread类并实现SerialPortEventListener接口来处理串口连接
 * 该类使得串口通信能够在单独的线程中进行监听和处理，而不会阻塞主线程
 */
@Slf4j
@Component
@Lazy
public class SerialConnect extends Thread implements SerialPortEventListener { // SerialPortEventListener
    public OutputStream outputStream;// 向串口输出的流
    private CommPortIdentifier portId; // 串口通信管理类
    private Enumeration<?> portList; // 有效连接上的端口的枚举
    private SerialPort serialPort; // 串口的引用
    private InputStream inputStream; // 从串口来的输入流
    private final String[] msgArr = new String[11];
    // 堵塞队列用来存放读到的数据
    private final BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>();
    private int totalMod = 0;
    private final BodyUtils bodyUtil = new BodyUtils();
    private int i = 0;
    private int j = 0;
    private final BodyPart[] bodyPartArr = new BodyPart[10];
    @Autowired
    private BodyMapper bodymapper;

    /**
     * SerialPort EventListener 的方法,持续监听端口上是否有数据流
     */
    @Override
    public void serialEvent(SerialPortEvent event) {//
        switch (event.getEventType()) {
            // 处理各种串口事件
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            // 当有可用数据时读取数据
            case SerialPortEvent.DATA_AVAILABLE:
                try {
                    // 获取输入流
                    InputStream in = serialPort.getInputStream();
                    // 获取缓冲区长度
                    int bufflenth = in.available();
                    int numBytes = -1;
                    int mod = 0;
                    byte[] readBuffer = new byte[bufflenth];
                    // 循环读取数据
                    while (inputStream.available() > 0) {
                        // 读取数据到缓冲区
                        numBytes = inputStream.read(readBuffer);
                        if (numBytes > 0) {
                            // 将读到的数据添加到消息队列
                            msgQueue.add(new Date() + "真实收到的数据为：-----" + new String(readBuffer).trim());
                            // 处理接收到的数据体
                            BodyPart bodyPart = bodyUtil.transBody(readBuffer);
                            // 将处理后的数据体存入数组
                            bodyPartArr[i] = bodyPart;
                            // 将数据体转换为字符串存入消息数组
                            msgArr[i] = bodyPart.toString();
                            i = i + 1;
                            // 插入数据库
                            bodymapper.insertAll(bodyPart);
                            // 每10个数据进行一次计算
                            if (i == 10) {
                                // 计数器加一
                                j = j + 1;
                                // 计算累加值
                                mod = bodyUtil.caMod(bodyPartArr);
                                totalMod = totalMod + mod;
                                // 将累加值存入消息数组
                                msgArr[i] = String.valueOf(totalMod);
                                // 每5次计算发送一次WebSocket消息
                                if (j == 5) {
                                    WebSocketServer.sendMessage(Arrays.toString(msgArr));
                                    j = 0;
                                }
                                i = 0;
                            }
                        } else {
                            // 没有读到数据，添加消息到队列
                            msgQueue.add("没有读到数据");
                        }
                    }
                } catch (IOException e) {
                    // 捕获并处理IO异常
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 通过程序打开COM4串口，设置监听器以及相关的参数
     *
     * @return 返回1 表示端口打开成功，返回 0表示端口打开失败
     */
    public int startComPort() {
        // 通过串口通信管理类获得当前连接上的串口列表
        portList = CommPortIdentifier.getPortIdentifiers();

        while (portList.hasMoreElements()) {

            // 获取相应串口对象
            portId = (CommPortIdentifier) portList.nextElement();
            log.info("设备类型：--->" + portId.getPortType());
            log.info("设备名称：---->" + portId.getName());
            // 判断端口类型是否为串口
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                // 判断如果COM5串口存在，就打开该串口
                if (portId.getName().equals("COM1")) {
                    try {
                        // 打开串口名字为COM_5(名字任意),延迟为2毫秒
                        serialPort = portId.open("COM_1", 2000);

                    } catch (PortInUseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    // 设置当前串口的输入输出流
                    try {
                        inputStream = serialPort.getInputStream();
                        outputStream = serialPort.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    // 给当前串口添加一个监听器
                    try {
                        serialPort.addEventListener(this);
                    } catch (TooManyListenersException e) {
                        e.printStackTrace();
                        return 0;
                    }
                    // 设置监听器生效，即：当有数据时通知
                    serialPort.notifyOnDataAvailable(true);

                    // 设置串口的一些读写参数
                    try {
                        // 比特率、数据位、停止位、奇偶校验位
                        serialPort.setSerialPortParams(115200,
                                SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                    } catch (UnsupportedCommOperationException e) {
                        e.printStackTrace();
                        return 0;
                    }

                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            log.info("--------------任务处理线程运行了--------------");
            while (true) {
                // 如果堵塞队列中存在数据就将其输出
                if (msgQueue.size() > 0) {
                    log.info(msgQueue.take());
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
