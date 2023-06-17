package xyz.xminao.dinenow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.dto.DishDto;
import xyz.xminao.dinenow.dto.SetmealDto;
import xyz.xminao.dinenow.entity.*;
import xyz.xminao.dinenow.service.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    // 套餐
    @Autowired
    private SetmealService setmealService;

    // 套餐和菜品对应关系
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    //  新增套餐
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        setmealService.save(setmealDto);
        return Result.success("新增套餐成功");
    }

    // 套餐分页查询
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);

        // 条件查询器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, lambdaQueryWrapper);

        // 对象拷贝，只拷贝数量不拷贝内容
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        // 获取原始page的records数据
        List<Setmeal> records = pageInfo.getRecords();

        // 返回的dto对象
        List<SetmealDto> list = records.stream().map(item -> {
            SetmealDto dto = new SetmealDto();
            // 对象拷贝
            BeanUtils.copyProperties(item, dto); // 拷贝套餐的基本信息
            // 获取套餐的分类ID
            Long categoryId = item.getCategoryId();
            // 根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            // 设置dto对象的分类名称
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
            return dto;
        }).toList();
        dtoPage.setRecords(list);
        return Result.success(dtoPage);
    }

    // 删除套餐
    // 可以一次删除一个或多个
    // 删除套餐正常出售1中的需要停售0才能删除
    @DeleteMapping
    public Result<String> deleteByIds(@RequestParam List<Long> ids) {
        log.info("要删除的套餐为：{}", ids);
        setmealService.removeWithDish(ids);
        return null;
    }

    /**
     * 根据条件查询套餐 ，用于客户端展示套餐内容
     * 请求参数 categoryId ， status
     */
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        return Result.success(setmealService.list(lambdaQueryWrapper));
    }

    /**
     * 点击图片查看套餐内菜品详情
     */
    @GetMapping("/dish/{id}")
    public Result<List<DishDto>> showSetmealDish(@PathVariable Long id) {
        log.info("查看套餐内菜品详情:{}", id);

        // 根据id查询套餐菜品关系 setmealDish
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SetmealDish::getSetmealId, id);
        // 查询所有套餐菜品关系数据
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        log.info("套餐菜品关系数据：{}", setmealDishes);

        // 设置返回的dto对象
        List<DishDto> dishDtos = setmealDishes.stream().map(item -> {
            DishDto dto = new DishDto();
            Dish dish = dishService.getById(item.getDishId());
            List<DishFlavor> dishFlavors = dishFlavorService.listByDishId(item.getDishId());
            dto.setFlavors(dishFlavors);
            // 获取dish对象的分类id
            Long categoryId = dish.getCategoryId();
            // 获取分类对象
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dto.setCategoryName(categoryName);
            dto.setCopies(item.getCopies());
            BeanUtils.copyProperties(dish, dto);
            return dto;
        }).toList();
        log.info("dto数据：{}", dishDtos);

        return Result.success(dishDtos);
    }

    /**
     * 获取套餐回显数据
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> getById(@PathVariable Long id) {
        log.info("获取套餐回显ID：{}", id);
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        // 套餐的基本信息拷贝进去
        BeanUtils.copyProperties(setmeal, setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        setmealDto.setCategoryName(categoryService.getById(setmeal.getCategoryId()).getName());
        return Result.success(setmealDto);
    }

    /**
     * 更改套餐的状态，可以批量处理
     *  0：停售 1：启售
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("更改套餐状态：{}", ids);
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ids != null, Setmeal::getId, ids)
                .set(Setmeal::getStatus, status);
        setmealService.update(updateWrapper);
        return Result.success("套餐状态更改成功");
    }

}
