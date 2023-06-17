package xyz.xminao.dinenow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.xminao.dinenow.common.BaseContext;
import xyz.xminao.dinenow.common.CustomException;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.entity.AddressBook;
import xyz.xminao.dinenow.service.AddressBookService;

import java.util.List;

/**
 * 地址簿管理
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    // 查询指定用户的所有地址
    @RequestMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("列出地址簿请求：{}", addressBook);
        Long userId = BaseContext.getCurrentId();
        return Result.success(addressBookService.listByUserId(userId));
    }

    // 新增地址
    @PostMapping
    public Result<String> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("新增地址：{}", addressBook);
        addressBookService.save(addressBook);
        return Result.success("新增地址成功");
    }

    // 设置默认地址
    @PutMapping("/default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址：{}", addressBook);
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId())
                .set(AddressBook::getIsDefault, 0);
        addressBookService.update(lambdaUpdateWrapper); // 将原来默认地址归为空
        // 将当前的地址设为默认地址 isdefault = 1
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }

    // 获取默认地址
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        log.info("获取默认地址，id: {}", BaseContext.getCurrentId());

        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(userId != null, AddressBook::getUserId, userId)
                .eq(AddressBook::getIsDefault, 1);
        return Result.success(addressBookService.getOne(lambdaQueryWrapper));
    }

    // 根据id获取地址信息
    @GetMapping("/{id}")
    public Result<AddressBook> get(@PathVariable Long id) {
        log.info("获取地址，id：{}", id);
        return Result.success(addressBookService.getById(id));
    }

    // 修改地址
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook) {
        log.info("修改地址：{}", addressBook);
        addressBookService.updateById(addressBook);
        return Result.success("地址修改成功");
    }

    /**
     * 删除地址
     *
     * 可以优化的：
     *      删除了默认地址，需要顺眼设置默认地址
     */
    @DeleteMapping
    public Result<String> delete(@RequestParam("ids") Long ids) {
        log.info("删除地址：{}", ids);
        if (ids == null) {
            throw new CustomException("地址不存在，操作失败");
        }
        AddressBook addressBook = addressBookService.getById(ids);
        if (addressBook == null) {
            throw new CustomException("地址不存在，操作失败");
        }
        addressBookService.removeById(ids);
        return Result.success("地址删除成功");
    }
}
