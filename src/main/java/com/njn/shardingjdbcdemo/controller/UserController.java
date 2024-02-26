package com.njn.shardingjdbcdemo.controller;

import com.njn.shardingjdbcdemo.entity.User;
import com.njn.shardingjdbcdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public void list() {
        List<User> users = userMapper.selectList(null);
    }

}
