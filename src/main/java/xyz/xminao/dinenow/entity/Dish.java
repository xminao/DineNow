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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dish implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // id

    private String name; // 菜品名称

    private Long categoryId; // 菜品分类id

    private BigDecimal price; // 菜品价格

    private String code; // 菜品码

    private String image; // 图片

    private String description; // 菜品描述

    private Integer status; // 菜品状态 0：停售 1：正常销售

    private Integer sort; // 展示顺序
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间

    @TableField(fill = FieldFill.INSERT)
    private Long createUser; // 创建者

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser; // 更新者

   // private Integer isDeleted; // 逻辑删除
}
