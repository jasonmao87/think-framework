package com.think.mongo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
@Data
@Accessors(chain = true)
public class TestNEntity implements Serializable {
    private static final long serialVersionUID = 6647501238087521480L;


    private String xxx ;
}
