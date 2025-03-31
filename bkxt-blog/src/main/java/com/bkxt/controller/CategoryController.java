package com.bkxt.controller;

import com.bkxt.domain.ResponseResult;
import com.bkxt.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 35238
 * @date 2023/7/20 0020 14:15
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    //CategoryService是我们在huanf-framework工程里面写的接口
    private CategoryService categoryService;

    @GetMapping("/getCategoryList")
    //ResponseResult是我们在huanf-framework工程里面写的实体类
    public ResponseResult getCategoryList(){
        return categoryService.getCategoryList();
    }

}