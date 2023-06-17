package xyz.xminao.dinenow.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
// 统一返回结果类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code; // 1:成功 0：失败
    private String msg; // 错误信息
    private T data; // 数据
    private Map map = new HashMap(); // 动态数据

    public static <T> Result<T> success(T data) {
        Result<T> res = new Result<>();
        res.code = 1;
        res.data = data;
        return res;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> res = new Result<>();
        res.code = 0;
        res.msg = msg;
        return res;
    }

    public Result<T> add(String msg, String value) {
        this.map.put(msg, value);
        return this;
    }
}

