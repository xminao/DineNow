package xyz.xminao.dinenow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.xminao.dinenow.entity.AddressBook;
import xyz.xminao.dinenow.mapper.AddressBookMapper;
import xyz.xminao.dinenow.service.AddressBookService;

import java.util.List;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Override
    public List<AddressBook> listByUserId(Long id) {
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null, AddressBook::getUserId, id);
        lambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        return this.list(lambdaQueryWrapper);
    }
}
