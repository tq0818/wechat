package com.ycya.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.leyicat.db.QueryResult;
import com.leyicat.mvc.MvcConst;
import com.leyicat.support.JwtHelper;
import com.leyicat.util.HttpUtil;
import com.leyicat.util.JsonUtil;
import com.leyicat.util.SecurityUtil;
import com.ycya.common.ServiceResult;

public class SysUtil{
	public static void dealListReseult(HttpServletResponse resp,int code,String msg,Object src) throws IOException{
		Map<String,Object> result = new HashMap<>();
		result.put("code", code);
		result.put("msg", msg);
		if(src instanceof QueryResult){
			QueryResult qr = (QueryResult)src;
			result.put("count", qr.getTotal());
			result.put("data", qr.getData());
		}else if(src instanceof List){
			int i =3;
			List list = (List)src;
			result.put("count", list.size());
			result.put("data", list);
		}
		/*doRespOut(resp,code,msg,result);*/
		doRespOut(resp, JsonUtil.toJSON(result));
	}
	
	public static String getUserNameFromReq(HttpServletRequest req){
		return SysUtil.getUserNameFromToken(SysUtil.parseTokenFromReq(req));
	}

	/**
	 * 根据用户名创建token
	 * @param userName
	 * @return
	 */
	public static String genToken(String userName) {
        String token = null;
        try {
            token = JwtHelper.createToken(URLEncoder.encode(userName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return token;
    }
	
	/**
	 * 从请求头中解析出token
	 * @param req
	 * @return
	 */
	public static String parseTokenFromReq(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");// "Bearer "
        if (null == auth || auth.length() <= 7) {
            return null;
        }
        return auth.substring(7);
    }

	/**
	 * 从Token中解析出用户名
	 * @param token
	 * @return
	 */
    public static String getUserNameFromToken(String token) {
        String tmpPayload = SecurityUtil.base64decoder(token.split("\\.")[1]);
        if (null == tmpPayload || "".equals(tmpPayload)) {
            return "";
        }

		JsonNode json = JsonUtil.toJSONObject(tmpPayload);
        if (null == json || "".equals(json) || null == json.get("aud")) {
            return "";
        }
        try {
            return URLDecoder.decode(json.get("aud").asText(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

	/**
	 * 检查Token，0成功，1过期，9其他错误
	 * @param token
	 * @return
	 */
	public static int checkToken(String token) {
		@SuppressWarnings("rawtypes")
		Map claims = JwtHelper.verifyToken(token);
		return (int) claims.get("code");
	}	

	/**
	 * 组装响应
	 * 
	 * @param code
	 * @param msg
	 * @param data
	 * @return
	 */
	public static ServiceResult buildResp(int code, String msg,
			Object data) {
		ServiceResult result = new ServiceResult();
		result.setCode(code);
		result.setMsg(msg);
		if (null != data)
			result.setData(data);
		return result;
	}
	
	public static <T> Map<String,Object> buildRespForLayer(int code, String msg,QueryResult<T> data) {
		Map<String,Object> result = new HashMap<>();
		result.put("code",code);
		result.put("msg",msg);
		result.put("count", data.getTotal());
		
		if (null != data.getData())
			result.put("data", data.getData());
		return result;
	}

	public static void doRespOut(HttpServletResponse rep, int code, String msg,
			Object data) throws IOException {
		doRespOutJson(rep, buildResp(code, msg, data));
	}
	
	public static <T> void doRespOutForLayer1(HttpServletResponse rep, int code, String msg,
			QueryResult<T> data) throws IOException {
		doRespOutJson(rep, buildRespForLayer(code, msg, data));
	}
	
	/**
	 * 返回数据
	 * 
	 * @param rep
	 * @throws IOException
	 */
	public static void doRespOut(HttpServletResponse rep, String result)
			throws IOException {
		PrintWriter out = rep.getWriter();
		out.write(result);
		out.flush();
	}



	public static void doRespOutJson(HttpServletResponse rep, Object result)
			throws IOException {
		PrintWriter out = rep.getWriter();
		out.write(JsonUtil.toJSON(result));
		out.flush();
	}
	
	public static void doRespOutAMD(HttpServletResponse rep, int moduleCode) throws IOException {
		int code = -1;
		String msg = null;
		if(moduleCode==MvcConst.RESPCODE_OK){
			code = MvcConst.RESPCODE_OK;
			msg = MvcConst.RESPCODE_OK_MSG;
		}else if(moduleCode==MvcConst.RESPCODE_DATA_EXIST){
			code = MvcConst.RESPCODE_DATA_EXIST;
			msg = MvcConst.RESPCODE_DATA_EXIST_MSG;
		}else if(moduleCode==MvcConst.RESPCODE_DATA_NOT_EXIST){
			code = MvcConst.RESPCODE_DATA_NOT_EXIST;
			msg = MvcConst.RESPCODE_DATA_NOT_EXIST_MSG;
		}else{
			code = MvcConst.RESPCODE_UNKNOW;
			msg = MvcConst.RESPCODE_UNKNOW_MSG;
		}
		doRespOutJson(rep, buildResp(code, msg, null));
	}
	/**
	 * 5-9 添加httprequest对象转map
	 * @param req
	 * @return
	 */
	public static Map<String, String> getHttpParas(HttpServletRequest req) {
		return HttpUtil.parseRequset(req);
	}
	
	/**
	  * map转化为bean的方法
	  * @param javaBean
	  * @param map
	  * @return
	  */
	 public static <T> T mapTobean(Map<String,String> map,Class<T> clz) throws Exception{  
		    //创建JavaBean对象  
		    T obj = clz.newInstance();  
		    //获取指定类的BeanInfo对象  
		    BeanInfo beanInfo = Introspector.getBeanInfo(clz, Object.class);  
		    //获取所有的属性描述器  
		    PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();  
		    for(PropertyDescriptor pd:pds){  
		        Object value = map.get(pd.getName());  
		        Method setter = pd.getWriteMethod();  
		        setter.invoke(obj, value);  
		    }  
		    return  obj;  
		}
	 /**
		 * 5.15 判断数组中有没有改元素
		 * @param arr
		 * @param targetValue
		 * @return
		 */
		public static boolean useArrayUtils(String[] arr,String targetValue){
		    return  ArrayUtils.contains(arr,targetValue);
		}
		
		/**
		 * 判断要删除的数据
		 * 5-15
		 * @param arr
		 * @param list
		 * @return
		 */
		public static String[] judgeDeleteData(String[] arr,List<?> list){
			String newPowerId = new String();
			for(int i = 0; i < list.size(); i++) {     //通过循环输出列表中的内容
		         String mk = list.get(i).toString();
		          // 这里是判断--新数组里面有没有-旧的数组的元素   没有的话，即为要删除的数组值
		         if(!useArrayUtils(arr,mk)){
		        	 newPowerId+=mk+",";
		         }
		         }
			if(!newPowerId.equals("")){
			String k = newPowerId.substring(0,newPowerId.length()-1);
			String[] delData = k.split(","); 
			return delData;
			}
		   return null;
		}
		/**
		 * 5-15
		 * 判断要添加的数据
		 * @param listMap
		 * @param strList
		 * @return
		 */
		public static String[] judgeAddDataList(List<?> listMap,List<String> strList){
		 String newPowerId = new String();
	     for (int i = 0; i < strList.size(); i++) {    //通过循环输出列表中的内容
	    	  String pk = strList.get(i);
	    	  if(!listMap.contains(pk)){
	    		  newPowerId += pk+",";
	    	  }
	     }
	     if(!newPowerId.equals("")){
	     String k = newPowerId.substring(0,newPowerId.length()-1);
	     String[] addData = k.split(",");
	     return addData;
	     }
	     return null;
		}
		/**
		 * 5-15添加bean转map的方法
		 * 对象转map
		 * @param obj
		 * @return
		 */
		public static Map<String, Object> beanToMap(Object obj) {
			Map<String, Object> resultMap = new HashMap<>();
			try {
				PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
				PropertyDescriptor[] des = propertyUtilsBean.getPropertyDescriptors(obj);
				for (int i = 0; i < des.length; i++) {
					String name = des[i].getName();
					try {
						if (!"class".equals(name) && null != propertyUtilsBean.getNestedProperty(obj, name)) {
							resultMap.put(name, propertyUtilsBean.getNestedProperty(obj, name));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return resultMap;
		}
		
}
