package com.ycya.util.validate;

import com.leyicat.validate.IValidateRule;

public class AllowNullRule  implements IValidateRule {

	@Override
	public String getErrMsg(String arg0) {
		return "";
	}

	@Override
	public String getRuleKey() {
		return "allowNull";
	}

	@Override
	public boolean validate(Object arg0) {
		return true;
	}
}
