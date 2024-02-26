package com.njn.shardingjdbcdemo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.njn.shardingjdbcdemo.entity.*;
import com.njn.shardingjdbcdemo.mapper.DictMapper;
import com.njn.shardingjdbcdemo.mapper.OrderItemMapper;
import com.njn.shardingjdbcdemo.mapper.OrderMapper;
import com.njn.shardingjdbcdemo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class ReadWriteTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DictMapper dictMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;


    @Test
    public void testInsert() {
        User user = new User();
        user.setUname("曹操");
        userMapper.insert(user);
    }


    @Test
    public void testTransaction() {
        User user = new User();
        user.setUname("刘备");
        userMapper.insert(user);
        List<User> users = userMapper.selectList(null);
    }


    /**
     * 为了保证主从库间的事务一致性，避免跨服务的分布式事务，ShardingSphere-JDBC的`主从模型中，事务中的数据读写均用主库`。
     *
     *  1. 不添加@Transactional：insert对主库操作，select对从库操作
     *  2. 添加@Transactional：则insert和select均对主库操作
     *
     */
    @Transactional
    @Test
    public void testTransaction2() {
        User user = new User();
        user.setUname("刘备");
        userMapper.insert(user);
        List<User> users = userMapper.selectList(null);
    }

    @Test
    public void testSelectList() {
        List<User> users1 = userMapper.selectList(null);
        List<User> users2 = userMapper.selectList(null);
        List<User> users3 = userMapper.selectList(null);
        List<User> users4 = userMapper.selectList(null);
    }


    /**
     * 水平分表插入测试
     */
    @Test
    public void testInsert1() {

        Order order = new Order();
//        order.setId(1L);
        order.setOrderNo("123456");
        order.setUserId(1L);
        order.setAmount(new BigDecimal("100"));

        orderMapper.insert(order);

    }


    /**
     * 水平分片：分库插入数据测试
     */
    @Test
    public void testInsertOrderDatabaseStrategy(){

        for (long i = 4; i < 8; i++) {
            Order order = new Order();
            order.setOrderNo("ATGUIGU001");
            order.setUserId(i + 1);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }

    }


    /**
     * 水平分片：分表插入数据测试
     */
    @Test
    public void testInsertOrderTableStrategy(){

        for (long i = 1; i < 5; i++) {

            Order order = new Order();
            order.setOrderNo("ATGUIGU" + i);
            order.setUserId(1L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }

        for (long i = 5; i < 9; i++) {

            Order order = new Order();
            order.setOrderNo("ATGUIGU" + i);
            order.setUserId(2L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }
    }


    /**
     * 水平分片：分表插入数据测试
     */
    @Test
    public void testInsertOrderTableStrategy2(){

        for (long i = 100; i < 108; i++) {

            Order order = new Order();
            order.setOrderNo("AU00" + i);
            order.setUserId(i);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }

    }


    /**
     * 水平分片：分表插入数据测试
     */
    @Test
    public void testInsertOrderTableStrategy3(){

        for (long i = 1; i < 5; i++) {

            Order order = new Order();
            order.setOrderNo("AT" + i);
            order.setUserId(1L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }

        for (long i = 5; i < 9; i++) {

            Order order = new Order();
            order.setOrderNo("AT" + i);
            order.setUserId(2L);
            order.setAmount(new BigDecimal(100));
            orderMapper.insert(order);
        }
    }

    /**
     * 水平分片：查询所有记录
     * 查询了两个数据源，每个数据源中使用UNION ALL连接两个表
     */
    @Test
    public void testShardingSelectAll(){

        List<Order> orders = orderMapper.selectList(null);
        orders.forEach(System.out::println);
    }

    /**
     * 水平分片：根据user_id查询记录
     * 查询了一个数据源，每个数据源中使用UNION ALL连接两个表
     */
    @Test
    public void testShardingSelectByUserId(){

        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("user_id", 1L);
        List<Order> orders = orderMapper.selectList(orderQueryWrapper);
        orders.forEach(System.out::println);
    }


    /**
     * 测试关联表插入
     */
    @Test
    public void testInsertOrderAndOrderItem(){

        for (long i = 1; i < 3; i++) {

            Order order = new Order();
            order.setOrderNo("AT" + i);
            order.setUserId(1L);
            orderMapper.insert(order);

            for (long j = 1; j < 3; j++) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderNo("AT" + i);
                orderItem.setUserId(1L);
                orderItem.setPrice(new BigDecimal(10));
                orderItem.setCount(2);
                orderItemMapper.insert(orderItem);
            }
        }

        for (long i = 5; i < 7; i++) {

            Order order = new Order();
            order.setOrderNo("AT" + i);
            order.setUserId(2L);
            orderMapper.insert(order);

            for (long j = 1; j < 3; j++) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderNo("AT" + i);
                orderItem.setUserId(2L);
                orderItem.setPrice(new BigDecimal(2));
                orderItem.setCount(3);
                orderItemMapper.insert(orderItem);
            }
        }

    }


    /**
     * 测试关联表查询
     */
    @Test
    public void testGetOrderAmount(){

        List<OrderVo> orderAmountList = orderMapper.getOrderAmount();
        orderAmountList.forEach(System.out::println);
    }

    /**
     * 广播表：每个服务器中的t_dict同时添加了新数据
     */
    @Test
    public void testBroadcast(){

        Dict dict = new Dict();
        dict.setDictType("type1");
        dictMapper.insert(dict);
    }

    /**
     * 查询操作，只从一个节点获取数据
     * 随机负载均衡规则
     */
    @Test
    public void testSelectBroadcast(){

        List<Dict> dicts = dictMapper.selectList(null);
        dicts.forEach(System.out::println);
    }

}
