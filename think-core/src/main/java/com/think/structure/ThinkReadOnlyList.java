package com.think.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * @Date :2021/6/9
 * @Name :ThinkReadOnleyList
 * @Description : 只读list
 */
public class ThinkReadOnlyList<E> extends ArrayList<E> {


    @Override
    public boolean add(E e) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    public void add(int index, E element) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    public E set(int index, E element) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    public E remove(int index) {
        throw new RuntimeException("禁止针对只读List操作");
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        throw new RuntimeException("禁止针对只读List操作");
    }
}
