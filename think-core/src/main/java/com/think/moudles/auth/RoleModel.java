package com.think.moudles.auth;

import com.think.core.bean.SimplePrimaryEntity;

public class RoleModel extends SimplePrimaryEntity {
    private String name ;

    private String description;

    private boolean root ;

    private long orgId;

}
