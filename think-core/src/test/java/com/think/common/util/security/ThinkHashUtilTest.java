package com.think.common.util.security;

import com.think.common.util.L;
import org.junit.jupiter.api.Test;

class ThinkHashUtilTest {

    @Test
    void simpleStrHashcode() {
        String str ="abc";
        L.print(Integer.toString(Integer.MAX_VALUE,36));
        L.print(ThinkHashUtil.simpleStrHashcode("abcxxx"));
    }
}