package xyz.xminao.dinenow.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

// 用户信息
@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id; // id

    private String name; // 姓名

    private String phone; // 电话号

    private String sex; // 性别 0：女 1：男

    private String idNumber; // 身份证号

    private String avatar; // 头像

    private Integer status; // 状态 0：禁用 1：正常
}
