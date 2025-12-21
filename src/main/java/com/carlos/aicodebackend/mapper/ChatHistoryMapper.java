package com.carlos.aicodebackend.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.mybatisflex.core.BaseMapper;
import com.carlos.aicodebackend.model.entity.ChatHistory;

/**
 * 对话历史 映射层。
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {

}
