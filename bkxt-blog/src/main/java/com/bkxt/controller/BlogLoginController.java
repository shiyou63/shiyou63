package com.bkxt.controller;

import com.bkxt.domain.ResponseResult;
import com.bkxt.domain.User;
import com.bkxt.enums.AppHttpCodeEnum;
import com.bkxt.exception.SystemException;
import com.bkxt.service.BlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 35238
 * @date 2023/7/22 0022 21:31
 */
@RestController
public class BlogLoginController {

    @Autowired
    //BlogLoginService是我们在service目录写的接口
    private BlogLoginService blogLoginService;

    @PostMapping("/login")
    //ResponseResult是我们在huanf-framework工程里面写的实体类
    public ResponseResult login(@RequestBody User user){
        //如果用户在进行登录时，没有传入'用户名'
        if(!StringUtils.hasText(user.getUserName())){
            // 提示'必须要传用户名'。AppHttpCodeEnum是我们写的枚举类。SystemException是我们写的统一异常处理的类
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return blogLoginService.login(user);
    }

    @PostMapping("/logout")
    public ResponseResult logout(){
        return blogLoginService.logout();
    }
}