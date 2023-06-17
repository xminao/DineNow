package xyz.xminao.dinenow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.xminao.dinenow.entity.Employee;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
