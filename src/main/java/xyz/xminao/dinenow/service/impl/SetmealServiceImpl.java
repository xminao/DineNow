package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.xminao.dinenow.common.CustomException;
import xyz.xminao.dinenow.dto.SetmealDto;
import xyz.xminao.dinenow.entity.Setmeal;
import xyz.xminao.dinenow.entity.SetmealDish;
import xyz.xminao.dinenow.mapper.SetmealMapper;
import xyz.xminao.dinenow.service.SetmealDishService;
import xyz.xminao.dinenow.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishService;

    // 删除套餐，同时需要删除套餐和菜品的关联数据
    @Override
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态 1：可用 0：不可用
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, 1);
        long count = this.count(lambdaQueryWrapper);
        if (count > 0) { // 有正在出售的 不可以删除
            throw new CustomException("有正在出售的套餐，无法删除");
        }

        // 先删除套餐中的数据 即 setmeal表
        this.removeByIds(ids);

        // 删除套餐和菜品联系
        setmealDishService.removeBySetmealIds(ids);
    }

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐的基本信息到 setmeal表
        this.save(setmealDto);

        // 保存和套餐关联菜品的信息到 setmeal_dish表中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        // 保存套餐和菜品的关联关系 到表 setmeal_dish中
        setmealDishService.saveBatch(setmealDishes);
    }
}
