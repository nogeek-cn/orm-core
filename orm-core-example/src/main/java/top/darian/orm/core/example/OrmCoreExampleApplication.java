package top.darian.orm.core.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import top.darian.orm.core.BeanToDataBaseUtils;
import top.darian.orm.core.example.endity.UserDO;
import top.darian.orm.core.spring.beans.context.annotation.TableInfoComponentScan;


@SpringBootApplication
@TableInfoComponentScan("top.darian.orm.core.example.endity")
public class OrmCoreExampleApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(OrmCoreExampleApplication.class, args);
        System.out.println(BeanToDataBaseUtils.getColumnByFunctionName(UserDO::getUserName, UserDO.class));
        // user_name
    }
}