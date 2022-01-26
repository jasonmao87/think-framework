package com.think.data.model;

import com.think.core.annotations.Remark;
import com.think.data.Manager;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Type;

@Slf4j
public class ThinkColumnModel implements Serializable {

    @Remark("字段")
    private String key ;

    @Remark("映射的建表sql类型")
    private String sqlTypeString ;

    @Remark("类型 ")
    private Type type;

    @Remark("是TFlowState模型")
    private boolean stateModel = false;

//    @Remark("需要针对参数值进行校验")
//    private boolean verificationRequired  ;

    @Remark("是否允许空")
    private boolean nullable;

    @Remark("长度")
    private int length ;

    @Remark("是否是主键")
    private boolean pk ;

    @Remark("权重")
    private int indexValue ;

    @Remark("备注")
    private String comment ;

    @Remark("是否有索引")
    private boolean hasIndex = false;

    @Remark("索引模型")
    private ThinkIndexModel indexModal ;


    @Remark("需要脱敏处理")
    private boolean sensitive =false;

    @Remark("是否允许修改")
    private boolean editAble = true;

    @Remark("快速排序支持")
    private boolean fastMatchAble = false;

    @Remark("默认值")
    private String defaultValue;

    @Remark("Date类型不要设置默认值")
    private boolean noSetDateDefaultValue =false;

    @Remark("使用text作为key的类型")
    private boolean usingText = false;

    @Remark("是否枚举对象")
    private boolean enumState ;


    protected ThinkColumnModel() {
    }

    public boolean isHasIndex() {
        if(log.isTraceEnabled()){
            log.trace(" 检查 {} 是否有索引 index -> {}" , this.key,this.hasIndex);
        }
        return this.hasIndex;
    }


    /**
     * index 生效 需依赖的 key，如果 是 null ，可以直接使用
     * @return
     */
    public String leftIndexKey(){
        if(this.isPk()){
            return this.key;
        }
        if(this.indexModal == null){
            return null;
        }
        String ks[] = this.indexModal.getKeys();
        if(ks.length == 1){
            return this.key;
        }else{
            String leftKey = ks[0] ;
            for (String k : ks){
                if(k.equalsIgnoreCase(this.key)){
                    break;
                }else {
                    leftKey = k;
                }
            }
            return leftKey;
        }
    }

    protected void setIndexModal(ThinkIndexModel indexModal){
        if(log.isTraceEnabled()){
            log.trace( "{}被注入索引模型",this.key);
        }
        this.hasIndex = true;
        this.indexModal = indexModal;
    }

    public void setEnumState(boolean enumState) {
        this.enumState = enumState;
    }

    public void setSqlTypeString(String sqlTypeString ) {
        this.sqlTypeString = sqlTypeString;
    }

    protected void setComment(String comment) {
        this.comment = comment;
    }

    protected void setType(Type type){
        this.type = type;
    }

    protected void setKey(String key) {
        this.key = key;
    }

    protected void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    protected void setLength(int length) {
        this.length = length;
    }

    protected void setPk(boolean pk) {
        this.pk = pk;
        this.hasIndex = true;
    }

    public void setNoSetDateDefaultValue(boolean noSetDateDefaultValue) {
        this.noSetDateDefaultValue = noSetDateDefaultValue;
    }

    protected void setSensitive(boolean sensitive) {
        if(this.type == String.class) {
            this.sensitive = sensitive;
        }
    }

//    protected void setVerificationRequired(boolean verificationRequired) {
//        this.verificationRequired = verificationRequired;
//    }

    protected void setEditAble(boolean editAble) {
        this.editAble = editAble;
    }

    protected void setStateModel(boolean stateModel) {
        this.stateModel = stateModel;
    }

    public void setFastMatchAble(boolean fastMatchAble) {
        this.fastMatchAble = fastMatchAble;
    }

    protected void setDefaultValue(String v){
        this.defaultValue = v;
    }
    public boolean isSensitive() {
        return sensitive;
    }


    public boolean isEnumState() {
        return enumState;
    }

    protected void setIndexValue(int indexValue) {
        this.indexValue = indexValue;
    }

    public boolean isEditAble() {
        return editAble;
    }

    public String getKey() {
        return key;
    }

    public String getFastMatchKeyWhileExits(){
        if(isFastMatchAble()){
            return "fs_" + key;
        }
        return null;
    }

    public String getSecondaryFastMatchKeyWhileExits(){
        if(isFastMatchAble()){
            return "fss_" + key;
        }
        return null;
    }

    public String getSqlTypeString( ) {
        /**
        {"autoIncPK":false,"beanClass":"com.think.test.TbHello","
            columnModals":[
            {"hasIndex":true,"indexValue":9999999,"key":"id","length":32,"nullable":false,"privateKey":true},
            {"hasIndex":false,"indexValue":0,"key":"name","length":32,"nullable":false,"privateKey":false},
            {"hasIndex":false,"indexValue":0,"key":"hellop","length":32,"nullable":false,"privateKey":false},
         {"hasIndex":false,"indexValue":0,"key":"deleteState","length":24,"nullable":false,"privateKey":false},{"hasIndex":false,"indexValue":0,"key":"deleteTime","length":24,"nullable":false,"privateKey":false},{"hasIndex":false,"indexValue":0,"key":"createTime","length":24,"nullable":false,"privateKey":false},{"hasIndex":false,"indexValue":0,"key":"lastUpdateTime","length":24,"nullable":false,"privateKey":false}],"dataSourceId":"DEFAULT","splitTable":false,"tableComment":"","tableName":"tb_think_hello"}
        */
        return this.sqlTypeString ;


    }

    public boolean isNullable() {
        return nullable;
    }

    public int getLength() {
        if(isEnumState()){
            return 32;
        }
        if (this.getType().getTypeName().equalsIgnoreCase("boolean")) {
            return 1;
        }
        if(this.isUsingText()){
            //mediumtext
            return 16777215;
        }

        return length;
    }

    public boolean isPk() {
        return pk;
    }

    public int getIndexValue() {
        return indexValue;
    }

    public String getComment() {
        if(comment!=null){
            comment = comment.replaceAll("'","^").replaceAll("\"","^");
        }
        return comment;
    }

    public ThinkIndexModel getIndexModal() {
        return indexModal;
    }

    /**
     * 只允许string
     * @return
     */
    public boolean isFastMatchAble() {
        if(this.isStateModel()){
            return false;
        }
        if(this.fastMatchAble) {
            if (this.isHasIndex()) {
                return false;
            }
            if(this.length > 64){
                return false;
            }

            if (this.getType().getTypeName().contains("String") ) {
                return fastMatchAble;
            }
        }
        return false;
    }

    public Type getType() {
        return type;
    }

//    public boolean isVerificationRequired() {
//        return verificationRequired;
//    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isUsingText() {
        return usingText;
    }

    public void setUsingText(boolean usingText) {
        this.usingText = usingText;
    }

    public boolean isThinkLinkedId(){
        return key.equalsIgnoreCase("thinkLinkedId");
    }

    public boolean isStateModel() {
        return stateModel;
    }
}
