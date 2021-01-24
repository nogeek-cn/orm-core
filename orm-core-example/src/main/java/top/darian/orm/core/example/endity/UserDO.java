package top.darian.orm.core.example.endity;

import lombok.Data;
import top.darian.orm.core.config.annotation.TableInfo;

/***
 *
 *
 * @author <a href="mailto:1934849492@qq.com">Darian</a>
 * @date 2021/1/23  下午9:36
 */
@TableInfo
@Data
public class UserDO {

    private String name;

    private String userName;
}
