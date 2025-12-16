package com.carlos.aicodebackend.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.carlos.aicodebackend.model.entity.User;
import com.mybatisflex.core.BaseMapper;

/**
 * 用户 映射层。
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
