package top.darian.orm.core.example;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.darian.orm.core.BeanToDataBaseUtils;
import top.darian.orm.core.config.annotation.TableInfo;
import top.darian.orm.core.example.endity.UserDO;
import top.darian.orm.core.spring.beans.context.annotation.TableInfoComponentScan;


@SpringBootApplication
@TableInfoComponentScan("top.darian.orm.core.example")
public class OrmCoreExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrmCoreExampleApplication.class, args);
        // user_name
        System.out.println(
                BeanToDataBaseUtils.getColumnByFunctionName(
                        TestModule::getUserName,
                        TestModule.class));
    }
}
@TableInfo
@Data
class TestModule {
    private String userName;
}
