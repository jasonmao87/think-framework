package com.think.core.security;

import com.think.common.util.L;
import org.junit.Test;

import static org.junit.Assert.*;

public class ThinkSecurityManagerTest {

    @Test
    public void setSecurityKey() {
        L.print(ThinkSecurityKey.generateKey());
    }
}