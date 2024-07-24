package com.github.makewheels.aitools.user;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.makewheels.aitools.utils.IdService;
import com.github.makewheels.aitools.wechat.WechatUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    @Resource
    private UserRedisService userRedisService;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private UserRepository userRepository;
    @Resource
    private IdService idService;
    @Resource
    private WechatUserService wechatUserService;

    /**
     * 小程序登录
     */
    public User login(String code) {
        JSONObject response = wechatUserService.login(code);
        String openid = response.getString("openid");
        return saveNewUser(openid);
    }

    /**
     * 根据openid创建新用户
     */
    private User saveNewUser(String openid) {
        User user = userRepository.getByOpenid(openid);
        if (user == null) {
            user = new User();
            user.setId(idService.getUserId());
            user.setToken("tk_" + IdUtil.getSnowflakeNextIdStr());
            user.setOpenid(openid);
            mongoTemplate.save(user);
        }
        return user;
    }

    /**
     * 根据登录token获取用户
     */
    public User getUserByToken(String token) {
        if (token == null) {
            return null;
        }
        //先看redis有没有
        User user = userRedisService.getUserByToken(token);
        //如果redis已经有了，返回ok
        if (user != null) {
            return user;
        }

        //如果redis没有，查mongo
        user = userRepository.getByToken(token);

        //如果mongo有，放到redis里，返回ok
        if (user != null) {
            userRedisService.setUserByToken(user);
            return user;
        }

        //如果mongo也没有，那这时候它需要重新登录了
        return null;
    }

    /**
     * 获取用户登录信息
     * 获取token方式有两种，header和url参数
     */
    public User getUserByRequest(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isEmpty(token)) {
            String[] tokens = request.getParameterMap().get("token");
            if (tokens != null) {
                token = tokens[0];
            }
        }
        return getUserByToken(token);
    }

    /**
     * 根据id查用户
     */
    public User getUserById(String userId) {
        User user = userRepository.getById(userId);
        if (user != null) {
            user.setToken(null);
        }
        return user;
    }

}
