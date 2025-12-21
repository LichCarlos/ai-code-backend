package com.carlos.aicodebackend.model.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 创建应用请求
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@Data
public class AppAddRequest implements Serializable {

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    private static final long serialVersionUID = 1L;
}
