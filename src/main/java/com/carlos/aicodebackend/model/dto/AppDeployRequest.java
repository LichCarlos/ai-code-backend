package com.carlos.aicodebackend.model.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 应用部署请求
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@Data
public class AppDeployRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long appId;

    private static final long serialVersionUID = 1L;
}

