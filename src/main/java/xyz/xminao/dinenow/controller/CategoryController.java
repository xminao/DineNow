package xyz.xminao.dinenow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.entity.Category;
import xyz.xminao.dinenow.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        log.info("新增分类：{}", category);
        categoryService.save(category);
        return Result.success("新增成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize) {
        // 设置分页参数
        Page<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    // 根据ID删除分类
    @DeleteMapping
    public Result<String> delete(Long id) {
        log.info("删除分类ID： {}", id);
        categoryService.remove(id);
        return Result.success("分类删除成功");
    }

    // 修改分类信息
    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        log.info("修改分类信息");
        categoryService.updateById(category);
        return Result.success("修改分类成功");
    }

    // 根据条件查询分类下拉列表
    // 前端发来需要分类 1：菜品分类 2：套餐分类，返回对应列表
    @GetMapping("/list")
    public Result<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 添加条件，判断是否为菜品（1：菜品 2：套餐）
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return Result.success(list);
    }

}
