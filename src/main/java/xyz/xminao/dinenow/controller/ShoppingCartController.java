package xyz.xminao.dinenow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.xminao.dinenow.common.BaseContext;
import xyz.xminao.dinenow.common.CustomException;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.entity.ShoppingCart;
import xyz.xminao.dinenow.service.ShoppingCartService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    // 加入购物车
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("添加购物车：{}", shoppingCart);

        // 设置用户id，指定当前购物车是哪个用户的
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 获取添加的菜品id
        Long dishId = shoppingCart.getDishId();
        // 获取用户原来购物车中是否有要添加的菜品/套餐
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        // 如果是菜品，就用菜品判断条件
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else { // 如果不是菜品，就是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, dishId);
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        // 如果原来购物车中有当前菜品/套餐就数量加一即可
        if (cartServiceOne != null) {
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else { // 如果不存在就是数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return Result.success(cartServiceOne);
    }

    // 查看购物车集合
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        log.info("查看购物车...");

        Long userId = BaseContext.getCurrentId();
        return Result.success(shoppingCartService.listByUserId(userId));
    }

    // 清空购物车
    @DeleteMapping("/clean")
    public Result<String> clean() {
        log.info("清空购物车，id：{}", BaseContext.getCurrentId());
        shoppingCartService.removeByUserId(BaseContext.getCurrentId());
        return Result.success("清空购物车成功");
    }

    /**
     * 从购物车中删除商品
     *
     * 分析：
     *      前端传来 json：dishId, setmealId
     *      数量为0就删除
     */
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart) {
        // 获取当前用户id
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new CustomException("用户数据异常，操作失败");
        }

        // 获取传来的菜品/套餐id
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        if ((dishId == null && setmealId == null) || (dishId != null && setmealId != null)) {
            throw new CustomException("数据异常，操作失败");
        }

        // 根据当前对购物车进行更改
        // 获取当前用户的购物车数据
        List<ShoppingCart> carts = shoppingCartService.listByUserId(userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        // 根据传来的是菜品还是套餐进行对应减少操作
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        }

        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        if (cart.getNumber() > 1) {
            cart.setNumber(cart.getNumber() - 1); // 数量大于1的减一
            shoppingCartService.updateById(cart);
        } else {
            shoppingCartService.remove(queryWrapper); // 直接删除
        }

        return Result.success("减少购物车中的商品");
    }
}
