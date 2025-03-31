package com.bkxt.controller;

import com.bkxt.domain.ResponseResult;
import com.bkxt.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 35238
 * @date 2023/7/22 0022 14:34
 */
@RestController
@RequestMapping("link")
public class LinkController {

    @Autowired
    //LinkService是我们在huanf-framework工程写的接口
    private LinkService linkService;


    @GetMapping("/getAllLink")
    //ResponseResult是我们在huanf-framework工程写的实体类
    public ResponseResult getAllLink(){

        return linkService.getAllLink();
    }

}