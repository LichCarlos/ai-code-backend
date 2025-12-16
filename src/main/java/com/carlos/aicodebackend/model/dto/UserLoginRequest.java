package com.carlos.aicodebackend.model.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserLoginRequest implements Serializable {

  private static final long serialVersionUID = 3191241716373120793L;

  /**
   * 账号
   */
  private String userAccount;

  /**
   * 密码
   */
  private String userPassword;
}