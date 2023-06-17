package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.xminao.dinenow.entity.Employee;
import xyz.xminao.dinenow.mapper.EmployeeMapper;
import xyz.xminao.dinenow.service.EmployeeService;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
