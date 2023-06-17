package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.xminao.dinenow.entity.OrderDetail;
import xyz.xminao.dinenow.mapper.OrderDetailMapper;
import xyz.xminao.dinenow.service.OrderDetailService;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    @Override
    public List<OrderDetail> listByOrderId(Long id) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, OrderDetail::getOrderId, id);
        return this.list(queryWrapper);
    }
}
