package xyz.xminao.dinenow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.xminao.dinenow.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {
    List<OrderDetail> listByOrderId(Long id);
}
