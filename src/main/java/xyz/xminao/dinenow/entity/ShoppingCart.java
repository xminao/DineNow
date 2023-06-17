package xyz.xminao.dinenow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // id

    private String name; // 名称

    private Long userId; // 用户id

    private Long dishId; // 菜品id

    private Long setmealId; // 套餐id

    private String dishFlavor; //  菜品的口味

    private Integer number; // 数量

    private BigDecimal amount; // 金额

    private String image; // 图片

    private LocalDateTime createTime;
}
