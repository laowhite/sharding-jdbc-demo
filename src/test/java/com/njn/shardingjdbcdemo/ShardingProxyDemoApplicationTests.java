package com.njn.shardingjdbcdemo;

import com.njn.shardingjdbcdemo.entity.Order;
import com.njn.shardingjdbcdemo.entity.OrderVo;
import com.njn.shardingjdbcdemo.entity.User;
import com.njn.shardingjdbcdemo.mapper.OrderMapper;
import com.njn.shardingjdbcdemo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShardingProxyDemoApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 读数据测试
     */
    @Test
    public void testSelectAll(){
        List<User> users = userMapper.selectList(null);
        users.forEach(System.out::println);
    }

    /**
     * 读数据测试
     */
    @Test
    public void testOrderSelectAll(){
        List<Order> orders = orderMapper.selectList(null);
        orders.forEach(System.out::println);
    }

    /**
     * 水平分表
     */
    @Test
    public void testOrderShuiPingSelectAll(){
        List<Order> orders = orderMapper.selectList(null);
        orders.forEach(System.out::println);
        System.out.println("=================");
        List<OrderVo> orderAmount = orderMapper.getOrderAmount();
        orderAmount.forEach(System.out::println);
    }

}