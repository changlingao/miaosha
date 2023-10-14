package miaosha.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import miaosha.domain.MiaoshaOrder;
import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.redis.RedisService;
import miaosha.result.CodeMsg;
import miaosha.result.Result;
import miaosha.service.GoodsService;
import miaosha.service.MiaoshaService;
import miaosha.service.MiaoshaUserService;
import miaosha.service.OrderService;
import miaosha.vo.GoodsVo;
import miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @RequestMapping("/do_miaosha")
    public String list(Model model, MiaoshaUser user, @RequestParam("goodsId") Long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }

    	//判断库存
    	GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
    	int stock = goods.getStockCount();
    	if(stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
    	}
    	//事务：减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }
}

