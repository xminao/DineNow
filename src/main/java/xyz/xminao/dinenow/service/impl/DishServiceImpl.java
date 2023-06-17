package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.xminao.dinenow.dto.DishDto;
import xyz.xminao.dinenow.entity.Dish;
import xyz.xminao.dinenow.entity.DishFlavor;
import xyz.xminao.dinenow.mapper.DishMapper;
import xyz.xminao.dinenow.service.DishFlavorService;
import xyz.xminao.dinenow.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询当前菜品的口味信息
        dishDto.setFlavors(dishFlavorService.listByDishId(id));

        return dishDto;
    }

    // 更新菜品以及口味信息
    @Override
    @Transactional // 事务
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应口味数据
        dishFlavorService.removeByDishId(dishDto.getId());

        // 添加当前提交过来的口味信息 dish_flavor insert
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(item -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId = dishDto.getId(); // 菜品id

        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(item -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavors
        dishFlavorService.saveBatch(flavors);
    }
}
