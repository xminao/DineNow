package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.xminao.dinenow.entity.DishFlavor;
import xyz.xminao.dinenow.mapper.DishFlavorMapper;
import xyz.xminao.dinenow.service.DishFlavorService;

import java.util.List;

@Service
public class DishFlavorImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
    @Override
    public List<DishFlavor> listByDishId(Long id) {
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        return this.list(lambdaQueryWrapper);
    }

    @Override
    public void removeByDishId(Long id) {
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        this.remove(lambdaQueryWrapper);
    }
}
