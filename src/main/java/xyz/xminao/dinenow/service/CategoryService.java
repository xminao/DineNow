package xyz.xminao.dinenow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.xminao.dinenow.entity.Category;

public interface CategoryService extends IService<Category> {
    // 自定义删除分类方法
    void remove(Long id);
}
