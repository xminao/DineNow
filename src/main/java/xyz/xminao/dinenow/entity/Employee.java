package xyz.xminao.dinenow.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; //  主键

    private String name; // 姓名

    private String username; // 用户名

    private String password; // 密码

    private String phone; // 电话号

    private String sex; // 性别

    private String idNumber; // 身份证号

    private Integer status; // 状态：0：禁用 1：正常

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间

    @TableField(fill = FieldFill.INSERT)
    private Long createUser; // 创建人

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser; // 修改人

}
