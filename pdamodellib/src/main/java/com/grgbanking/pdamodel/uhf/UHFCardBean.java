package com.grgbanking.pdamodel.uhf;

/**
 * 
 * 版权所有：2015-GRGBANKING
 * 项目名称：PDAModel   
 *
 * 类描述：
 * 类名称：com.grgbanking.pdamodel.uhf.UHFCardBean     
 * 创建人：G0212269
 * 创建时间：2015-8-24 下午2:53:08   
 * 修改人：
 * 修改时间：2015-8-24 下午2:53:08   
 * 修改备注：   
 * @version   V1.0
 */
public class UHFCardBean {
	private byte[] epcArr;
	private byte[] reserveArr;
	private byte[] tidArr;
	private byte[] userArr;

	public byte[] getEpcArr() {
		return epcArr;
	}

	public void setEpcArr(byte[] epcArr) {
		this.epcArr = epcArr;
	}

	public byte[] getReserveArr() {
		return reserveArr;
	}

	public void setReserveArr(byte[] reserveArr) {
		this.reserveArr = reserveArr;
	}

	public byte[] getTidArr() {
		return tidArr;
	}

	public void setTidArr(byte[] tidArr) {
		this.tidArr = tidArr;
	}

	public byte[] getUserArr() {
		return userArr;
	}

	public void setUserArr(byte[] userArr) {
		this.userArr = userArr;
	}

}
