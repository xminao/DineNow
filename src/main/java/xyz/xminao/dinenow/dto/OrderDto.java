package xyz.xminao.dinenow.dto;

import lombok.Data;
import xyz.xminao.dinenow.entity.OrderDetail;
import xyz.xminao.dinenow.entity.Orders;

import java.util.List;

@Data
public class OrderDto extends Orders {
    private String userName; // 用户名

    private String phone; // 电话号

    private String address; // 收货地址

    private String consignee; // 收货人

    private List<OrderDetail> orderDetails; // 订单详情
}
