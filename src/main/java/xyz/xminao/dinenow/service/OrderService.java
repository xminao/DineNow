package xyz.xminao.dinenow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.xminao.dinenow.entity.Orders;

import java.util.List;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
    List<Orders> listByUserId(Long id);
}
