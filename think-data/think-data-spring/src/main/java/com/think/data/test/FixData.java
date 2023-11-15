package com.think.data.test;

import com.think.common.util.security.DesensitizationUtil;

import java.sql.*;

/**
 * @author : JasonMao
 * @version : 1.0
 * @date : 2023/3/3 19:01
 * @description :
 */
public class FixData {

    private static final String SYS ="`thinkdid-cloud-system`";
    private static final String HOS = "`thinkdid-cloud-hospital`";


    /**
     * HOS
     *  tb_hospital_user   name , mobilePhone
     *
     *
     *  SYSTEM
     *      tb_system_core_account  mobilePhone
     *
     *
     *
     */


    private static Connection connection;
//    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        Class.forName("com.mysql.cj.jdbc.Driver");
//        connection = DriverManager.getConnection("jdbc:mysql://xxxxxxxxxxxxxxxxx8.136.243.133:3306/" +
//                "thinkdid-cloud-hospital?generateSimpleParameterMetadata=true&usingSSL=false&serverTimezone=Asia/Shanghai",
//                "root", "Think@mysql_2021");
//        final Statement statement = connection.createStatement();
//
//        fixSystemAccount();
////
//        tbHospitalUserFix();
////        final ResultSet resultSet = statement.executeQuery("select id ,mobilePhone , name  from `thinkdid-cloud-hospital`.tb_hospital_user_A0  where  version >=0");
////        while (resultSet.next()) {
////            System.out.println(resultSet.getLong("id"));
////            System.out.println(fixSString( resultSet.getString("mobilePhone")));
////            System.out.println(resultSet.getString("name"));
////        }
////
//
//    }



    public static final Statement  getSt() throws SQLException {
        return connection.createStatement();
    }

    public static final PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }



    public static final void fixSystemAccount() throws SQLException {

        String sql = "select id , mobilePhone from " +SYS+".tb_system_core_account where version >-1 ";
        System.out.println(sql);
        final ResultSet resultSet = getSt().executeQuery(sql);
        while (resultSet.next()){
            final long id = resultSet.getLong("id");
            String mobilePhone = resultSet.getString("mobilePhone");
            mobilePhone = fixSString(mobilePhone);

            String update = "update " +SYS+".tb_system_core_account  set mobilePhone = ? ,version = version -100 where id = ? ";

            final PreparedStatement preparedStatement = prepareStatement(update);
            preparedStatement.setString(1,mobilePhone);
            preparedStatement.setLong(2,id);
            preparedStatement.executeUpdate();
            preparedStatement.close();


        }



    }



    public static final void tbHospitalUserFix() throws SQLException {
        System.out.println("检查表 ");
        String sql = "show tables like 'tb_hospital_user_A%' ";
        final ResultSet resultSet = getSt().executeQuery(sql);
        while (resultSet.next()){
            //System.out.println(resultSet.getString(1));
            _tbHospitalUserFix( HOS +"." + resultSet.getString(1));
        }

    }

    private static final void  _tbHospitalUserFix(String... tableNames) throws SQLException {
        if(tableNames.length > 1){
            for (String tableName : tableNames) {
                _tbHospitalUserFix(tableName);
            }
        }else{
            final String sqlFindAll = "select id, name ,mobilePhone  from " + tableNames[0] + " where version >-1";
            System.out.println(sqlFindAll);
            final ResultSet resultSet = getSt().executeQuery(sqlFindAll);
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String mobilePhone = resultSet.getString("mobilePhone");
                name = fixSString(name);
                mobilePhone = fixSString(mobilePhone);

                String update ="update " + tableNames[0] + " set name = ? ,mobilePhone = ? ,version = version -100  where id = ?";
                final PreparedStatement preparedStatement = prepareStatement(update);
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,mobilePhone);
                preparedStatement.setLong(3,id );
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }

        }



    }



    public static final String fixSString(String source){
        StringBuilder sb = new StringBuilder("");
        for(char c : source.toCharArray()){
            c += 480;
            sb.append(c);
        }

        System.out.println( source +" -> " + sb.toString() + " -> " + DesensitizationUtil.decode(sb.toString()));
        return sb.toString();

    }
}
