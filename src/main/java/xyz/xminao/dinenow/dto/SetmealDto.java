package xyz.xminao.dinenow.dto;

import lombok.Data;
import xyz.xminao.dinenow.entity.Setmeal;
import xyz.xminao.dinenow.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    // 套餐和菜品对应关系
    private List<SetmealDish> setmealDishes;
    // 套餐分类名
    private String categoryName;
}
