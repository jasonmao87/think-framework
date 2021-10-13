package com.think.moudles.auth;

import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkColumn;
import com.think.core.bean.SimplePrimaryEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@ApiModel(value = "标准账户模型",description = "标准账户模型")
@Accessors(chain = true)
@Remark("标准用户账户模型")
public class UserAccountModel extends SimplePrimaryEntity {

    @ApiModelProperty("登录id")
    @ThinkColumn(length = 32,nullable = false)
    private String userId ="";

    @ApiModelProperty("用户名")
    @ThinkColumn(length = 32,nullable = false)
    private String userName = "";

    @ApiModelProperty("当前ENCODE密码")
    @ThinkColumn(length = 64,nullable = false)
    private String password ="";

    @ApiModelProperty(value = "随机加盐字符串",hidden = true)
    @ThinkColumn(length = 8 ,nullable = false)
    private String randomPasswordStr ="" ;


}
