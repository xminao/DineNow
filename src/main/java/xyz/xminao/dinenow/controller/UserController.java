package xyz.xminao.dinenow.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.xminao.dinenow.common.Result;
import xyz.xminao.dinenow.entity.User;
import xyz.xminao.dinenow.service.UserService;
import xyz.xminao.dinenow.utils.MailUtils;

import javax.mail.MessagingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MailUtils mailUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    // 发送验证码
    @RequestMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) throws MessagingException {
        String phone = user.getPhone(); // 其实获取的是邮箱
        if (phone != null) {
            // 随机生成一个验证码
            String code = mailUtils.achieveCode();
            log.info("生成的验证码：{}", code);

            // 给邮箱发送验证码
            mailUtils.sendMail(phone, code);
            // 验证码存入session，方便后面拿出来对比
//            session.setAttribute(phone, code);
            // 存入redis中，五分钟有效
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return Result.success("验证码发送成功");
        }
        return Result.error("验证码发送失败");
    }

    // 登录
    @RequestMapping("/login")
    public Result<User>  login(@RequestBody Map map, HttpSession session) {
        log.info("登录数据：{}", map.toString());

        String email = map.get("phone").toString();// 获取输入的邮箱
        String code = map.get("code").toString(); // 获取输入的验证码

        // 从session中获取验证码
//        Object codeInSession = session.getAttribute(email);
        // 从redis中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(email);

        ///////////// 免登录测试用
        User user = userService.getByPhone(email);
        if (user != null) {
            session.setAttribute("user", user.getId());
            return Result.success(user);
        }
        //////////////

//        // 进行验证码比对
//        if (codeInSession != null && codeInSession.equals(code)) {
//            User user = userService.getByPhone(email);
//            // 未注册新用户
//            if (user == null) {
//                user = new User();
//                user.setPhone(email);
//                user.setStatus(1);
//                userService.save(user);
//            }
//            // 保存到session
//            session.setAttribute("user", user.getId());
//
//            // 如果登录成功，删除redis中的验证码
//            redisTemplate.delete(email);
//
//            return Result.success(user);
//        }
        return Result.error("登录失败");
    }

    // 登出，直接从session中删除
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return Result.success("登出成功");
    }

}
