package com.hy.plugin;

import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.JDBCConnectionFactory;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.sql.*;

public class ProcessHandler {

    private static final Log log = LogFactory.getLog(ProcessHandler.class);

    public static String getTableComment(Context context,String tableName){
        String databaseProductName = "";
        Connection conn = null;
        String remark = "";
        //获取数据库的版本信息
        try {
            JDBCConnectionFactory connectionFactory = new JDBCConnectionFactory(context.getJdbcConnectionConfiguration());
            conn = connectionFactory.getConnection();
            DatabaseMetaData md = conn.getMetaData();
            databaseProductName = md.getDatabaseProductName().toUpperCase();
        } catch (SQLException se) {
            log.error("获取数据库版本失败:" + se.getMessage());
        }

        try {
            //自己写方法从数据库中获取表备注的信息
            if ("MYSQL".equals(databaseProductName)) {
                //设置数据库表的备注信息
                //start mysql
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(new StringBuilder().append("SHOW TABLE STATUS LIKE '").append(tableName).append("'").toString());
                while (rs.next())
                    remark = rs.getString("COMMENT");
                //table.setRemark(rs.getString("COMMENT"));

                closeResultSet(rs);
                stmt.close();
                //end
            } else if ("ORACLE".equals(databaseProductName)) {
                //start oracle,获取oracle数据库的表备注
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(new StringBuilder().append("select * from user_tab_comments where Table_Name Like '").append(tableName).append("'").toString());
                while (rs.next())
                    remark = rs.getString("COMMENT");
                    //table.setRemark(rs.getString("COMMENTS"));
                closeResultSet(rs);
                stmt.close();
                //end
            } else if ("SQLSERVER".equals(databaseProductName)) {
                //start oracle,获取SqlServer数据库的表备注
                Statement stmt = conn.createStatement();
                StringBuilder sb = new StringBuilder();
                sb.append("select ROW_NUMBER() OVER (ORDER BY a.object_id) AS No, ");
                sb.append(" a.name AS TABLE_NAME, ");
                sb.append(" isnull(g.[value],'-') AS COMMENT ");
                sb.append(" from sys.tables a left join sys.extended_properties g ");
                sb.append(" on (a.object_id = g.major_id AND g.minor_id = 0) ");
                sb.append(" where a.name = ' ");
                sb.append(tableName);
                sb.append("'");
                ResultSet rs = stmt.executeQuery(sb.toString());

                while (rs.next())
                    remark = rs.getString("COMMENT");
                    //table.setRemark(rs.getString("COMMENT"));
                closeResultSet(rs);
                stmt.close();
                //end
            }

        }catch (SQLException e){
            log.error("获取表注释失败" + e.getMessage());
        }


        return remark;
    }

    /**
     * Close result set.
     *
     * @param rs
     *            the rs
     */
    private static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
