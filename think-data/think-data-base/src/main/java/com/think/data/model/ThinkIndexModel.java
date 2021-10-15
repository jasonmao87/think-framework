package com.think.data.model;

import com.think.common.util.StringUtil;
import com.think.core.annotations.Remark;

import java.io.Serializable;

public class ThinkIndexModel implements Serializable {

    private static final long serialVersionUID = -142708136317928217L;
    @Remark("索引包含字段")
    private String[] keys;

    @Remark("唯一值约束")
    private boolean uk ;

    @Remark("索引名")
    private String indexName;

    protected ThinkIndexModel(String[] keys, boolean uk) {
        this.keys = keys;
        this.uk = uk;
        if(keys.length>0) {
            StringBuilder sb = new StringBuilder();
            for (String k : keys) {
                sb.append(k).append("_");
            }
            sb.append(uk ? "UNIQUE_INDEX" : "INDEX");
            this.indexName = sb.toString();
            if(indexName.length() >48){
                this.indexName = (uk ? "UNIQUE_INDEX" : "INDEX") + StringUtil.randomStr(16);
            }
        }else{
            this.indexName = "";
        }
    }


    public String getIndexName() {
        return indexName;
    }

    public String[] getKeys() {
        return keys;
    }

    public boolean isUk() {
        return uk;
    }


    public String toString(){
        String str = "[indexModal : " +indexName;

        str += "\n\t" ;
        str +="\n\tUK = " + uk;
        return str;


    }
}
