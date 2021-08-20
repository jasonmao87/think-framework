package com.think.common.data.mongo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.think.common.data.ThinkMongoFilterOp;
import com.think.common.util.DateUtil;
import com.think.common.util.IdUtil;
import com.think.core.annotations.Remark;

import java.io.Serializable;
import java.util.Date;

/**
 * 参数条件对象类
 */
public class ThinkMongoFilterBean implements Serializable {
    private static final long serialVersionUID = 7825889774778910680L;
    @Remark("是否合法")
    private boolean safe =true;
    @Remark("字段key")
    private String key ;
    @Remark("操作符")
    private ThinkMongoFilterOp op ;
    @Remark("参数值是不是key")
    private boolean paramIsKey ;
    @Remark("入参长度")
    private int len = 0 ;
    @Remark("参数值 ")
    private Serializable[] values;

    private boolean finish = false;

    private ThinkMongoFilterBean(String key) {
        this.key = key;
    }

    protected static ThinkMongoFilterBean build(String key){
        return new ThinkMongoFilterBean(key);
    }

    protected static ThinkMongoFilterBean parseFromJSON(String key , JSONObject entry){
        Object v = entry.get("v");
        String opStr = entry.getString("op").toUpperCase();
        String type = entry.getString("type");
        ThinkMongoFilterOp op = ThinkMongoFilterOp.valueOf(opStr);
        if(key.equals("id")){
            v = v +"";
        }
        /****** 针对createTime 的 优化 开始********************************************************************/
        if(key.equalsIgnoreCase("createTime") ) {
            if (v instanceof Date) {
                //转成 long 型值
                Date tempDate = (Date) v;
                if (op == ThinkMongoFilterOp.LE || op == ThinkMongoFilterOp.LEE ) {
                    tempDate = DateUtil.computeAddSeconds(tempDate,1);
                    v = IdUtil.idByDate(tempDate) + "";
                    key = "id";
                }else if( op == ThinkMongoFilterOp.LG || op == ThinkMongoFilterOp.LGE){
                    tempDate = DateUtil.computeAddSeconds(tempDate,-1);
                    v = IdUtil.idByDate(tempDate) +"";
                    key = "id";
                }
            }else if(v instanceof Date[]){
                if(op == ThinkMongoFilterOp.BETWEEN_AND){
                    Date tempDateArr[] = (Date[]) v;
                    Date t1 = tempDateArr[0];
                    Date t2 = tempDateArr[1];
                    t1 = DateUtil.computeAddSeconds(t1,-1);
                    t2 = DateUtil.computeAddSeconds(t2,11);
                    long id1 = IdUtil.idByDate(t1);
                    long id2 = IdUtil.idByDate(t2);
                    v = new long[]{id1,id2};
                    key = "id";
                }
            }
        }
        /****** 针对createTime 的 优化 结束********************************************************************/
        if(v instanceof JSONArray){
            JSONArray arrar = (JSONArray) v;
            v = arrar.toArray(new Serializable[arrar.size()]);
        }
        switch (op) {
            case EQ: {
                return ThinkMongoFilterBean.EQ(key, (Serializable) v);
            }

            case LE: {
                return ThinkMongoFilterBean.LE(key, (Serializable) v);
            }
            case LEE: {
                return ThinkMongoFilterBean.LEE(key, (Serializable) v);
            }

            case LG: {
                return ThinkMongoFilterBean.LG(key, (Serializable) v);
            }
            case LGE: {
                return ThinkMongoFilterBean.LGE(key, (Serializable) v);
            }

            case BETWEEN_AND:{
                Serializable[] vs = (Serializable[]) v;
                return ThinkMongoFilterBean.BETWEEN_AND(key,vs[0],vs[1]);
            }
            case LIKE:{
                return ThinkMongoFilterBean.LIKE(key, (String) v);
            }

            case IN:{
                return ThinkMongoFilterBean.IN(key,(Serializable[]) v);
            }
            case OR:{
                return ThinkMongoFilterBean.OR(key,(Serializable[]) v);
            }
        }
        return null;
    }


    private int currentIndex = 0;

