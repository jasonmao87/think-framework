package com.think.common.data.mysql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.think.common.data.ThinkFilterOp;
import com.think.common.util.DateUtil;
import com.think.common.util.IdUtil;
import com.think.core.annotations.Remark;

import java.io.Serializable;
import java.util.Date;

/**
 * 参数条件对象类
 */
public class ThinkFilterBean implements Serializable {
    private static final long serialVersionUID = -2620742982741342850L;

    @Remark("是否合法")
    private boolean safe =true;
    @Remark("字段key")
    private String key ;
    @Remark("操作符")
    private ThinkFilterOp op ;
    @Remark("参数值是不是key")
    private boolean paramIsKey ;
    @Remark("入参长度")
    private int len = 0 ;
    @Remark("参数值 ")
    private Serializable[] values;

    @Remark("SQL片段")
    private String queryPart ;

    private boolean finish = false;

    private boolean sensitive =false;


    public boolean fastMatchAble = false;

    public void setFastMatchAble(boolean fastMatchAble) {
        this.fastMatchAble = fastMatchAble;
    }

    public boolean isFastMatchAble() {
        return fastMatchAble;
    }

    public void setSensitive(){
        sensitive = true;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    private ThinkFilterBean(String key) {
        this.key = key;
    }

    protected static ThinkFilterBean build(String key){
        return new ThinkFilterBean(key);
    }

    protected static ThinkFilterBean parseFromJSON(String key , JSONObject entry){
        Object v = entry.get("v");
        String opStr = entry.getString("op").toUpperCase();
        String type = entry.getString("type");
        ThinkFilterOp op = ThinkFilterOp.valueOf(opStr);
        /****** 针对createTime 的 优化 开始********************************************************************/
        if(key.equalsIgnoreCase("createTime") ) {
            if (v instanceof Date) {
                //转成 long 型值
                Date tempDate = (Date) v;
                if (op == ThinkFilterOp.LE || op == ThinkFilterOp.LEE ) {
                    tempDate = DateUtil.computeAddSeconds(tempDate,1);
                    v = IdUtil.idByDate(tempDate);
                    key = "id";
                }else if( op == ThinkFilterOp.LG || op == ThinkFilterOp.LGE){
                    tempDate = DateUtil.computeAddSeconds(tempDate,-1);
                    v = IdUtil.idByDate(tempDate);
                    key = "id";
                }
            }else if(v instanceof Date[]){
                if(op == ThinkFilterOp.BETWEEN_AND){
                    Date tempDateArr[] = (Date[]) v;
                    Date t1 = tempDateArr[0];
                    Date t2 = tempDateArr[1];
                    t1 = DateUtil.computeAddSeconds(t1,-1);
                    t2 = DateUtil.computeAddSeconds(t2,1);
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
                return ThinkFilterBean.EQ(key, (Serializable) v);
            }
            case NOT_EQ: {
                return ThinkFilterBean.NOT_EQ(key, (Serializable) v);
            }
            case EQ_KEY:{
                return ThinkFilterBean.EQ_KEY(key, (String) v);
            }
            case NOT_EQ_KEY:{
                return ThinkFilterBean.NOT_EQ_KEY(key, (String) v);
            }
            case LE: {
                return ThinkFilterBean.LE(key, (Serializable) v);
            }
            case LEE: {
                return ThinkFilterBean.LEE(key, (Serializable) v);
            }
            case LE_KEY:{
                return ThinkFilterBean.LE_KEY(key, (String) v);
            }
            case LEE_KEY:{
                return ThinkFilterBean.LEE_KEY(key, (String) v);
            }
            case LG: {
                return ThinkFilterBean.LG(key, (Serializable) v);
            }
            case LGE: {
                return ThinkFilterBean.LGE(key, (Serializable) v);
            }
            case LG_KEY:{
                return ThinkFilterBean.LG_KEY(key, (String) v);
            }
            case LGE_KEY:{
                return ThinkFilterBean.LGE_KEY(key, (String) v);
            }
            case BETWEEN_AND:{
                Serializable[] vs = (Serializable[]) v;
                return ThinkFilterBean.BETWEEN_AND(key,vs[0],vs[1]);
            }
            case LIKE:{
                return ThinkFilterBean.LIKE(key, (String) v);
            }
            case IS_NULL:{
                return ThinkFilterBean.IS_NULL(key);
            }
            case IS_NOT_NULL:{
                return ThinkFilterBean.IS_NOT_NULL(key);
            }
            case IN:{

                return ThinkFilterBean.IN(key,(Serializable[]) v);
            }
            case OR:{

                return ThinkFilterBean.OR(key,(Serializable[]) v);
            }
            case NOT_IN:{

                return ThinkFilterBean.NOT_IN(key,(Serializable[]) v);
            }
        }
        return null;
    }


    private int currentIndex = 0;

    protected boolean hasMore(){
        return currentIndex< len;
    }

    protected ThinkFilterBean resetIndex(){
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

    protected static ThinkFilterBean EQ(String key,Serializable v){
        return build(key).initOp(ThinkFilterOp.EQ,new Serializable[]{v});
    }

    protected static ThinkFilterBean EQ_KEY(String key,String v){
        return build(key).initOp(ThinkFilterOp.EQ,new String[]{v});
    }
    protected static ThinkFilterBean NOT_EQ(String key,Serializable v){
        return build(key).initOp(ThinkFilterOp.NOT_EQ,new Serializable[]{v});
    }

    protected static ThinkFilterBean NOT_EQ_KEY(String key,String v){
        return build(key).initOp(ThinkFilterOp.NOT_EQ_KEY,new String[]{v});
    }

    protected static ThinkFilterBean LE(String key,Serializable v){
        return build(key).initOp(ThinkFilterOp.LE,new Serializable[]{v});
    }
    protected static ThinkFilterBean LEE(String key,Serializable v){
        return build(key).initOp(ThinkFilterOp.LEE,new Serializable[]{v});
    }

    protected static ThinkFilterBean LE_KEY(String key, String v){
        return build(key).initOp(ThinkFilterOp.LE_KEY,new String[]{v});
    }
    protected static ThinkFilterBean LEE_KEY(String key,String v){
        return build(key).initOp(ThinkFilterOp.LEE_KEY,new String[]{v});
    }

    protected static ThinkFilterBean LG(String key,Serializable v){
        return build(key).initOp(ThinkFilterOp.LG,new Serializable[]{v});
    }
    protected static ThinkFilterBean LGE(String key,Serializable v){
        return build(key).initOp(ThinkFilterOp.LGE,new Serializable[]{v});
    }

    protected static ThinkFilterBean LG_KEY(String key, String v){
        return build(key).initOp(ThinkFilterOp.LG_KEY,new String[]{v});
    }
    protected static ThinkFilterBean LGE_KEY(String key,String v){
        return build(key).initOp(ThinkFilterOp.LGE_KEY,new String[]{v});
    }

    protected static ThinkFilterBean BETWEEN_AND(String key,Serializable v1,Serializable v2){
        return build(key).initOp(ThinkFilterOp.BETWEEN_AND,new Serializable[]{v1,v2});
    }

    protected static ThinkFilterBean IN(String key,Serializable... v){
        return build(key).initOp(ThinkFilterOp.IN,v);
    }


    protected static ThinkFilterBean NOT_IN(String key,Serializable... v){
        return build(key).initOp(ThinkFilterOp.NOT_IN,v);
    }

    protected static ThinkFilterBean OR(String key,Serializable... v){
        return build(key).initOp(ThinkFilterOp.OR,v);
    }

    protected static ThinkFilterBean IS_NULL(String key){
        return build(key).initOp(ThinkFilterOp.IS_NULL,new Serializable[]{});
    }

    protected static ThinkFilterBean IS_NOT_NULL(String key){
        return build(key).initOp(ThinkFilterOp.IS_NOT_NULL,new Serializable[]{});
    }

    protected static ThinkFilterBean LIKE(String key,String v){
        return build(key).initOp(ThinkFilterOp.LIKE,new String[]{v});
    }

    protected ThinkFilterBean  initOp(ThinkFilterOp op ,Serializable[] values){
        if(this.finish){
            return null;
        }
        StringBuilder sb = new StringBuilder( " ")
                .append(key)
                .append(" ");
        this.finish = true;
        this.values = values;
        this.op = op;
        switch (op){
            case EQ: {
                this.len =1 ;
                sb.append("= ? ");
                if(this.len != values.length){
                   this.safe = false;
                }
                break;
            }
            case EQ_KEY:{
                this.paramIsKey =true;
                this.len=0;
                if(values.length ==1 ){
                    sb.append("= ").append(values[0]).append(" ");
                }else{
                    this.safe =false;
                }
                break;
            }
            case NOT_EQ:{
                this.len =1;
                sb.append("<> ? ");
                if(this.len != values.length){
                    this.safe = false;
                }
                break;
            }
            case NOT_EQ_KEY:{
                this.paramIsKey = true;
                this.len=0;
                if(this.values.length == 1) {
                    sb.append("=").append(values[0]).append(" ");
                }else{
                    this.safe = false;
                }
                break;
            }
            case LE:{
                this.len = 1;
                if(this.len == this.values.length  ){
                    sb.append("< ? ");
                }else{
                    this.safe =false;
                }
                break;
            }
            case LEE:{
                this.len =1;
                if(this.len == values.length) {
                    sb.append("<= ? ");
                }else{
                    this.safe =false;
                }
                break;
            }
            case LE_KEY:{
                this.paramIsKey = true;
                this.len = 0 ;
                if(this.values.length == 1){
                    sb.append(" <").append(values[0]).append(" ");
                }else{
                    this.safe = false;
                }
                break;
            }
            case LEE_KEY:{
                this.paramIsKey = true;
                this.len = 0 ;
                if(this.values.length == 1){
                    sb.append(" <= ").append(values[0]).append(" ");
                }else{
                    this.safe = false;
                }
                break;
            }
            case LG:{
                this.len = 1 ;
                if(this.len == this.values.length  ){
                    sb.append(" > ? ");
                }else{
                    this.safe =false;
                }
                break;
            }
            case LGE:{
                this.len = 1;
                if(this.len == this.values.length  ){
                    sb.append(" >= ? ");
                }else{
                    this.safe =false;
                }
                break;
            }
            case LG_KEY:{
                this.len =0;
                this.paramIsKey =true;
                if(this.values.length == 1){
                    sb.append(" > ").append(values[0]).append(" ");
                }else{
                    this.safe = false;
                }
                break;
            }
            case LGE_KEY:{
                this.len =0;
                this.paramIsKey =true;
                if(this.values.length == 1){
                    sb.append(" >=").append(values[0]).append(" ");
                }else{
                    this.safe = false;
                }
                break;
            }
            case BETWEEN_AND:{
                this.len =2;
                if(this.values.length == this.len){
                    sb.append( " between ? and ? ");
                }else {
                    this.safe = false;
                }
                break;
            }
            case IN:{
                this.len = values.length;
                if(this.len == values.length){
                    int i= 0 ;
                    sb.append( "in (");
                    while (i < this.len){
                        if(i >0){
                            sb.append(",");
                        }
                        sb.append(" ? ");
                        i ++;

                    }
                    sb.append(") ");

                }
                break;
            }
            case NOT_IN:{
                this.len = values.length;
                if(this.len == values.length){
                    int i= 0 ;
                    sb.append( "not in (");
                    while (i < this.len){
                        if(i >0){
                            sb.append(",");
                        }
                        sb.append(" ? ");
                        i ++;

                    }
                    sb.append(") ");

                }
                break;

            }
            case OR:{
                //or 逻辑自动转成IN
                this.len = values.length;
                if(this.len == values.length){
                    int i= 0 ;
                    sb.append( "in (");
                    while (i < this.len){
                        if(i >0){
                            sb.append(",");
                        }
                        sb.append(" ? ");
                        i ++;
                    }
                    sb.append(") ");
                }
                break;
            }
            case IS_NULL:{
                this.len = 0;
                sb.append(" IS NULL");
                break;
            }
            case IS_NOT_NULL:{
                this.len = 0;
                sb.append(" IS NOT NULL");
                break;
            }
            case LIKE:{
                this.len = 1;
                if(this.len == values.length){
                    sb.append(" LIKE ? ");
                }else{
                    this.safe = false;
                }
                break;
            }
        }
        this.queryPart = sb.toString();
        return this;
    }


    public boolean isSafe() {
        return safe;
    }

    public String getKey() {
        return key;
    }

    public ThinkFilterOp getOp() {
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

    public String getQueryPart() {
        return queryPart;
    }

    public boolean isFinish() {
        return finish;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
