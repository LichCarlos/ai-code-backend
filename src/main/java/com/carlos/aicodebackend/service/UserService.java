package com.carlos.aicodebackend.service;

import java.util.List;

import com.carlos.aicodebackend.model.dto.UserQueryRequest;
import com.carlos.aicodebackend.model.entity.User;
import com.carlos.aicodebackend.model.vo.LoginUserVO;
import com.carlos.aicodebackend.model.vo.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;

import cn.hutool.db.sql.Query;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户 服务层。
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
public interface UserService extends IService<User> {
  /**
   * 用户注册
   *
   * @param userAccount   用户账户
   * @param userPassword  用户密码
   * @param checkPassword 校验密码
   * @return 新用户 id
   */
  long userRegister(String userAccount, String userPassword, String checkPassword);

  /**
   * 加密
   *
   * @param userPassword 用户密码
   */
  String getEncryptPassword(String userPassword);

  /**
   * 获取脱敏的已登录用户信息
   *
   * @return
   */
  LoginUserVO getLoginUserVO(User user);

  /**
   * 用户登录
   *
   * @param userAccount  用户账户
   * @param userPassword 用户密码
   * @param request
   * @return 脱敏后的用户信息
   */
  LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

  /**
   * 获取当前登录用户
   *
   * @param request
   * @return
   */
  User getLoginUser(HttpServletRequest request);

  /**
   * 用户注销
   *
   * @param request
   * @return
   */
  boolean userLogout(HttpServletRequest request);

  /**
   * 用户脱敏信息
   *
   * @param request
   * @return
   */
  UserVO getUserVO(User user);

  /**
   * 用户脱敏信息
   *
   * @param request
   * @return
   */
  List<UserVO> getUserVOList(List<User> userList);

  /**
   * 获取查询条件
   *
   * @param user
   * @return
   */
  QueryWrapper getQueryWrapper(UserQueryRequest UserQueryRequest);
}
