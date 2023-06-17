package xyz.xminao.dinenow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// 套餐菜品对应表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetmealDish implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // id

    private Long setmealId; // 套餐ID

    private Long dishId; // 菜品ID

    private String name; // 菜品名称，冗余字段

    private BigDecimal price; // 菜品原价

    private Integer copies; // 份数

    private Integer sort; // 排序

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间

    @TableField(fill = FieldFill.INSERT)
    private Long createUser; // 创建者

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser; // 更新者

    private Integer isDeleted; // 逻辑删除
}
