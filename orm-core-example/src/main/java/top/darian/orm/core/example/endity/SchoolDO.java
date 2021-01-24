package top.darian.orm.core.example.endity;

import lombok.Data;
import top.darian.orm.core.config.annotation.TableInfo;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/24  上午2:04
 */
@TableInfo
@Data
public class SchoolDO {

    private String id;

    private String schoolName;
}
