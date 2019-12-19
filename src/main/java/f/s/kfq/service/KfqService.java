package f.s.kfq.service;

import f.s.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import f.s.kfq.util.KfqUtil;

/**
 * @author Administrator
 * @version
 * @see
 */
@Service
public class KfqService {
	private static final Logger logger = LoggerFactory.getLogger(KfqService.class);

	private static final String gateway_url = "http://111.194.126.231:7010/kubaitiao/merchant/gateway";
	private static final String api_url = "http://111.194.126.231:7010/kubaitiao/external/merchant/api";
	private static final String aesKey = "jOu0jhbWjNIMg4d3HcEPdxkX9ueGerTmKJ2oZ7ncSZzu9OCBLK8zhRSDLOmuxxIK";
	private static final String aesIv = "0102030405060708";
	private static final String md5Key = "kMKtdC1hHcxXPPF9GRWGK1PsT1hDKoUKl4XSkDWiFTwNY7iCVV7HDAugnQmZC0XN";
	private static final String midPlatform = "1000100203";
	private static final String version = "V1.0";
	private static final String callbackURL = "http://47.99.83.51:18080/";
	private static final String notifyURL = "http://47.99.83.51:18080/kfq/callback/order";
	private static final String refundNotifyURL = "http://47.99.83.51:18080/kfq/callback/refund";
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 统一参数
	 * @author lijiafu
	 * @date 2019/12/19 14:27
	 */
	private Map<String, Object> getUnifyParam(String userId,String ip,String opType){
		Map<String, Object> mainMap = new HashMap<String, Object>();
		mainMap.put("midTransaction", midPlatform);
		mainMap.put("opType", opType);
		mainMap.put("time", System.currentTimeMillis());
		mainMap.put("uid", userId);// APP用户ID
		Map<String, Object> riskMap = new HashMap<String, Object>();
		riskMap.put("ip", ip);
		mainMap.put("risk", riskMap);
		return mainMap;
	}

	/**
	 * 创建分期订单
	 * @author lijiafu
	 * @date 2019/12/18 14:49
	 */
	public  Map<String, String> createOrder(String orderId, String userId,
			Double amount,String productInfo, Integer stageNum,String ip) throws Exception {
		Map<String, Object> mainMap = getUnifyParam(userId,ip,"1001");
		Map<String, Object> dateMap = new HashMap<String, Object>();
		dateMap.put("orderId", orderId);
		dateMap.put("amount", amount);
		dateMap.put("stageNum", stageNum);
		dateMap.put("productInfo",productInfo);
		dateMap.put("notifyURL", notifyURL);
		dateMap.put("callbackURL", callbackURL);
		mainMap.put("data", dateMap);
		String json = JSONObject.toJSONString(mainMap);
		System.out.println("--加密前->>" + json);
		String sign = KfqUtil.md5(json + md5Key);
		String data = KfqUtil.aesEncrypt(aesKey,aesIv,json);
        Map<String, String> params = new HashMap<String, String>();
        params.put("sign", sign);
        params.put("data", data);
        params.put("midPlatform", midPlatform);
        params.put("version", version);
        params.put("url", gateway_url);
		return params;
	}

	/**
	 * 交易结果查询
	 * @author lijiafu
	 * @date 2019/12/18 14:49
	 */
	public void queryTranResult(String orderId, String userId,String ip) throws Exception {
		Map<String, Object> mainMap = getUnifyParam(userId,ip,"4001");
		Map<String, Object> dateMap = new HashMap<String, Object>();
		dateMap.put("orderId", orderId);
		mainMap.put("data", dateMap);
		String json = JSONObject.toJSONString(mainMap);
		String sign = KfqUtil.md5(json + md5Key);
		String data = KfqUtil.aesEncrypt(aesKey,aesIv,json);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sign",sign);
		jsonObj.put("data",data);
		jsonObj.put("midPlatform",midPlatform);
		jsonObj.put("version",version);
		HttpEntity<String> entity = new HttpEntity<>(jsonObj.toString(), headers);
		ResponseEntity<JSONObject> exchange = restTemplate.exchange(api_url,
				HttpMethod.POST, entity, JSONObject.class);
		System.out.println(exchange.getBody());
		JSONObject obj = decodeData(exchange.getBody().toString());
		String result = obj.getString("result");
		if(StringUtils.isNotBlank(result) && result.equals("200")){//成功
			JSONObject jsonObject = JSONObject.parseObject(obj.getString("data"));
			String status  = jsonObject.getString("status");
			System.out.println(status);
		}else{
			String errCode = obj.getString("errCode");
			String errMsg = obj.getString("errMsg");
			System.out.println("错误信息");
		}
	}


	/**
	 * 用户交易查询
	 * @author lijiafu
	 * @date 2019/12/18 14:49
	 */
	public void queryTranByUser(String orderId, String userId, String ip, String status, String startTime, String endTime) throws Exception {
		Map<String, Object> mainMap = getUnifyParam(userId,ip,"4002");
		Map<String, Object> dateMap = new HashMap<String, Object>();
		dateMap.put("orderId", orderId);
		dateMap.put("status", status);
		dateMap.put("startTime", startTime);
		dateMap.put("endTime", endTime);
		mainMap.put("data", dateMap);
		String json = JSONObject.toJSONString(mainMap);
		String sign = KfqUtil.md5(json + md5Key);
		String data = KfqUtil.aesEncrypt(aesKey,aesIv,json);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sign",sign);
		jsonObj.put("data",data);
		jsonObj.put("midPlatform",midPlatform);
		jsonObj.put("version",version);
		HttpEntity<String> entity = new HttpEntity<>(jsonObj.toString(), headers);
		ResponseEntity<JSONObject> exchange = restTemplate.exchange(api_url,
				HttpMethod.POST, entity, JSONObject.class);
		System.out.println(exchange.getBody());
		JSONObject obj = decodeData(exchange.getBody().toString());
		String result = obj.getString("result");
		if(StringUtils.isNotBlank(result) && result.equals("200")){//成功
			JSONArray jsonArray = JSONObject.parseArray(obj.getString("data"));
			System.out.println(jsonArray.toString());
			if(jsonArray.size() > 0){
				JSONObject jsonObject = JSONObject.parseObject(jsonArray.getString(0));
				String oid  = jsonObject.getString("orderId");
				System.out.println(oid);
			}
		}else{
			String errCode = obj.getString("errCode");
			String errMsg = obj.getString("errMsg");
			System.out.println("错误信息");
		}
	}


