package miaosha.controller;

import miaosha.domain.MiaoshaUser;
import miaosha.redis.RedisService;
import miaosha.service.GoodsService;
import miaosha.service.MiaoshaUserService;
import miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static miaosha.service.MiaoshaUserService.COOKI_NAME_TOKEN;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

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
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @RequestMapping(value="/detail/{goodsId}")
    public String detail(Model model, MiaoshaUser user,
                         @PathVariable("goodsId")long goodsId) {
        model.addAttribute("user", user);

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        // 秒杀
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if(now < startAt ) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "goods_detail";
//        GoodsDetailVo vo = new GoodsDetailVo();
//        vo.setGoods(goods);
//        vo.setUser(user);
//        vo.setRemainSeconds(remainSeconds);
//        vo.setMiaoshaStatus(miaoshaStatus);
//        return Result.success(vo);
    }


}
