package com.hy.plugin;

import com.hy.contrains.PropertyRegistry;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

public class CustomizedCommentPlugin extends PluginAdapter {

    //添加作者配置
    public String author;

    /** The suppress date.
     * 禁止日期生成
     */
    private boolean suppressDate;

    /** The suppress all comments.
     * 禁止生成所有的注释
     */
    private boolean suppressAllComments;

    /**
     * 添加表格字段的注释说明
     * 当suppressAllComments为true，这个参数的值将会被忽略
     */
    private boolean addRemarkComments;

    /**
     * 日期格式
     */
    private SimpleDateFormat dateFormat;

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String authorString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_AUTHOR);

        suppressDate = isTrue(properties
                .getProperty(org.mybatis.generator.config.PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

        suppressAllComments = isTrue(properties
                .getProperty(org.mybatis.generator.config.PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));

        addRemarkComments = isTrue(properties
                .getProperty(org.mybatis.generator.config.PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));

        String dateFormatString = properties.getProperty(org.mybatis.generator.config.PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
        if (StringUtility.stringHasValue(dateFormatString)) {
            dateFormat = new SimpleDateFormat(dateFormatString);
        }

        //添加作者配置
        if (StringUtility.stringHasValue(authorString)) {
            author = authorString;
        }
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    protected String getDateString() {
        if (suppressDate) {
            return null;
        } else if (dateFormat != null) {
            return dateFormat.format(new Date());
        } else {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
    }


    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
        addModelClassComment(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
        addFieldComment(field,introspectedTable,introspectedColumn);
        Context context = this.getContext();
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        super.modelRecordWithBLOBsClassGenerated(topLevelClass, introspectedTable);
        addModelClassComment(topLevelClass, introspectedTable);
        return true;
    }



    /**
     * 添加表注解
     * @param topLevelClass
     * @param introspectedTable
     */
    public void addModelClassComment(TopLevelClass topLevelClass,
                                     IntrospectedTable introspectedTable) {
        if (suppressAllComments  || !addRemarkComments) {
            return;
        }
        //通过这种方式不能直接获取表备注
        //String remarks = introspectedTable.getRemarks();
        String remarks = ProcessHandler.getTableComment(getContext(),introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        //introspectedTable.getr
        //重新写方法从数据库中获取表备注的信息
        //String remarks = introspectedTable.getFullyQualifiedTable().getRemark();
        //获取实体类名称
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        StringBuilder sb = new StringBuilder();

        //添加导入类的信息
        topLevelClass.addJavaDocLine("import org.springframework.format.annotation.DateTimeFormat;");
        topLevelClass.addJavaDocLine("import com.fasterxml.jackson.annotation.JsonFormat;");
        topLevelClass.addJavaDocLine("import io.swagger.annotations.ApiModel;");
        topLevelClass.addJavaDocLine("import io.swagger.annotations.ApiModelProperty;");

        //添加类注释
        topLevelClass.addJavaDocLine("/**");
        sb.append(" * "+ remarks);
        sb.append("\n");
        sb.append(" * 实体类对应的数据表为：  ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        topLevelClass.addJavaDocLine(sb.toString());
        topLevelClass.addJavaDocLine(" * @author " + author);

        //添加时间
        topLevelClass.addJavaDocLine(" * @date " + getDateString());
        topLevelClass.addJavaDocLine(" */");
        topLevelClass.addJavaDocLine("@ApiModel(value =\"" + entityName + "\")");
    }

    /**
     * 添加字段注解
     * @param field
     * @param introspectedTable
     * @param introspectedColumn
     */
    public void addFieldComment(Field field,
                                IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }
        //字段备注信息
        String remarks = introspectedColumn.getRemarks();
        int jdbcType = introspectedColumn.getJdbcType();

        //获取字段类型
        if(jdbcType == Types.TINYINT){
            field.addJavaDocLine("@ApiModelProperty(value = \"" + remarks + "\", dataType = \"java.lang.Integer\")");
        }else{
            field.addJavaDocLine("@ApiModelProperty(value = \"" + remarks + "\")");
        }

        //当字段数据类型为date时添加日期注释
        StringBuffer sb = new StringBuffer();
        if(jdbcType == Types.TIMESTAMP || jdbcType == Types.TIME) {
            sb.append("@DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
        } else if(jdbcType == Types.DATE) {
            sb.append("@DateTimeFormat(pattern = \"yyyy-MM-dd\")");
        }

        if(sb.length() > 0) {
            field.addJavaDocLine(sb.toString());
        }

        StringBuffer sb2 = new StringBuffer();
        if(jdbcType == Types.DATE) {
            sb2.append("@JsonFormat(pattern = \"yyyy-MM-dd\")");
        } else if(jdbcType == Types.TIMESTAMP || jdbcType == Types.TIME) {
            sb2.append("@JsonFormat(pattern= \"yyyy-MM-dd HH:mm:ss\")");
        }

        if(sb.length() > 0) {
            field.addJavaDocLine(sb2.toString());
        }
    }

}
