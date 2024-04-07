package com.yuan.mod.controller.rxtx;


import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.yuan.mod.dao.Connect;
import com.yuan.mod.pojo.BodyPart;
import com.yuan.mod.service.util.BodyUtil;
import com.yuan.mod.controller.websocket.WebSocketServer;
import gnu.io.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
/**
 * @Package com.yuan
 * @ClassName SerialConnect
 * @Description TODO
 * @Author YUAND
 * @Date 2024/2/14 22:30
 * @Version 1.0
 */
@Controller
public class SerialConnect extends Thread implements SerialPortEventListener { // SerialPortEventListener
    static CommPortIdentifier portId; // 串口通信管理类
    static Enumeration<?> portList; // 有效连接上的端口的枚举
    InputStream inputStream; // 从串口来的输入流
    static OutputStream outputStream;// 向串口输出的流
    static SerialPort serialPort; // 串口的引用
    // 堵塞队列用来存放读到的数据
    private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>();
    private int totalMod = 0;
    private  BodyUtil bodyUtil = new BodyUtil();
    private Connect connect = new Connect();
    private int i = 0;
    private  int j = 0;
    private BodyPart[] bodyPartArr = new BodyPart[10];
    String[] msgArr = new String[11];


    @Override
    /**
     * SerialPort EventListene 的方法,持续监听端口上是否有数据流
     */
    public void serialEvent(SerialPortEvent event) {//

        switch (event.getEventType()) {
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
            case SerialPortEvent.DATA_AVAILABLE:// 当有可用数据时读取数据
//                try {
//                    sleep(10);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                try {
                    InputStream in = serialPort.getInputStream();

                    int bufflenth = in.available();
                    int numBytes = -1;
                    int mod = 0;
                    byte[] readBuffer = new byte[bufflenth];
                    while (inputStream.available() > 0) {
                        numBytes = inputStream.read(readBuffer);
                        if (numBytes > 0) {
                            msgQueue.add(new Date() + "真实收到的数据为：-----" + new String(readBuffer).trim());
                            BodyPart bodyPart = bodyUtil.transBody(readBuffer);
                                bodyPartArr[i] = bodyPart;
                                msgArr[i] = bodyPart.toString();
                                i = i + 1;
                                connect.connectTo(bodyPart);
                                if(i == 10){
                                    j = j + 1;
                                    mod = bodyUtil.caMod(bodyPartArr);
                                    totalMod = totalMod + mod;
                                    msgArr[i] = String.valueOf(totalMod);
                                    if (j == 5){
                                        WebSocketServer.sendMessage(Arrays.toString(msgArr));
                                        j = 0;
                                    }
                                    i = 0;
                            }
                        } else {
                            msgQueue.add("没有读到数据");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }



    /**
     *
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
            System.out.println("设备类型：--->" + portId.getPortType());
            System.out.println("设备名称：---->" + portId.getName());
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
            System.out.println("--------------任务处理线程运行了--------------");
            while (true) {
                // 如果堵塞队列中存在数据就将其输出
                if (msgQueue.size() > 0) {
                    System.out.println(msgQueue.take());
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 启动串口
     */
    public void go(){
        SerialConnect cRead = new SerialConnect();
        int i = cRead.startComPort();
        if (i == 1) {
            // 启动线程来处理收到的数据
            cRead.start();
            try {
                String st = "\r";
                System.out.println("发出字节数：" + st.getBytes("gbk").length);
                outputStream.write(st.getBytes("gbk"), 0,
                        st.getBytes("gbk").length);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