    protected boolean hasMore(){
        return currentIndex< len;
    }

    protected ThinkMongoFilterBean resetIndex(){
        this.currentIndex = 0;
        return this;
    }
    protected Serializable get(){
        if(currentIndex == len){
            return null;
        }
        currentIndex ++ ;
        return values[currentIndex -1 ];
    }

    protected static ThinkMongoFilterBean EQ(String key, Serializable v){
        return build(key).initOp(ThinkMongoFilterOp.EQ,new Serializable[]{v});
    }

    protected static ThinkMongoFilterBean EQ_KEY(String key, String v){
        return build(key).initOp(ThinkMongoFilterOp.EQ,new String[]{v});
    }


    protected static ThinkMongoFilterBean LE(String key, Serializable v){
        return build(key).initOp(ThinkMongoFilterOp.LE,new Serializable[]{v});
    }
    protected static ThinkMongoFilterBean LEE(String key, Serializable v){
        return build(key).initOp(ThinkMongoFilterOp.LEE,new Serializable[]{v});
    }



    protected static ThinkMongoFilterBean LG(String key, Serializable v){
        return build(key).initOp(ThinkMongoFilterOp.LG,new Serializable[]{v});
    }
    protected static ThinkMongoFilterBean LGE(String key, Serializable v){
        return build(key).initOp(ThinkMongoFilterOp.LGE,new Serializable[]{v});
    }


    protected static ThinkMongoFilterBean BETWEEN_AND(String key, Serializable v1, Serializable v2){
        return build(key).initOp(ThinkMongoFilterOp.BETWEEN_AND,new Serializable[]{v1,v2});
    }

    protected static ThinkMongoFilterBean IN(String key, Serializable... v){
        return build(key).initOp(ThinkMongoFilterOp.IN,v);
    }

    protected static ThinkMongoFilterBean OR(String key, Serializable... v){
        return build(key).initOp(ThinkMongoFilterOp.OR,v);
    }


    protected static ThinkMongoFilterBean LIKE(String key, String v){
        return build(key).initOp(ThinkMongoFilterOp.LIKE,new String[]{v});
    }

    protected ThinkMongoFilterBean initOp(ThinkMongoFilterOp op , Serializable[] values){
        if(this.finish){
            return null;
        }

        this.finish = true;
        this.values = values;
        this.op = op;
        switch (op){
            case EQ: {
                this.len =1 ;
                if(this.len != values.length){
                   this.safe = false;
                }
                break;
            }

            case LE:{
                this.len = 1;
                if(this.len == this.values.length  ){
                }else{
                    this.safe =false;
                }
                break;
            }
            case LEE:{
                this.len =1;
                if(this.len == values.length) {
                }else{
                    this.safe =false;
                }
                break;
            }

            case LG:{
                this.len = 1 ;
                if(this.len == this.values.length  ){
                }else{
                    this.safe =false;
                }
                break;
            }
            case LGE:{
                this.len = 1;
                if(this.len == this.values.length  ){
                }else{
                    this.safe =false;
                }
                break;
            }

            case BETWEEN_AND:{
                this.len =2;
                if(this.values.length == this.len){
                }else {
                    this.safe = false;
                }
                break;
            }
            case IN:{
                this.len = values.length;
                break;
            }
            case OR:{
                //or 逻辑自动转成IN
                this.len = values.length;
                if(this.len == values.length){
                    int i= 0 ;
                }
                break;
            }
//            case IS_NULL:{
//                this.len = 0;
//                break;
//            }
//            case IS_NOT_NULL:{
//                this.len = 0;
//                break;
//            }
            case LIKE:{
                this.len = 1;
                if(this.len == values.length){
                }else{
                    this.safe = false;
                }
                break;
            }
        }
        return this;
    }


    public boolean isSafe() {
        return safe;
    }

    public String getKey() {
        return key;
    }

    public ThinkMongoFilterOp getOp() {
        return op;
    }

    public boolean isParamIsKey() {
        return paramIsKey;
    }

    public int getLen() {
        return len;
    }

    public Serializable[] getValues() {
        return values;
    }


    public boolean isFinish() {
        return finish;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
