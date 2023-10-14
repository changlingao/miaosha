package miaosha.service;

import miaosha.domain.MiaoshaUser;
import miaosha.domain.OrderInfo;
import miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    // 事务：减库存 下订单 写入秒杀订单
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        goodsService.reduceStock(goods);
        // change two tables: order_info, miaosha_order
        return orderService.createOrder(user, goods);
    }
}
