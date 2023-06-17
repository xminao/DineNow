package xyz.xminao.dinenow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.xminao.dinenow.entity.SetmealDish;

import java.util.List;

public interface SetmealDishService extends IService<SetmealDish> {
    void removeBySetmealIds(List<Long> ids);
}
