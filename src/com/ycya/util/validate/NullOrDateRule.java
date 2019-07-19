package com.ycya.util.validate;

import com.leyicat.validate.IValidateRule;
import com.leyicat.validate.rule.DateRule;

public class NullOrDateRule implements IValidateRule {
	private String fmt;

	public NullOrDateRule(){
		this.fmt = "yyyy-MM-dd HH:mm:ss";
	}
	
	public NullOrDateRule(String fmt){
		this.fmt = fmt;
	}
	
	@Override
	public boolean validate(Object value) {
		return null==value || new DateRule(this.fmt).validate(value);
	}

	@Override
	public String getRuleKey() {
		return "nullOrDate";
	}

	@Override
	public String getErrMsg(String key) {
		return "只能为null或指定格式的日期";
	}

}
