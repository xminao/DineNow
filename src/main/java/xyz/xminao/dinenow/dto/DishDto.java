package xyz.xminao.dinenow.dto;

import lombok.Data;
import xyz.xminao.dinenow.entity.Dish;
import xyz.xminao.dinenow.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

// 用于表现层展示的层间传递对象
@Data
public class DishDto extends Dish {
    // 菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    // 菜品分类名
    private String categoryName;

    private Integer copies;
}
