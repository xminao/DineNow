package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.xminao.dinenow.entity.ShoppingCart;
import xyz.xminao.dinenow.mapper.ShoppingCartMapper;
import xyz.xminao.dinenow.service.ShoppingCartService;

import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public List<ShoppingCart> listByUserId(Long id) {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null, ShoppingCart::getUserId, id)
                .orderByAsc(ShoppingCart::getCreateTime);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public void removeByUserId(Long id) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, ShoppingCart::getUserId, id);
        this.remove(queryWrapper);
    }
}
