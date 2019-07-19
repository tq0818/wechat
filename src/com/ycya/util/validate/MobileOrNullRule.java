package com.ycya.util.validate;

import com.leyicat.validate.IValidateRule;

public class MobileOrNullRule implements IValidateRule {

	@Override
	public String getErrMsg(String arg0) {
		// TODO Auto-generated method stub
		return "电话号码格式不正确";
	}

	@Override
	public String getRuleKey() {
		// TODO Auto-generated method stub
		return "mobileOrNull";
	}

	@Override
	public boolean validate(Object o) {
		if(o==null||"".equals(o)){
			return true;
		}else{
			String y=String.valueOf(o);
			if(y.length()==11){
				for(int i = y.length();--i>=0;){   
					   if (!Character.isDigit(y.charAt(i))){ 
					    return false; 
					   } 
				  }
				return true;
			}
			return false;
		}
	}

}