	/**
	 * 退款申请
	 * @author lijiafu
	 * @date 2019/12/18 14:49
	 */
	public void refund(String orderId, String userId, String ip, String applyId, Double refundAmount,
	                   String reson) throws Exception {
		Map<String, Object> mainMap = getUnifyParam(userId,ip,"3001");
		Map<String, Object> dateMap = new HashMap<String, Object>();
		dateMap.put("orderId", orderId);
		dateMap.put("applyId", applyId);
		dateMap.put("refundAmount", refundAmount);
		dateMap.put("reson", reson);
		dateMap.put("callbackURL", refundNotifyURL);
		mainMap.put("data", dateMap);
		String json = JSONObject.toJSONString(mainMap);
		String sign = KfqUtil.md5(json + md5Key);
		String data = KfqUtil.aesEncrypt(aesKey,aesIv,json);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sign",sign);
		jsonObj.put("data",data);
		jsonObj.put("midPlatform",midPlatform);
		jsonObj.put("version",version);
		HttpEntity<String> entity = new HttpEntity<>(jsonObj.toString(), headers);
		ResponseEntity<JSONObject> exchange = restTemplate.exchange(api_url,
				HttpMethod.POST, entity, JSONObject.class);
		System.out.println(exchange.getBody());
		JSONObject obj = decodeData(exchange.getBody().toString());
		String result = obj.getString("result");
		if(StringUtils.isNotBlank(result) && result.equals("200")){//成功
			JSONObject jsonObject = JSONObject.parseObject(obj.getString("data"));
			String status  = jsonObject.getString("status");
			System.out.println(status);
		}else{
			String errCode = obj.getString("errCode");
			String errMsg = obj.getString("errMsg");
			System.out.println("错误信息");
		}
	}


	/**
	 * 用户退款查询
	 * @author lijiafu
	 * @date 2019/12/18 14:49
	 */
	public void queryRefundbyUser(String orderId, String userId, String ip, String applyId,
	                              String startDate,String endDate,Integer pageSize,Integer pageNum) throws Exception {
		Map<String, Object> mainMap = getUnifyParam(userId,ip,"4005");
		Map<String, Object> dateMap = new HashMap<String, Object>();
		dateMap.put("orderId", orderId);
		dateMap.put("applyId", applyId);
		dateMap.put("startDate", startDate);
		dateMap.put("endDate", endDate);
		dateMap.put("pageSize", pageSize);
		dateMap.put("pageNum", pageNum);
		mainMap.put("data", dateMap);
		String json = JSONObject.toJSONString(mainMap);
		String sign = KfqUtil.md5(json + md5Key);
		String data = KfqUtil.aesEncrypt(aesKey,aesIv,json);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sign",sign);
		jsonObj.put("data",data);
		jsonObj.put("midPlatform",midPlatform);
		jsonObj.put("version",version);
		HttpEntity<String> entity = new HttpEntity<>(jsonObj.toString(), headers);
		ResponseEntity<JSONObject> exchange = restTemplate.exchange(api_url,
				HttpMethod.POST, entity, JSONObject.class);
		System.out.println(exchange.getBody());
		JSONObject obj = decodeData(exchange.getBody().toString());
		String result = obj.getString("result");
		if(StringUtils.isNotBlank(result) && result.equals("200")){//成功
			JSONObject jsonObject = JSONObject.parseObject(obj.getString("data"));
			int count  = jsonObject.getInteger("count");
			JSONArray jsonArray = JSONObject.parseArray(jsonObject.getString("record"));
			System.out.println(jsonArray.toString());
			if(jsonArray.size() > 0){
				JSONObject recordJson = JSONObject.parseObject(jsonArray.getString(0));
				String oid  = recordJson.getString("orderId");
				String aId  = recordJson.getString("applyId");
				Double refundAmount  = recordJson.getDouble("refundAmount");
				int status  = recordJson.getInteger("status");
				String applyDate  = recordJson.getString("applyDate");
				String reson  = recordJson.getString("reson");
				String operateDate  = recordJson.getString("operateDate");
			}
		}else{
			String errCode = obj.getString("errCode");
			String errMsg = obj.getString("errMsg");
			System.out.println("错误信息");
		}
	}


	public  JSONObject decodeData(String resultJson) {
	        try {
		        JSONObject jsonObject = JSONObject.parseObject(resultJson);
	            String data = (String) jsonObject.get("data"); 
	            String aseStr = KfqUtil.aesDecrypt(aesKey, data);
	            return JSONObject.parseObject(aseStr);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		KfqService kfqService = new KfqService();
		Map<String, String> params = kfqService.createOrder("20000123123", "20001", 5.4, "商品信息",1,"127.0.0.1");
		System.out.println(params);
	}

}
