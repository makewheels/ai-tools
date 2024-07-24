package com.github.makewheels.aitools.user;

import com.github.makewheels.aitools.system.response.ErrorCode;
import com.github.makewheels.aitools.system.response.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("login")
    public Result<User> login(@RequestParam String code) {
        return Result.ok(userService.login(code));
    }

    @GetMapping("getUserByToken")
    public Result<User> getUserByToken(@RequestParam String token) {
        User user = userService.getUserByToken(token);
        if (user != null) {
            return Result.ok(user);
        } else {
            return Result.error(ErrorCode.USER_TOKEN_WRONG);
        }
    }

    @GetMapping("getUserById")
    public Result<User> getUserById(@RequestParam String userId) {
        User user = userService.getUserById(userId);
        return Result.ok(user);
    }

}
