package xyz.xminao.dinenow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜品口味
 */
@Data
public class DishFlavor implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // i

    private Long dishId; // 菜品id

    private String name; // 口味名称

    private String value; // 口味数据list

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
