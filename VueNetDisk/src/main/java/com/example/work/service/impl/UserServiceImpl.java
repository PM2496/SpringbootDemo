package com.example.work.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.work.bean.User;
import com.example.work.mapper.UserMapper;
import com.example.work.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
