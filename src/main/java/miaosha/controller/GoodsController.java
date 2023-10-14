package miaosha.controller;

import miaosha.domain.MiaoshaUser;
import miaosha.redis.RedisService;
import miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

import static miaosha.service.MiaoshaUserService.COOKI_NAME_TOKEN;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    // duplicate param and code: have to get MiaoshaUser every time,
//    @RequestMapping("/to_list")
//    public String list(Model model,
//                      @CookieValue(value=COOKI_NAME_TOKEN, required = false) String cookieToken,
//                      @RequestParam(value=COOKI_NAME_TOKEN, required = false) String paramToken,
//                      HttpServletResponse response) {
//        if (cookieToken.isEmpty() && paramToken.isEmpty()) {
//            return "login";
//        }
//        String token = paramToken.isEmpty()? cookieToken : paramToken;
//        MiaoshaUser user = userService.getByToken(response, token);
//        model.addAttribute("user", user);
//        return "goods_list";
//    }

    // so neat man
    @RequestMapping("/to_list")
    public String list(Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        return "goods_list";
    }
}
