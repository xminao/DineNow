package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.xminao.dinenow.entity.SetmealDish;
import xyz.xminao.dinenow.mapper.SetmealDishMapper;
import xyz.xminao.dinenow.service.SetmealDishService;

import java.util.List;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
    // 根据套餐id删除套餐和菜品关联
    @Override
    public void removeBySetmealIds(List<Long> ids) {
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
        this.remove(lambdaQueryWrapper);
    }
}
