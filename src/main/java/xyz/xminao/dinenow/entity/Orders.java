package xyz.xminao.dinenow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // id

    private String number; // 订单号

    private Integer status; // 订单状态 1 待付款 2 待派送 3 已派送 4 已完成 5 已取消

    private Long userId; // 下单用户id

    private Long addressBookId; // 收货地址id

    private LocalDateTime orderTime; // 下单时间

    private LocalDateTime checkoutTime; // 结账时间

    private Integer payMethod; // 结账方式 1 微信 2 支付宝

    private BigDecimal amount; // 实收金额

    private String remark; // 备注

    private String userName; // 用户名

    private String phone; // 手机号

    private String address; // 地址

    private String consignee; // 收货人
}
