package xyz.xminao.dinenow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {
    // 确定对象一致
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // id

    private Integer type; // 类型 1：菜品 2：套餐

    private String name; // 分类名称

    private Integer sort; // 顺序

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间

    @TableField(fill = FieldFill.INSERT)
    private Long createUser; // 创建者

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser; // 更新者
}
