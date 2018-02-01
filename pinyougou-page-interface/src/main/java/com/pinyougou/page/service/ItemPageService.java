package com.pinyougou.page.service;

public interface ItemPageService {
	/**
	 * 根据SPUid生成商品页面
	 * 生成商品详细页
	 * @param goodsId
	 */
	public boolean genHtml(Long goodsId);

	/**
	 * 删除商品详细页
	 * @param goodsId
	 * @return
	 */
	public boolean deleteHtml(Long[] goodsIds);

}
