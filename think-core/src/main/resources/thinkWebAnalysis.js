
function dataModel(){

    if(!(this instanceof  dataModel)){
        return new dataModel();
    }

    this.screenOuterWidth = window.outerWidth;
    this.screenOuterHeight =window.outerHeight ;
    this.screenInnerWidth = window.innerWidth;
    this.screenInnerHeight = window.innerHeight;
    this.loadTime = new Date().getTime();
    this.platformString = navigator.platform;
    this.UAString  = navigator.userAgent ;
    this.url = window.location.href;
    this.isNewUv = true;

    this.setNewUv = function( isNew){
        this.isNewUv = isNew;
        return this;
    }



}



console.log(new dataModel())


let doAnalysis = function(){
    console.log("do analysis ")

    let isNewUv = true;

    // let screenOuterWidth = window.outerWidth;
    // let screenOuterHeight =window.outerHeight ;
    // let screenInnerWidth = window.innerWidth;
    // let screenInnerHeight = window.innerHeight;
    //
    // let loadTime = new Date().getTime();
    // let platformString = navigator.platform;
    // let uaString  = navigator.userAgent ;
    // let url = window.location.href;
    let lastVisit = window.localStorage.getItem("thinkLastVisit");
    if(lastVisit!= null && lastVisit == todayString()){
        isNewUv = false;
    }else{
        window.localStorage.setItem("thinkLastVisit" ,todayString());
    }
    let thinkVisitPageCount  = window.localStorage.getItem("thinkVisitPageCount");
    window.localStorage.setItem("thinkVisitPageCount" ,thinkVisitPageCount );
    //最后活跃时间
    let thinkLastActive = window.localStorage.getItem("thinkLastActive");
    window.localStorage.setItem("thinkLastActive" , new Date().getTime());


}
/**
 * 添加一个Onload 事件
 * @param func
 */
function addLoadEventByAnalysis(func){
    let sourceOnload =window.onload;
    if(typeof window.onload!='function'){
        window.onload=func;
    }else{
        window.onload=function(){
            sourceOnload();
            func();
        }
    }
}

window.onload = function(){
    alert(123);
}

/***
 * shuqianwanhangdaima
 * @param dataModel
 */

window.onload = function(){
    alert(1)
}


function reportData(dataModel){

}
/**
 * 获得一个 格式化的日期字符串
 * @returns {string}
 */
function todayString(){
    let now = new Date();
    return dateString(now);
}
function dateString(date){
    return date.getFullYear() +'' + (date.getMonth() +1) + '' + date.getDate();
}
function isToday(ms){
    if(ms == null){
        return false;
    }
    let t  = new Date(ms);
    return dateString(t) == todayString();
}

addLoadEventByAnalysis(doAnalysis);
