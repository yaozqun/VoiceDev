package com.grgbanking.pdamodel.uhf;
/**
 * 
 * 版权所有：2015-GRGBANKING
 * 项目名称：PDAModel   
 *
 * 类描述：
 * 类名称：com.grgbanking.pdamodel.uhf.BlockConstant     
 * 创建人：G0212269
 * 创建时间：2015-8-24 下午2:53:19   
 * 修改人：
 * 修改时间：2015-8-24 下午2:53:19   
 * 修改备注：   
 * @version   V1.0
 */
public class BlockConstant {
	/**
	 * epc的大小为6个字,业务中用多长的数据要自行截取
	 */
	public static final int SIZE_EPC = 6;
	/**
	 * reserve的大小为4个字
	 */
	public static final int SIZE_RESERVE = 4;
	/**
	 * tid的大小为6个字(12字节)
	 */
	public static final int SIZE_TID = 6;
	/**
	 * user的大小为32个字
	 */
	public static final int SIZE_USER = 32;

	// 各区块的编号
	public static final int RESERVE_NO = 0;
	public static final int EPC_NO = 1;
	public static final int TID_NO = 2;
	public static final int USER_NO = 3;

	// 各区块的密钥
	public static final byte[] PASSWORD_RESERVE = new byte[] { 0x00, 0x00,
			0x00, 0x00 };
	public static final byte[] PASSWORD_EPC = new byte[] { 0x00, 0x00, 0x00,
			0x00 };
	public static final byte[] PASSWORD_TID = new byte[] { 0x00, 0x00, 0x00,
			0x00 };
	public static final byte[] PASSWORD_USER = new byte[] { 0x00, 0x00, 0x00,
			0x00 };
}
