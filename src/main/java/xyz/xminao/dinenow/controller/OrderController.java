package xyz.xminao.dinenow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.xminao.dinenow.common.BaseContext;
import xyz.xminao.dinenow.common.CustomException;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.dto.OrderDto;
import xyz.xminao.dinenow.entity.OrderDetail;
import xyz.xminao.dinenow.entity.Orders;
import xyz.xminao.dinenow.entity.ShoppingCart;
import xyz.xminao.dinenow.service.OrderDetailService;
import xyz.xminao.dinenow.service.OrderService;
import xyz.xminao.dinenow.service.ShoppingCartService;
import xyz.xminao.dinenow.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders) {
        log.info("用户下单,数据：{}", orders);
        orderService.submit(orders);
        return Result.success("下单成功");
    }


    /**
     * 用户页
     */
    @GetMapping("/userPage")
    public Result<Page> page(int page, int pageSize) {
        Long userId = BaseContext.getCurrentId(); // 当前用户id

        Page<Orders> pageInfo = new Page<>(page, pageSize);
        // 用于表现层显示数据
        Page<OrderDto> dtoPage = new Page<>(page, pageSize);
        // 查询当前用户的订单数据
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, Orders::getUserId, userId)
                .orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, queryWrapper); // 获取订单分页数据

        // 拷贝查询的数量
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        // 生成dto分页对象
        List<OrderDto> list = pageInfo.getRecords().stream().map(item -> {
            OrderDto dto = new OrderDto();
            BeanUtils.copyProperties(item, dto); // 把订单基本信息拷贝进dto
            // 设置订单细节属性
            LambdaQueryWrapper<OrderDetail> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(queryWrapper1);
            dto.setOrderDetails(orderDetails);
            return dto;
        }).toList();

        // 给dtoPage赋值
        dtoPage.setRecords(list);

        log.info("list:{}", list);
        return Result.success(dtoPage);
    }

    /**
     * 再来一单
     * * 分析一下流程：
     *      根据传来的ordersID查询orderDetail数据
     *      清空当前用户的购物车
     *      把查出来的orderDetails数据拷贝进购物车（userid和createtime属性需要另外set）
     *      更新购物车数据
     *
     *      前端：跳转到结算前的界面
     */
    @PostMapping("/again")
    public Result<String> again(@RequestBody Map<String, String> map) {
        // 获取传来的要再来一单的订单号
        Long orderId = Long.valueOf(map.get("id"));
        // 获取当前用户
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new CustomException("用户数据错误，操作失败");
        }

        // 根据orderId查询orderDetails
        List<OrderDetail> orderDetails = orderDetailService.listByOrderId(orderId);

        // 清空当前用户的购物车
        shoppingCartService.removeByUserId(userId);
        // 把orderdetials数据拷贝进一个购物车list
        List<ShoppingCart> carts = orderDetails.stream().map(item -> {
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(item, cart);
            cart.setUserId(userId);
            cart.setCreateTime(LocalDateTime.now());
            return cart;
        }).toList();

        // 保存进购物车表中
        shoppingCartService.saveBatch(carts);
        return Result.success("再来一单...");
    }
}
