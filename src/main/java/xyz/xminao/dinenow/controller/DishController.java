package xyz.xminao.dinenow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import xyz.xminao.dinenow.common.CustomException;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.dto.DishDto;
import xyz.xminao.dinenow.entity.Category;
import xyz.xminao.dinenow.entity.Dish;
import xyz.xminao.dinenow.service.CategoryService;
import xyz.xminao.dinenow.service.DishFlavorService;
import xyz.xminao.dinenow.service.DishService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService; // 菜品

    @Autowired
    private DishFlavorService dishFlavorService; // 口味

    @Autowired
    private CategoryService categoryService; // 分类

    @Autowired
    private RedisTemplate redisTemplate;

    // 新增菜品
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        log.info("接收到的数据：{}", dishDto);
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    // 菜品分页查询
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 返回给表现层的结果
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        // 查询菜品分页
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, lambdaQueryWrapper);

        // 进行拷贝，之拷贝查询到的条目数量，不拷贝查询到的记录，忽略records
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        // 获取原records数据
        List<Dish> records = pageInfo.getRecords();
        // 遍历records
        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            // 数据赋值给dishdto
            BeanUtils.copyProperties(item, dishDto);
            // 获取dish对象的分类id
            Long categoryId = item.getCategoryId();
            // 获取分类对象
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            // 返回dishDto对象
            return dishDto;
        }).toList(); //封装为集合 作为最终结果

        dishDtoPage.setRecords(list);

        return Result.success(dishDtoPage);
    }

    // 根据id查询菜品信息和口味信息
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return Result.success(dishDto);
    }

    // 根据id修改菜品信息
    // 需要删除redis缓存
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        log.info("修改菜品：{}", dishDto);

        dishService.updateWithFlavor(dishDto);
        // 清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success("菜品信息修改成功");
    }

    // 根据条件查询对应的菜品数据
//    @GetMapping("/list")
//    public Result<List<Dish>> list(Long categoryId) {
//        log.info("查询菜品列表：{}", categoryId);
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        dishLambdaQueryWrapper.eq(Dish::getCategoryId, categoryId)
//                .eq(Dish::getStatus, 1) // 1:为正常出售
//                .orderByAsc(Dish::getSort)
//                .orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
//        return Result.success(list);
//    }

    // 根据分类id 条件查询对应的用于表现层的dishdto对象数据
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {
        // 使用redis缓存菜品列表
        List<DishDto> dishDtos = null;
        // 动态构造key，使用dish_categoryid_status
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        // 从redis中获取缓存的数据
        dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtos != null) {
            return Result.success(dishDtos);
        }

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
                .eq(Dish::getStatus, 1) // 正常销售
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        // 获取符合条件的菜品列表
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        // 返回的用来显示的dishdto对象
        dishDtos = list.stream().map(item -> {
            DishDto dto = new DishDto();
            // 拷贝菜品基本信息
            BeanUtils.copyProperties(item, dto);
            // 获取分类名称并设置
            Category category = categoryService.getById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }

            // 查询当前菜品的口味信息，填充进dishdto
            dto.setFlavors(dishFlavorService.listByDishId(item.getId()));
            return dto;
        }).toList();

        // 缓存进redis
        redisTemplate.opsForValue().set(key, dishDtos, 30, TimeUnit.MINUTES);
        return Result.success(dishDtos);
    }

    /**
     * 更改菜品的状态，可以批量处理
     *  0：停售 1：启售
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("更改菜品状态：{}", ids);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ids != null, Dish::getId, ids)
                .set(Dish::getStatus, status);
        dishService.update(updateWrapper);
        // 清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success("菜品状态更改成功");
    }

    /**
     * 删除
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("删除菜品");
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids)
                .eq(Dish::getStatus, 1);
        long count = dishService.count(queryWrapper);
        if (count > 0) {
            throw new CustomException("存在正在销售的商品，无法删除");
        }
        dishService.removeBatchByIds(ids);
        // 清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success("删除成功");
    }
}
