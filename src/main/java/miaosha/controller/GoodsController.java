package miaosha.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import miaosha.domain.MiaoshaUser;
import miaosha.redis.GoodsKey;
import miaosha.redis.RedisService;
import miaosha.result.Result;
import miaosha.service.GoodsService;
import miaosha.service.MiaoshaUserService;
import miaosha.vo.GoodsDetailVo;
import miaosha.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.List;

import static miaosha.service.MiaoshaUserService.COOKI_NAME_TOKEN;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


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

    /*
    Jmeter: QPS: 5000 with 4w threads, cannot deal with 5w... db connection pool?
     */
    // so neat man
    @RequestMapping(value="/to_list")
//    produces = "text/html"
//    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
        model.addAttribute("user", user);
        //取缓存
//        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
//        if(!StringUtils.isEmpty(html)) {
//            return html;
//        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";

        /*
        //手动渲染
        //取缓存

        // WebContext ctx = new WebContext(request,response,request.getServletContext(),request.getLocale(), model.asMap());
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(request.getServletContext());
        IWebExchange exchange = application.buildExchange(request, response);
        WebContext ctx = new WebContext(exchange);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
        */
    }

    @RequestMapping(value="/to_detail2/{goodsId}")
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
    }

    @RequestMapping(value="/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                                        @PathVariable("goodsId")long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
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
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        return Result.success(vo);
    }
}
