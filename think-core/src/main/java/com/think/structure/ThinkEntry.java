package com.think.structure;

import java.io.Serializable;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2022/9/8 14:25
 * @description :
 */
public class ThinkEntry<K,V> {
    private K k ;
    private V v;

    public ThinkEntry(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public V getV() {
        return v;
    }
}
