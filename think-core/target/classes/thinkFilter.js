/**
 * 用于前端 配合 动态的构建筛选条件 ， 示例代码
 *  建议在各类框架中 封装
 * JASONMAO87@hotmail.com
 * 2020-8-25
 * @type {boolean}
 */

let debugEnable = true;

Array.prototype.pushModal=function(filterBeanModal){
    if(this.length == 0 ){
        nDebug( filterBeanModal,true);
        this.push( filterBeanModal) ;
    }else{
        let index = 1;
        let key = filterBeanModal.key;
        this.forEach(function (value) {
            if(value.key ==  filterBeanModal.key ){
                filterBeanModal.key ='#' + index + "##" + key ;
            }
            index ++ ;
        });
        this.push( filterBeanModal)
    }
}

function ThinkWebFilter( ){
    this.filterBody = [];
    this._eq = function (k , v , dataType) {
        nDebug("WebFilter 添加参数 "+ k + " == " + v + " type[ "+ dataType+"]") ;
        let modal = new filterBeanModel(k,webSqlOp.opEq,v,dataType) ;
        this.filterBody.pushModal(modal);
        return this;
    };
    this.eqStr = function (k,v) {
        return this._eq(k,v,webDataType.STRING);
    };
    this.eqNumber = function(k ,v ){
        return this._eq(k,v,webDataType.NUMBER);
    };
    this.eqLong = function(k ,v ){
        return this._eq(k,v,webDataType.LONG);
    };
    this.eqInt = function(k,v){
        return this._eq(k,v,webDataType.INT);
    };
    this.eqDate = function(k,v){
        return this._eq(k,v,webDataType.DATE);
    };
    this.eqBool = function (k,v) {
        return this._eq(k,v,webDataType.BOOLEAN);
    };
    /**--------------------------------------------------------------------------------------------------------------------------------*/
    this._notEq = function(k, v, dataType){
        nWarn("不建议使用此方法！！ 不等于用不好可能会存在一定的性能问题哦 ！！！",true) ;
        let modal = new filterBeanModel(k,webSqlOp.opNotEq,v,dataType);
        this.filterBody.pushModal(modal);
        return this;
    };

    this.notEqStr = function (k,v) {
        return this._notEq(k,v,webDataType.STRING);
    };
    this.notEqNumber = function(k ,v ){
        return this._notEq(k,v,webDataType.NUMBER);
    };
    this.notEqLong = function(k ,v ){
        return this._notEq(k,v,webDataType.LONG);
    };
    this.notEqInt = function(k,v){
        return this._notEq(k,v,webDataType.INT);
    };
    this.notEqDate = function(k,v){
        return this._notEq(k,v,webDataType.DATE);
    };
    this.notEqBool = function (k,v) {
        return this._eq(k,v,webDataType.BOOLEAN);
    };

    /**--------------------------------------------------------------------------------------------------------------------------------*/
    /**
     * k 的值等于 vk值相等
     * @param k
     * @param vk
     */
    this.eqKey = function(k,vk){
        let modal = new filterBeanModel(k,webSqlOp.opEqKey ,vk,webDataType.KEY);
        this.filterBody.pushModal(modal);
        return this;
    };
    /**
     * k 的值 不等于某个vkey值相等
     * @param k
     * @param vk
     */
    this.notEqKey = function(k,vk){
        let modal = new filterBeanModel(k,webSqlOp.opNotEqKey  ,vk,webDataType.KEY)
        this.filterBody.pushModal(modal);
        return this;
    };
    /**
     * in
     * @param k
     * @param values
     * @returns {ThinkWebFilter}
     */
    this.in = function(k , values ){
        if(values instanceof  Array){
            let modal = new filterBeanModel(k,webSqlOp.opIn,values,webDataType.ARRAY);
            this.filterBody.pushModal(modal);
        }else{
            nWarn("操作没有执行！in方法的 values 参数必须是数组",true);
        }
        return this;

    };
    // this.notIn = function(k , values ){
    //     if(values instanceof  Array){
    //         let modal = new filterBeanModel(k,webSqlOp.opNotIn,values,webDataType.ARRAY);
    //         this.filterBody.pushModal(modal);
    //     }else{
    //         console.warn("操作没有执行！not in方法的 values 参数必须是数组");
    //     }
    //     return this;
    // };

    this.or = function(k ,values){
        if(values instanceof  Array){
            let modal = new filterBeanModel(k,webSqlOp.opOr,values,webDataType.ARRAY);
            this.filterBody.pushModal(modal);
        }else{
            nWarn("操作没有执行！or方法的 values 参数必须是数组",true);
        }
        return this;
    };

    this.like = function(k ,likestr){
        let modal = new filterBeanModel(k,webSqlOp.opLike,likestr,webDataType.STRING);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.lessThanDate = function(k,dateStr) {
        let modal = new filterBeanModel(k,webSqlOp.opLessThan,dateStr,webDataType.DATE);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.lessThanNumber =  function(k,number) {
        let modal = new filterBeanModel(k,webSqlOp.opLessThan,number,webDataType.NUMBER);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.lessThanKey = function (k,vk) {
        let modal = new filterBeanModel(k,webSqlOp.opLessThanKey,vk,webDataType.KEY);
        this.filterBody.pushModal(modal);
        return this;

    }
    this.lessThanAndEqDate = function (k,dateStr) {
        let modal = new filterBeanModel(k,webSqlOp.opLessAndEquals,dateStr,webDataType.DATE);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.lessThanAndEqNumber =  function (k,number) {
        let modal = new filterBeanModel(k,webSqlOp.opLessAndEquals,number,webDataType.NUMBER);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.lessThanAndEqKey = function (k,vk) {
        let modal = new filterBeanModel(k,webSqlOp.opLessThanAndEqualsKey,vk,webDataType.KEY);
        this.filterBody.pushModal(modal);
        return this;
    }
    /**---------------------------------------------------------------------------------------------------------*/
    this.largeThanDate = function (k,dateStr) {
        let modal = new filterBeanModel(k,webSqlOp.opLargeThan,dateStr,webDataType.DATE);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.largeThanNumber =  function (k,number) {
        let modal = new filterBeanModel(k,webSqlOp.opLargeThan,number,webDataType.NUMBER);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.largeThanKey = function (k,vk) {
        let modal = new filterBeanModel(k,webSqlOp.opLargeThanKey,vk,webDataType.KEY);
        this.filterBody.pushModal(modal);
        return this;
    }

    this.largeThanAndEqDate = function (k,dateStr) {
        let modal = new filterBeanModel(k,webSqlOp.opLargeAndEquals,dateStr,webDataType.DATE);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.largeThanAndEqNumber =  function (k,number) {
        let modal = new filterBeanModel(k,webSqlOp.opLargeAndEquals,number,webDataType.NUMBER);
        this.filterBody.pushModal(modal);
        return this;
    };
    this.largeThanAndEqKey = function (k,vk) {
        let modal = new filterBeanModel(k,webSqlOp.opLargeThanAndEqualsKey,vk,webDataType.KEY);
        this.filterBody.pushModal(modal);
        return this;
    }


    this.betweenAnd = function(k,v1,v2){
        let vk = [v1,v2];
        let modal = new filterBeanModel(k,webSqlOp.opBetweenAnd,vk,webDataType.ARRAY);
        this.filterBody.pushModal(modal);
        return this;
    }
    this.clear = function(){
        this.filterBody = [];
    };


    this._bodyJson =function(){
        let tempArray = [];
        for(let i = 0 ; i < this.filterBody.length ; i ++) {
            if(this.filterBody[i].key == null ){
                console.info("remove it " + i);
                //this.filterBody.remove(i)
            }else{
                tempArray.push(this.filterBody[i]);
            }
        }
        this.filterBody = [];

        this.filterBody = tempArray;
        let body = "\"filterBody\" : {";
        for(let i = 0 ; i < this.filterBody.length ; i ++){
            let modal = this.filterBody[i];
            let item = "\n\t\t";
            item += "\""+modal.key+"\": {" ;
            // op
            item += "\n\t\t\t\"op\":\""+modal.op +"\"";
            item +=",";
            //type
            item += "\n\t\t\t\"type\":\""+modal.type +"\" ";
            item +=",";
            // v
            if(modal.op != "OR" && modal.op !="IN" && modal.op!="NOT_IN" && modal.op != "BETWEEN_AND"){
                item += "\n\t\t\t\"v\":";
                if(modal.type == "string" || modal.type == "date" || modal.type == "KEY"){
                    item += "\""+modal.Value+"\"";
                }else{
                    item += modal.Value;
                }
            }else{
                item += "\n\t\t\t\"v\": [";
                for(let j = 0 ; j <modal.Value.length ; j ++){
                    if(j >0){
                        item += ",";
                    }
                    let v = modal.Value[j];
                    if(isNaN(v)){
                        item +="\""+v+"\"";
                    }else{
                        item +=v + "";
                    }
                }
                item += "]"
            }
            item+= "\n\t\t}";
            if(i >0){
                body +="," ;
            }
            body += item;
        }
        body += "\n\t} \n";
        return body ;

    };
    this.ascFilter= function(limit , lastId ){
        if(lastId){ // 做排序 的处理  filter 中 apped id > lastId
            if(isNaN( lastId)){
                this.largeThanDate("id",lastId)
            }else{
                this.largeThanNumber("id",lastId)
            }
        }

        let filterJson = "{" +
            "\n\t\"limit\" :" + limit +" ," +
            "\n\t\"sortQuery\": { \"key\":\"id\" ,\"sort\": \"asc\"   } ,\n\t"
            + this._bodyJson() +
            "}"
        return filterJson
    };
    this.descFilter =function(limit ,lastId){
        if(lastId){ // 做排序 的处理  filter 中 apped id > lastId
            if(isNaN( lastId)){
                this.lessThanNumber("id",lastId)
            }else{
                this.lessThanNumber("id",lastId)
            }
        }
        let filterJson = "{" +
            "\n\t\"limit\" :" + limit +" ," +
            "\n\t\"sortQuery\": { \"key\":\"id\" ,\"sort\": \"desc\"    } ," +
            "\n\t"
            + this._bodyJson() +
            "}"
        return filterJson
    };

    this.customASCFilter = function(limit ,sortKey ,lastValue){
        if(lastValue){ // 做排序 的处理  filter 中 apped id > lastId
            if(isNaN( lastValue)){
                this.largeThanDate(sortKey,lastValue)
            }else{
                this.largeThanNumber(sortKey,lastValue)
            }
        }

        let filterJson = "{\n\t" +
            "\"limit\" :" + limit +" ,\n\t\"sortQuery\": { \"key\":\""+sortKey+"\" ,\"sort\": \"desc\"    } ,\n\t"
            + this._bodyJson() +
            "}"
        return filterJson
    };
    /**
     * 用于 自定义 正序排序的搜索 ，参数 : limit(分页大小), sortKey 排序的字段， lastValue 上一页的最后一个值（只允许整数 或日期 字符串）
     * @param limit
     * @param sortKey
     * @param lastValue
     * @returns {string}
     */
    this.customDescFilter = function(limit ,sortKey ,lastValue){
        if(lastValue){ // 做排序 的处理  filter 中 append id > lastId
            if(isNaN( lastValue)){
                this.lessThanNumber(sortKey,lastValue)
            }else{
                this.lessThanNumber(sortKey,lastValue)
            }
        }
        let filterJson = "{" +
            "\n\t\"limit\" :" + limit +" ,\n\t\"sortQuery\": { \"key\":\""+sortKey+"\" ,\"sort\": \"desc\"   } ,\n\t"
            + this._bodyJson() +
            "}"
        return filterJson
    };

    /**
     * 用于 查询count的filter
     * @returns {string}
     */
    this.countFilter = function(){
        let filterJson = "{" +
            "\"limit\" : -1 ,"
            + this._bodyJson()
            +"}";
        return filterJson
    };

    /**
     * 支持操作符
     * @type {{opEq: string, opNotEq: string, opIn: string, opNotIn: string, opOr: string, opLike: string, opLargeThan: string, opLargeAndEquals: string, opLessThan: string, opLessAndEquals: string}}
     */
    let webSqlOp ={
        opEq                    :   "EQ",
        opNotEq                 :   "NOT_EQ",
        opEqKey                 :   "EQ_KEY",
        opNotEqKey              :   "NOT_EQ_KEY",
        opIn                    :   "IN",
        //opNotIn               :   "NOT_IN",
        opOr                    :   "OR",
        opLike                  :   "LIKE",
        opLargeThan             :   "LG",
        opLargeAndEquals        :   "LGE",
        opLargeThanKey          :   "LG_KEY",
        opLargeThanAndEqualsKey :   "LGE_KEY",
        opLessThan              :   "LE",
        opLessAndEquals         :   "LEE",
        opLessThanKey           :   "LE_KEY",
        opLessThanAndEqualsKey  :   "LEE_KEY",
        opBetweenAnd            :   "BETWEEN_AND"

    } ;

    /**
     * 支持数据类型
     * @type {{NUMBER: string, STRING: string, DATA: string, INT: string, LONG: string, DOUBLE: string, BOOLEAN: string}}
     */
    let webDataType = {
        NUMBER      :   "number"    ,
        STRING      :   "string"    ,
        DATE        :   "date"      ,
        INT         :   "int"       ,
        LONG        :   "long"      ,
        DOUBLE      :   "double"    ,
        BOOLEAN     :   "boolean"   ,
        ARRAY       :   "array"     ,
        KEY         :   "KEY"

    }

    /**
     * filter 模型
     * @param _key
     * @param _op
     * @param _keyValue
     * @param _dataType
     */
    function filterBeanModel(_key,_op,_value ,_dataType){
        if(isNaN(_value)){
            if(_value.indexOf('"')>-1){
                console.warn("不允许输入 -上引号\"");
                return null;
            }
            if(_value.indexOf("'")>-1){
                console.warn("不允许输入 --上引号 \'");
                return null;
            }
        }




        this.key        =  _key ;
        this.op         =  _op;
        this.Value      = _value;
        this.type       =  _dataType;
        nDebug("添加了新的参数：[值类型："+(this.type )+"]" );
        nDebug("\t 关键字 :" + this.key + "" ,true);
        nDebug("\t 操作符 :" + this.op + " " ,true);
        nDebug("\t 值 :" + this.Value + " " ,true);
        nDebug("\t 值数据类型 :" + this.type + " " ,true)



    }
}




function defaultDateFormate(dateString){
    let date = new Date(dateString);
    let str = date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate();
    return str;
}

function nDebug(info ,ig){
    if(debugEnable){
        if(ig){
            console.info(  info)
        }else{
            console.info("来自ThinkFilter的调试日志 :"  + info)
        }
    }
}

function nWarn(info ,ig){
    if(debugEnable){
        if(ig){
            console.warn(  info)
        }else{
            console.warn("来自ThinkFilter的警告日志 :"  + info)
        }
    }
}
