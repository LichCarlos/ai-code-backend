package com.carlos.aicodebackend.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 对话历史视图对象
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@Data
public class ChatHistoryVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 消息
     */
    private String message;

    /**
     * 消息类型：user/ai
     */
    private String messageType;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;
}

