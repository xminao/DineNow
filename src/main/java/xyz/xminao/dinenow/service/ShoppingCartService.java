package xyz.xminao.dinenow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.xminao.dinenow.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    List<ShoppingCart> listByUserId(Long id);
    void removeByUserId(Long id);
}
