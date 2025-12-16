package com.carlos.aicodebackend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.carlos.aicodebackend.mapper.UserMapper;
import com.carlos.aicodebackend.model.entity.User;
import com.carlos.aicodebackend.service.UserService;

import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author <a href="https://github.com/LichCarlos">LichCarlos</a>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
