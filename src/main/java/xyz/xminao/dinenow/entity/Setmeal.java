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

// 套餐表
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Setmeal implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // id

    private Long categoryId; // 套餐分类ID

    private String name; // 套餐名称

    private BigDecimal price; // 套餐价格

    private Integer status; // 状态 0：停售 1：正常销售

    private String code; // 编码

    private String description; // 套餐描述

    private String image; // 套餐图片

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间

    @TableField(fill = FieldFill.INSERT)
    private Long createUser; // 创建人

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser; // 修改人

//    private Integer isDeleted; // 逻辑删除
}
