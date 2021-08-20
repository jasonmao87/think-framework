package com.think.data.provider;


import java.util.*;

public class SQLFormatterUtil {

    public static void main(String[] args){
        String sql=" SELECT * from abcsdadfa where i=1 and xda = i  and  c in (?,?,?)";
        SqlFormat(sql);
    }
    public static Map<String, String> map = new HashMap<String, String>();//需要换行的字段
    public static Map<String, String> bracket= new HashMap<String, String>();//括号前关键字
    static{
        map.put("select", "select");
        map.put("from", "from");
        map.put("group by", "group by");
        map.put("where", "where");
        map.put("order by", "order by");

        bracket.put("not", "not");

        //map.put("(", "(");
        map.put(")", ")");

    }

    public static String SqlFormat(String sql){
        sql = sql.trim().replaceAll(",", ", ").replaceAll(" +", " ")
                .replaceAll("\\s+", " ").replace(" ", " |")
                .replace("(", "|(|").replace(")", "|)|")
                .replace("| |", " |").replace("||", "|");
        sql = sql.toLowerCase();
        int sj=0;//

        String[] sqlArray  = sql.split("\\|");
        int length = sqlArray.length;
        Stack stack = new Stack();
        Stack stackTemp = new Stack();

        for(int i=0;i<length; i++){
            if("(".equals(sqlArray[i].trim())){
                if(map.get(sqlArray[i+1].trim())!=null){//后有关键字
                    stack.push("(" +
                            "");
                    printlnSql(sqlArray[i], sj);
                }else if(bracket.get(sqlArray[i-1].trim())!=null){//前有关键字
                    stack.push("(");
                    printlnSql(sqlArray[i], sj);
                }else{
                    stackTemp.push("(");
                    System.out.println(sqlArray[i]);
                }
            }else if(")".equals(sqlArray[i].trim())){
                if(stackTemp.size()>0){
                    stackTemp.pop();
                    System.out.println(sqlArray[i]);
                }else{
                    stackTemp.pop();
                    sj=stack.size();
                    printlnSql(sqlArray[i], sj);
                }
            }else if("".equals(sqlArray[i].trim())){

            }else if("(".equals(sqlArray[i].trim()) && map.get(sqlArray[i-1].trim())!=null){

            }else if(map.get(sqlArray[i].trim())!=null){
                printlnSql(sqlArray[i], sj);
            }else{
                System.out.print(sqlArray[i]);
            }
        }
        return sql;
    }

    public static void printlnSql(String sqlSub, int i){
        if(i>0){
            System.out.println("\n");
            for(int j=0; j<i; j++){
                System.out.println("\t\t");
            }
        }else{
            System.out.println('\n'+sqlSub);
        }
    }
}