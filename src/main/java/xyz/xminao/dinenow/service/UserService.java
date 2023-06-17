package xyz.xminao.dinenow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.xminao.dinenow.entity.User;

public interface UserService extends IService<User> {
    // 根据手机号（邮箱）获取用户
    User getByPhone(String phone);
}
