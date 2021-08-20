package com.think.moudles.auth;

import com.think.common.util.security.SHAUtil;
import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkTable;
import com.think.core.annotations.bean.ThinkColumn;
import com.think.core.annotations.bean.ThinkIndex;
import com.think.core.annotations.bean.ThinkIndexes;
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
//
//    public static void main(String[] args) {
//
//
//        for(int i = 0 ; i < 5 ;i ++){
//            String s = "";
//            if(i == 0){
//                System.out.println("SHA256");
//                s = SHAUtil.sha256("maodaiqing19871007@think.did" + "&"+ "JASON_MAO-52241169877-+M@777&156995000000");
//            }else if(i == 1){
//                s = SHAUtil.sha1("maodaiqing19871007@think.did" + "&"+ "JASON_MAO-52241169877-+M@777&156995000000");
//                System.out.println("SHA1");
//            }else if( i == 2 ){
//                s = SHAUtil.sha224("maodaiqing19871007@think.did" + "&"+ "JASON_MAO-52241169877-+M@777&156995000000");
//                System.out.println("SHA224");
//            }else if( i ==3 ){
//                s = SHAUtil.sha384("maodaiqing19871007@think.did" + "&"+ "JASON_MAO-52241169877-+M@777&156995000000");
//                System.out.println("SHA384");
//            }else if(i == 4){
//                s = SHAUtil.sha512("maodaiqing19871007@think.did" + "&"+ "JASON_MAO-52241169877-+M@777&156995000000");
//                System.out.println("SHA512");
//            }
//
//            System.out.println(s);
//            System.out.println(s.length());
//        }
//
//
//    }

}
