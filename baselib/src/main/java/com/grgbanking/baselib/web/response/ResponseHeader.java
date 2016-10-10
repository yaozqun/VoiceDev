package com.grgbanking.baselib.web.response;

public class ResponseHeader {
	/**
	 * 成功\失败标识 0000--成功 9999--系统错误 8888--业务错误
	 */
	public String rspCode;
	/**
	 * 错误描述
	 */
	public String rspDesc;
	/**
	 * 业务流水号
	 */
	public String serialNo;

}
