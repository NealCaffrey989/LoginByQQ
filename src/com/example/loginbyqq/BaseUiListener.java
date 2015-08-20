package com.example.loginbyqq;

import android.content.Context;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
/**
 * @author monster
 * @category BaseUiListener 进行信息的回调
 */
public class BaseUiListener implements IUiListener{
		
	@SuppressWarnings("unused")
	private Context mContext;
	@SuppressWarnings("unused")
	private String mScope;
	

	
	public BaseUiListener(){
		super();
	}
	
	public BaseUiListener(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	public BaseUiListener(Context mContext, String mScope) {
		super();
		this.mContext = mContext;
		this.mScope = mScope;
	}
	
	@Override
	public void onCancel() {
		//Log.e("TAG-->Cancel", "Cancel");
	}
	@Override
	public void onComplete(Object object) {
		//Log.e("TAG-->Complete", ""+object);	

	}
	@Override
	public void onError(UiError error) {
		//Log.e("TAG-->Error", ""+error);
	}
	
	
}
