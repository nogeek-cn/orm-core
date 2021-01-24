package top.darian.orm.core.example.endity;

import lombok.Data;
import top.darian.orm.core.config.annotation.TableInfo;

import javax.persistence.Column;
import javax.persistence.Table;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/24  下午1:03
 */
@TableInfo
@Data
@Table(name = "user_class")
public class ClassDO {
    @Column(name = "aaa_class_name")
    private String className;
}
