package com.think.data.verification;

public interface ThinkKeyValidator {
    void verification(Class targetClass,String k ,  Object v) throws RuntimeException ;
}
