package com.kfzx.leyou.mapper;

import com.kfzx.leyou.user.pojo.User;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author VicterTian
 * @version V1.0
 * @Date 2019/1/4
 */
@Repository
public interface UserMapper extends Mapper<User> {
}
