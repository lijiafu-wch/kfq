package f.s.kfq.controller;

import f.s.kfq.service.KfqService;
import f.s.kfq.util.KfqUtil;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author lijiafu
 * @date 2019/12/18 17:31
 * @since 1.0
 */
@Controller
@RequestMapping("kfq/callback")
public class CallbackController {
    private static final Logger logger = LoggerFactory.getLogger(CallbackController.class);
    @Autowired
    private KfqService kfqService;


    @RequestMapping(value = "order", method = { RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String orderCallback(HttpServletRequest request) {
        String json = KfqUtil.getBodyJson(request);
        logger.info("库分期订单回调:----------------:" + json);
        try {
            JSONObject jsonResult = kfqService.decodeData(json);
            JSONObject jsonObject = JSONObject.parseObject(jsonResult.getString("data"));
            Integer status = jsonObject.getInteger("status");// 200成功 300失败
            String orderId = jsonObject.getString("orderId");// 订单ID
            Double amount = jsonObject.getDouble("amount");// 金额
            Integer stageNum = jsonObject.getInteger("stageNum");// 分期数
            logger.info("库分期订单回调,参数:----------------:" + status+"-"+orderId+"-"+amount+"-"+stageNum);
            //处理业务逻辑
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "SUCCESS";// 成功

    }

    @RequestMapping(value = "refund", method = { RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String refundCallback(HttpServletRequest request) {
        String json = KfqUtil.getBodyJson(request);
        logger.info("库分期退款回调:----------------:" + json);
        try {
            JSONObject jsonResult = kfqService.decodeData(json);
            JSONObject jsonObject = JSONObject.parseObject(jsonResult.getString("data"));
            Integer status = jsonObject.getInteger("status");// 200成功 300失败
            String refundApplyId = jsonObject.getString("refundApplyId");// 支付流水号
            logger.info("库分期退款回调,参数:----------------:" + status+"-"+refundApplyId);
            //处理业务逻辑
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "SUCCESS";// 成功

    }

}
