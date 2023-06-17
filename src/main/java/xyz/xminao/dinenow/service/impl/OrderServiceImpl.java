package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.xminao.dinenow.common.BaseContext;
import xyz.xminao.dinenow.common.CustomException;
import xyz.xminao.dinenow.entity.*;
import xyz.xminao.dinenow.mapper.OrderMapper;
import xyz.xminao.dinenow.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        // 用户id
        Long userId = BaseContext.getCurrentId();

        // 确认购物车是否为空，为空不能下单
        List<ShoppingCart> shoppingCartList = shoppingCartService.listByUserId(userId);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，下单失败");
        }

        // 获取用户数据
        User user = userService.getById(userId);

        // 确认地址是否存在
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw  new CustomException("用户地址信息有误，下单失败");
        }

        // 提交订单
        long orderId = IdWorker.getId(); // 根据雪花算法生成一个独立的订单id
        // 线程安全的原子操作类
        AtomicInteger amount = new AtomicInteger(0);
        // 添加订单细节表
        List<OrderDetail> orderDetails = shoppingCartList.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId); // 订单号
            orderDetail.setNumber(item.getNumber()); // 数量
            orderDetail.setDishFlavor(item.getDishFlavor()); // 口味
            orderDetail.setName(item.getName()); // 菜品/套餐名称
            orderDetail.setDishId(item.getDishId()); // 菜品id
            orderDetail.setSetmealId(item.getSetmealId()); // 套餐id
            orderDetail.setAmount(item.getAmount()); // 金额
            orderDetail.setImage(item.getImage()); // 图片
            amount.addAndGet(item.getAmount().multiply(new BigDecimal((item.getNumber()))).intValue()); // 总金额
            return orderDetail;
        }).toList();
        // 保存订单细节到订单细节表
        orderDetailService.saveBatch(orderDetails);

        // 向订单表记录设置属性
        orders.setId(orderId); // id
        orders.setNumber(String.valueOf(orderId)); // 订单号 string 类型
        orders.setStatus(2); // 订单状态 2：待派送
        orders.setUserId(userId); // 用户id
        orders.setOrderTime(LocalDateTime.now()); // 下单时间
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setPhone(addressBook.getPhone()); // 用户手机号
        orders.setConsignee(addressBook.getConsignee()); // 收货人姓名
        orders.setUserName(user.getName()); // 用户名
        orders.setAddress(
                (addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                +(addressBook.getCityName() == null ? "" : addressBook.getCityName())
                +(addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                +(addressBook.getDetail() == null ? "" : addressBook.getDetail())
        );
        // 支付相关
        orders.setCheckoutTime(LocalDateTime.now()); // 结账时间
        orders.setPayMethod(1); // 支付方式
        // 保存订单到订单表
        this.save(orders);

        // 清空购物车
        shoppingCartService.removeByUserId(userId);
    }

    @Override
    public List<Orders> listByUserId(Long id) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, Orders::getUserId, id)
                .orderByDesc(Orders::getOrderTime);
        return this.list(queryWrapper);
    }
}
