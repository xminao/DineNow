package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.xminao.dinenow.common.CustomException;
import xyz.xminao.dinenow.entity.Category;
import xyz.xminao.dinenow.entity.Dish;
import xyz.xminao.dinenow.entity.Setmeal;
import xyz.xminao.dinenow.mapper.CategoryMapper;
import xyz.xminao.dinenow.service.CategoryService;
import xyz.xminao.dinenow.service.DishService;
import xyz.xminao.dinenow.service.SetmealService;

@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    // 自定义删除分类，在删除之前需要判断分类下是否有菜品或者套餐
    @Override
    public void remove(Long id) {
        log.info("分类删除操作 service");
        // 查询当前分类下是否关联了菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 根据分类ID进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        long countDish = dishService.count(dishLambdaQueryWrapper);

        // 如果当前要删除的分类下有菜品，即关联菜品
        if (countDish > 0) {
            log.info("关联了菜品");
            throw new CustomException("当前分类下关联了菜品，无法直接删除");
        }

        // 查询当前分类下是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        long countSetmeal = setmealService.count(setmealLambdaQueryWrapper);

        // 如果当前分类下有套餐，即关联了套餐
        if (countSetmeal > 0) {
            throw new CustomException("当前分类下关联了套餐，无法直接删除");
        }

        super.removeById(id);
    }
}
