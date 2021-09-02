
package com.think.data.model;

import com.think.core.annotations.Remark;
import com.think.core.annotations.bean.ThinkColumn;
import com.think.data.Manager;
import com.think.data.exception.ThinkDataModelException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

@Remark("表模型")
public class ThinkTableModel implements Serializable {


    public final String getDbType() {
        return "mysql";
    }

    @Remark("映射类")
    private Class beanClass ;

    @Remark("表名")
    private String tableName ;

    @Remark("备注")
    private String tableComment;

    @Remark("数据源id")
    private String dataSourceId ;

    @Remark("是否数据分表")
    private boolean partitionAble;

    @Remark("主键自增")
    private boolean autoIncPK;

    @Remark("是否按年分表")
    private boolean yearSplitAble =false;

//    @Remark("启用版本管理")
//    private boolean versionAble = false;

    @Remark("索引")
    private ThinkIndexModel[] indexModels = null ;
    @Remark("列")
    private ThinkColumnModel[] columnModels = null;


    private String[] fastMatchKeys = null;

    protected ThinkTableModel(Class beanClass, String tableName, String tableComment  ) {
        this.beanClass = beanClass;
        this.tableComment = tableComment;
        this.tableName = tableName;
    }


    private void addFastMatchKey(String key){
        if (fastMatchKeys == null) {
            fastMatchKeys = new String[]{key};
        }else{
            int len = fastMatchKeys.length;
            String[] newArr = new String[len+1];
            for(int i = 0 ; i < len;i++){
                newArr[i] = fastMatchKeys[i];
            }
            newArr[len] = key;
            fastMatchKeys = newArr;
        }

    }

    protected void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    protected void setTableName(String tableName) {
        this.tableName = tableName;
    }

    protected void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    protected void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    protected void setIndexModels(ThinkIndexModel[] indexModels) {
        this.indexModels = indexModels;
    }

    protected void setColumnModels(ThinkColumnModel[] columnModels) {
        ThinkColumnModel thinkLinkedIdModel = null;
        int indexOfThinkLinkedId = -1 ;
        this.fastMatchKeys = null;
        this.columnModels = columnModels;
        int index = 0 ;
        for (ThinkColumnModel columnModel : this.columnModels) {
            if(columnModel.isThinkLinkedId()){
                indexOfThinkLinkedId = index;
            }
            if(columnModel.isFastMatchAble()){
                this.addFastMatchKey(columnModel.getKey());
            }
            index ++ ;
        }
        if(indexOfThinkLinkedId > 0){
            thinkLinkedIdModel = this.columnModels[indexOfThinkLinkedId];
            this.columnModels[indexOfThinkLinkedId] = this.columnModels[index -1 ] ;
            this.columnModels[index -1] = thinkLinkedIdModel;
        }




    }

    protected void setAutoIncPK(boolean autoIncPK) {
        this.autoIncPK = autoIncPK;
    }

    protected void setPartitionAble(boolean partitionAble) {
        this.partitionAble = partitionAble;
    }
    protected void setYearSplitAble(boolean yearSplitAble) {
        this.yearSplitAble = yearSplitAble;
    }


    public boolean containsSortKey(String key){
        if(fastMatchKeys != null){
            for(String s : fastMatchKeys){
                if(s.equalsIgnoreCase(key)){
                    return true;
                }
            }

        }
        return false;
    }

//    public String getSortKeyName(String key){
//        if(containsSortKey(key)) {
//            return "fs_" + key;
//        }
//        return null;
//    }

    public String[] getSortKeyArray(){
        if(this.fastMatchKeys !=null) {
            return this.fastMatchKeys;
        }
        return new String[0];
    }
//    protected void setVersionAble(boolean versionAble) {
//        this.versionAble = versionAble;
//    }

    public Class getBeanClass() {
        return beanClass;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }

    public ThinkIndexModel[] getIndexModels() {
        return indexModels;
    }

    public ThinkColumnModel[] getColumnModels() {

        if(!Manager.isThinkLinkedIdSupportAble()){
        }

        return columnModels;
    }

    /**
     * 索引数量
     * @return
     */
    public int indexSize(){
        return indexModels.length;
    }


    public boolean isYearSplitAble() {
        return this.yearSplitAble;
    }



    /**
     * 列总数
     * @return
     */
    public int columnLength(){
        return columnModels.length;
    }

//    public boolean isVersionAble() {
//        return versionAble;
//    }

    /**
     * 是否包含列
     * @param k
     * @return
     */
    public boolean containsKey(String k) {
        if(columnModels == null){
            throw new ThinkDataModelException("尚未初始化列模型");
        }
        for(ThinkColumnModel modal : columnModels){
            if(modal.getKey().equalsIgnoreCase(k)){
                return true;
            }
        }
        return false;
    }

    public boolean containsKeys(String[] keys){
        for(String k : keys){
            if(this.containsKey(k)){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }

    /**
     * 根据KEY 获取列模型
     * @param key
     * @return
     */
    public ThinkColumnModel getKey(String key){
        if(columnModels == null){
            throw new ThinkDataModelException("尚未初始化列模型");
        }
        for(ThinkColumnModel modal : columnModels){
           //L.info("check K |{}| --- for each in |{}|" ,key,modal.getKey());
            if(modal.getKey().equalsIgnoreCase(key)){
                return modal;
            }
        }
        return null;
    }

    /**
     * 根据indexValue 倒叙排列
     */
    protected void resortColumns(){
        Arrays.sort(columnModels, new Comparator<ThinkColumnModel>() {
            @Override
            public int compare(ThinkColumnModel o1, ThinkColumnModel o2) {
                if( o1.getIndexValue() > o2.getIndexValue()){
                    return -1;
                }else if(o1.getIndexValue() < o2.getIndexValue()){
                    return 1;
                }
                return 0;
            }
        });
    }

    public boolean isAutoIncPK() {
        return autoIncPK;
    }

    public boolean isPartitionAble() {
        return partitionAble;
    }
}