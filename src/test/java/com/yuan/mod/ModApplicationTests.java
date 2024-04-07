package com.yuan.mod;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

@SpringBootTest
public class ModApplicationTests {
    static int[] a = {1,2,3,4};

    public static void main(String[] args) {
        for (int i : a){
            System.out.println(i);
            if (i == 3) break;
        }
    }
}
