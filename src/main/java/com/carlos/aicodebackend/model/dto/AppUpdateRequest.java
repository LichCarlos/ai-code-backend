package com.carlos.aicodebackend.model.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户更新应用请求
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@Data
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    private static final long serialVersionUID = 1L;
}

