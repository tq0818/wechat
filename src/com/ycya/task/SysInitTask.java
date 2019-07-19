package com.ycya.task;

import com.leyicat.task.ITask;
import com.ycya.store.task.VspTask;
import com.ycya.weixin.util.MenuUtil;
import com.ycya.weixin.util.WeiXinUtil;

public class SysInitTask implements ITask {
	@Override
	public void startTask() {

		//创建菜单
		WeiXinUtil weiXinUtil = new WeiXinUtil();
		String accessToken = weiXinUtil.getAccessTokenRedis();
        String menu = MenuUtil.initMenu();
        int  res = MenuUtil.createMenu(accessToken,menu);
        if (res == 0){
        	System.out.println("菜单创建成功");
        }else{
        	System.out.println("菜单创建失败");
        }
		
	}

	@Override
	public void stopTask() {

	}

}
