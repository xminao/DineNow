package xyz.xminao.dinenow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.xminao.dinenow.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    // 根据用户ID列出其地址簿
    List<AddressBook> listByUserId(Long id);
}
