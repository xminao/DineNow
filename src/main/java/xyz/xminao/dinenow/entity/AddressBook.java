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
public class AddressBook implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // id

    private Long userId; // 用户id

    private String consignee; // 收货人

    private String phone; // 收货人手机号

    private String sex; // 0 女 1 男

    private String provinceCode; // 省级区划编号

    private String provinceName; // 省名字

    private String cityCode; // 市级区划编号

    private String cityName; // 城市名称

    private String districtCode; // 区级划编号

    private String districtName; // 区名

    private String detail; // 详细地址

    private String label; // 标签

    private Integer isDefault; // 是否默认 0否 1是，默认否

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
