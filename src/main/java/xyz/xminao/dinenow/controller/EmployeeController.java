package xyz.xminao.dinenow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.entity.Employee;
import xyz.xminao.dinenow.service.EmployeeService;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController{
    @Autowired
    private EmployeeService employeeService;

    // 登录功能
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 提交密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 没查到就返回登陆失败
        if (emp == null) {
            return Result.error("登录失败，用户名不存在");
        }

        // 查到用户名，对比密码
        if (!emp.getPassword().equals(password)) {
            return Result.error("登陆失败，密码错误");
        }

        // 查看员工状态，是否被禁用
        if (emp.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }

        // 登录成功，将员工ID存入session并返回登录成功
        // 以后改jwt
        request.getSession().setAttribute("employee", emp.getId());
        return Result.success(emp);
    }

    // 登出功能
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        // 移除session中的员工id
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    // 新增员工
    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，信息：{}", employee);

        // 设置初始密码为123456，进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

// 公共字段 可以注释掉
//        // 设置创建和更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        // 获取当前进行新增员工操作的员工ID
//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        // 设置创建者和更新者ID
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        // 存入数据库
        employeeService.save(employee);
        return Result.success("新增员工成功");
    }

    // 员工信息分页查询
    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}",page, pageSize, name);

        // 构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(StringUtils.hasLength(name), Employee::getName, name)
                .orderByDesc(Employee::getUpdateTime);// 添加排序条件

        // 执行查询
        employeeService.page(pageInfo, queryWrapper);
        return Result.success(pageInfo);
    }

    // 根据ID修改员工信息
    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("更新的信息：{}", employee);

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());

        employeeService.updateById(employee);

        return Result.success("员工信息修改成功");
    }

    // 根据ID查询员工信息
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据ID {} 查询员工", id);

        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("没有查询到对应信息");
    }
}
