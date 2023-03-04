package com.think.web.util;

import javax.servlet.http.HttpServletRequest;

public interface IRequestFunction<T> {
    T execute(HttpServletRequest request);
}
