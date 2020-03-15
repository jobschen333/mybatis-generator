### 使用插件生成swagger2 自定义model 注解，注释来源于数据库comment

## 1. mybatis generator maven plugin

# 添加依赖
    <plugin>
        <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-maven-plugin</artifactId>
        <version>1.3.5</version>
        <configuration>
            <configurationFile>${basedir}/src/main/resources/generatorConfig.xml</configurationFile>
            <verbose>true</verbose>
            <overwrite>true</overwrite>
        </configuration>
        <dependencies>
            <dependency>
                <groupId>com.hy.util</groupId>
                <artifactId>hy-mybatis-generator</artifactId>
                <version>0.1.0</version>
            </dependency>
        </dependencies>
    </plugin>
        
## generatorConfig.xml 中添加
        
    <property name="javaFileEncoding" value="UTF-8" />
    <plugin type="com.hy.plugin.CustomizedCommentPlugin">
        <!-- 是否禁止显示日期 true：是 ： false:否 -->
        <property name="suppressDate" value="false" />
        <!-- 是否去除自动生成的所有注释 true：是 ： false:否 -->
        <property name="suppressAllComments" value="false" />
        <!-- 是否添加字段注释 true:是 false：否 -->
        <property name="addRemarkComments" value="true" />
        <!-- 自定义属性 作者名称 -->
        <property name="author" value="Jeff" />
    </plugin>