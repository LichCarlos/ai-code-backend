package com.carlos.aicodebackend.mapper;

import com.mybatisflex.core.BaseMapper;
import com.carlos.aicodebackend.model.entity.App;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用 映射层。
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@Mapper
public interface AppMapper extends BaseMapper<App> {

}
