package com.hy.plugin;

import com.hy.contrains.PropertyRegistry;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.util.List;
import java.util.Properties;


public class OverIsMergeablePlugin extends PluginAdapter {

    private static final Log log = LogFactory.getLog(OverIsMergeablePlugin.class);

    // 是否合并
    private boolean isMergeable;

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        isMergeable = isTrue(properties.getProperty(PropertyRegistry.IS_MERGE_ABLE));
        System.out.println(isMergeable);
        log.error("isMergeable" + isMergeable);
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        sqlMap.setMergeable(isMergeable);
        /*try {
            Field field = sqlMap.getClass().getDeclaredField("isMergeable");
            //log.error("Field:" + field);
            field.setAccessible(true);
            field.setBoolean(sqlMap, isMergeable);
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        return super.sqlMapGenerated(sqlMap, introspectedTable);
    }

    public static boolean isTrue(String s) {
        return "true".equalsIgnoreCase(s); //$NON-NLS-1$
    }
}
