package com.ycya.util.validate;

import java.util.HashMap;
import java.util.Map;

import com.leyicat.validate.IValidateProvider;
import com.leyicat.validate.IValidateRule;
import com.leyicat.validate.rule.DateRule;
import com.leyicat.validate.rule.LengthRule;
import com.leyicat.validate.rule.NullRule;
import com.leyicat.validate.rule.NumRule;

public class ValidateProvider implements IValidateProvider {
	private static Map<String,IValidateRule[]> ruleMap = new HashMap<>();
	@Override
	public IValidateRule[] getRule(String key) {
		return ruleMap.get(key);
	}
	static{
    	ruleMap.put("null", new IValidateRule[]{new AllowNullRule()});
    	ruleMap.put("notNull", new IValidateRule[]{new NullRule()});
    	ruleMap.put("num", new IValidateRule[]{new NumRule("1",new Integer(Integer.MAX_VALUE).toString())});
    	ruleMap.put("nullOrDate", new IValidateRule[]{new NullOrDateRule("yyyy-MM-dd HH:mm:ss")});
    	ruleMap.put("id", new IValidateRule[]{new LengthRule("1", "20")});
    	ruleMap.put("is", new IValidateRule[]{new NumRule("0", "1")});
    	ruleMap.put("nullOrId", new IValidateRule[]{new NullOrId()});
    	//old
        ruleMap.put("count", new IValidateRule[]{new NumRule("0", "16")});
        ruleMap.put("data", new IValidateRule[]{new NullRule()});
        ruleMap.put("haveData", new IValidateRule[]{new LengthRule("1", "16")});
        ruleMap.put("pwd", new IValidateRule[]{new LengthRule("1", "32")});
        
        ruleMap.put("bigNumData", new IValidateRule[]{new NumRule("1", "10000")});
        ruleMap.put("bigData", new IValidateRule[]{new LengthRule("1", "1000")});
        ruleMap.put("Car.vin", new IValidateRule[]{new LengthRule("4", "20")});
        ruleMap.put("Car.vehicleNo", new IValidateRule[]{new LengthRule("7", "16")});
        ruleMap.put("Term.termNo", new IValidateRule[]{new LengthRule("7", "16")});
        ruleMap.put("Term.termPro", new IValidateRule[]{new LengthRule("2", "16")});
        ruleMap.put("Term.simNo", new IValidateRule[]{new LengthRule("7", "16")});
             
        ruleMap.put("Org.contactPer", new IValidateRule[]{new LengthRule("1", "32")});
        ruleMap.put("Date.short", new IValidateRule[]{new DateRule("yyyy-MM-dd")});
        ruleMap.put("Date.long", new IValidateRule[]{new DateRule("yyyy-MM-dd HH:mm:ss")});
        ruleMap.put("mobileOrNull", new IValidateRule[]{new MobileOrNullRule()});//空或者电话号码格式
    }
}
