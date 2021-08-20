package com.think.common.util.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThinkHashUtilTest {

    @Test
    void simpleStrHashcode() {
        String str ="abc";
        System.out.println(ThinkHashUtil.simpleStrHashcode("abcxxx"));
    }
}