package com.bondex.photo.test;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * date: 2020/4/28
 *
 * @Author: ysl
 * description:
 */
public class TestThreadTest {

    @Test
    public void testThread() {

        TestThread testThread = new TestThread();

        for (int i = 0; i < 10; ++i){
            testThread.testThread();
        }


        assertEquals(1,1);
    }
}