package f.s.kfq.controller;

import f.s.kfq.service.KfqService;
import f.s.utils.web.WebUtil;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author lijiafu
 * @date 2019/12/18 13:30
 * @since 1.0
 */
@Controller
@RequestMapping("kfq")
public class KfqController {
    @Autowired
    private KfqService kfqService;



    /**
     * 分期
     * @author lijiafu
     * @date 2019/12/18 17:11
     */
    @RequestMapping(value = "create/{orderId}/{userId}",method = { RequestMethod.GET,RequestMethod.POST})
    public String  test(@PathVariable String orderId,@PathVariable String userId, HttpServletRequest request, Model model){
        try {
            Map<String, String> params = kfqService.createOrder(orderId,userId  ,
                    1000D,"商品信息",
                    3, WebUtil.getIpAddr(request));
            model.addAttribute("sign", params.get("sign"));
            model.addAttribute("data", params.get("data"));
            model.addAttribute("midPlatform", params.get("midPlatform"));
            model.addAttribute("version", params.get("version"));
            model.addAttribute("url", params.get("url"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }

    /**
     * 查询交易结果
     * @author lijiafu
     * @date 2019/12/18 17:11
     */
    @RequestMapping(value = "query/{orderId}/{userId}",method = { RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String  queryTranResult(@PathVariable String orderId,@PathVariable String userId, HttpServletRequest request){
        try {
            kfqService.queryTranResult(orderId,userId, WebUtil.getIpAddr(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    /**
     * 查询用户交易
     * @author lijiafu
     * @date 2019/12/18 17:11
     */
    @RequestMapping(value = "query/user/{orderId}/{userId}",method = { RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String  queryTranByUser(@PathVariable String orderId, @PathVariable String userId,
                                   String status, String startTime, String endTime,
                                   HttpServletRequest request){
        try {
            kfqService.queryTranByUser(orderId,userId, WebUtil.getIpAddr(request),status,startTime,endTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }


    /**
     * 退款申请
     * @author lijiafu
     * @date 2019/12/18 17:12
     */
    @RequestMapping(value = "refund/{orderId}/{userId}",method = { RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String  refund(@PathVariable String orderId, @PathVariable String userId,
                                   String applyId, Double refundAmount, String reson,
                                   HttpServletRequest request){
        try {
            kfqService.refund(orderId,userId, WebUtil.getIpAddr(request),applyId,refundAmount,reson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

    /**
     * 退款查询
     * @author lijiafu
     * @date 2019/12/18 17:12
     */
    @RequestMapping(value = "refund/query/{orderId}/{userId}",method = { RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String  queryRefundbyUser(@PathVariable String orderId, @PathVariable String userId,
                          String applyId, String startDate, String endDate,Integer pageSize,Integer pageNum,
                          HttpServletRequest request){
        try {
            kfqService.queryRefundbyUser(orderId,userId, WebUtil.getIpAddr(request),applyId,startDate,endDate,pageSize,pageNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }
}
