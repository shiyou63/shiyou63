package com.bkxt.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bkxt.domain.UserRole;
import com.bkxt.mapper.UserRoleMapper;
import com.bkxt.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}