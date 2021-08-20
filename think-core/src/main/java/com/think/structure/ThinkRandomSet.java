package com.think.structure;

import org.omg.CORBA.Object;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Date :2021/5/19
 * @Name :ThinkRandomSet
 * @Description : 请输入
 */
public class ThinkRandomSet<T>{
    private Set<T> set;
    public ThinkRandomSet( ) {
        this.set = new HashSet<>();
    }
}
