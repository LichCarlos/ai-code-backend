package com.carlos.aicodebackend.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.carlos.aicodebackend.model.dto.ChatHistoryQueryRequest;
import com.carlos.aicodebackend.model.entity.ChatHistory;
import com.carlos.aicodebackend.model.entity.User;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话消息（统一接口）
     *
     * @param appId       应用ID
     * @param message     消息内容
     * @param messageType 消息类型（user/ai）
     * @param userId      用户ID
     * @return 是否保存成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 获取查询条件
     *
     * @param chatHistoryQueryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    /**
     * 分页查询应用的对话历史（游标查询）
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条记录的创建时间
     * @param loginUser      登录用户
     * @return 对话历史分页结果
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);

    /**
     * 根据应用ID删除所有对话历史
     *
     * @param appId 应用ID
     * @return 删除结果
     */
    boolean deleteByAppId(Long appId);

    /**
     * 从数据库加载历史对话到记忆中
     *
     * @param appId     应用ID
     * @param chatMemory 对话记忆
     * @param maxCount  最大加载数量
     * @return 实际加载的消息数量
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);
}
