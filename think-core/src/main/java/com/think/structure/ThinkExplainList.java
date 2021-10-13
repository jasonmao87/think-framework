package com.think.structure;

import com.think.core.bean.TEnumExplain;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Date :2021/9/27
 * @Name :ThinkExplainList
 * @Description : 请输入
 */
public class ThinkExplainList implements Serializable {
    private static final long serialVersionUID = 2010101010101010020L;


    @ApiModelProperty(hidden = true)
    private  boolean init = false;


    @ApiModelProperty(hidden = true)
    private List<TEnumExplain> explainDetail = null;

    public void add(TEnumExplain explain){
        if(explainDetail == null){
            explainDetail = new ArrayList<>();
        }
        explainDetail.add(explain);
    }

    @ApiModelProperty(hidden = true)
    public int getSize() {
        if(explainDetail!=null) {
            return this.explainDetail.size();
        }
        return 0;
    }

    public List<TEnumExplain> getExplainDetail() {
        return explainDetail;
    }


    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
