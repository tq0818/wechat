package com.ycya.util.validate;

import com.leyicat.validate.IValidateRule;
import com.leyicat.validate.rule.LengthRule;

public class NullOrId implements IValidateRule {
	
	@Override
	public boolean validate(Object value) {
		return null==value || new LengthRule("1","20").validate(value);
	}

	@Override
	public String getRuleKey() {
		return "nullOrId";
	}

	@Override
	public String getErrMsg(String key) {
		return "只能为null或长度为18-20的数字";
	}

}
