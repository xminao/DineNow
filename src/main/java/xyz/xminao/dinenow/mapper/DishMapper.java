package xyz.xminao.dinenow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.xminao.dinenow.entity.Dish;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}