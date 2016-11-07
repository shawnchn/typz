package com.izhbg.typz.shop.store.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.izhbg.typz.base.common.service.ServiceException;
import com.izhbg.typz.base.mapper.BeanMapper;
import com.izhbg.typz.base.page.Page;
import com.izhbg.typz.base.util.IdGenerator;
import com.izhbg.typz.base.util.StringHelper;
import com.izhbg.typz.shop.goods.dto.TShGoodsBasic;
import com.izhbg.typz.shop.goods.dto.TShGoodsImage;
import com.izhbg.typz.shop.goods.manager.TShGoodsBasicManager;
import com.izhbg.typz.shop.goods.service.TShGoodsImageService;
import com.izhbg.typz.shop.store.dto.TShStoreGoodsSale;
import com.izhbg.typz.shop.store.manager.TShStoreGoodsSaleManager;
import com.izhbg.typz.shop.store.service.TShStoreGoodsSaleService;

@Service("tShStoreGoodsSaleService")
public class TShStoreGoodsSaleServiceImpl implements TShStoreGoodsSaleService {

	@Autowired
	private TShStoreGoodsSaleManager tShStoreGoodsManager;
	@Autowired
	private TShGoodsBasicManager tShGoodsBasicManager;
	private BeanMapper beanMapper = new BeanMapper();
	@Autowired
	private TShGoodsImageService tShGoodsImageService;
	
	@Override
	public void add(TShStoreGoodsSale entity) throws Exception {
		if(entity==null)
			throw new ServiceException("参数为空，添加店铺产品失败");
		tShStoreGoodsManager.save(entity);
	}

	@Override
	public void update(TShStoreGoodsSale entity) throws Exception {
		if(entity==null||StringHelper.isEmpty(entity.getId()))
			throw new ServiceException("参数为空,更新店铺产品失败");
		TShStoreGoodsSale tsg = tShStoreGoodsManager.findUniqueBy("id", entity.getId());
		beanMapper.copy(entity, tsg);
		tShStoreGoodsManager.update(tsg);
	}

	@Override
	public void delete(String id) throws Exception {
		if(StringHelper.isEmpty(id))
			throw new ServiceException("参数为空，删除店铺产品失败");
		tShStoreGoodsManager.removeById(id);
	}

	@Override
	public void deleteBatche(String[] ids) throws Exception {
		if(ids==null||ids.length<=0)
			throw new ServiceException("参数为空，批量删除店铺产品失败");
		for(String id:ids)
			this.delete(id);
	}

	@Override
	public TShStoreGoodsSale getById(String id) throws Exception {
		if(StringHelper.isEmpty(id))
			throw new ServiceException("参数为空,获取店铺产品失败");
		return tShStoreGoodsManager.findUniqueBy("id", id);
	}

	@Override
	public Page pageList(Page page) throws Exception {
		Map<String, Object> map = new HashMap<>();
		page = tShStoreGoodsManager.pagedQuery(" from TShStoreGoodsSale ", page.getPageNo(), page.getPageSize(), map);
		List<TShStoreGoodsSale> list = (List<TShStoreGoodsSale>) page.getResult();
		if(list!=null){
			TShGoodsBasic tsgb = null;
			for(TShStoreGoodsSale tsgs:list){
				tsgb = tShGoodsBasicManager.findUniqueBy("id", tsgs.getGoodsId());
				if(tsgb!=null)
					tsgs.settShGoodsBasic(tsgb);
			}
		}
		page.setResult(list);	
		return page;
	}

	@Override
	public List<TShStoreGoodsSale> getAll() throws Exception {
		return tShStoreGoodsManager.getAll();
	}

	@Override
	public void addSaleGoods(String storeId, String[] checkdel) throws Exception {
		if(StringHelper.isEmpty(storeId)||checkdel==null)
			throw new ServiceException("参数为空,添加销售商品失败");
		Map<String, Object> map = new HashMap<>();
		TShStoreGoodsSale tsgs = null;
		for(String goodsId:checkdel){
			map.clear();
			map.put("storeId", storeId);
			map.put("goodsId", goodsId);
			tsgs = tShStoreGoodsManager.findUnique(" from TShStoreGoodsSale where goodsId=:goodsId and storeId=:storeId", map);
			if(tsgs==null){
				tsgs = new TShStoreGoodsSale();
				tsgs.setGoodsId(goodsId);
				tsgs.setStoreId(storeId);
				tsgs.setStoreId(storeId);
				tsgs.setId(IdGenerator.getInstance().getUniqTime()+"");
				tShStoreGoodsManager.save(tsgs);
			}
		}
	}

	@Override
	public Page pageList(Page page, String storeId) throws Exception {
		Map<String, Object> map = new HashMap<>();
		map.put("storeId", storeId);
		page = tShStoreGoodsManager.pagedQuery(" from TShStoreGoodsSale where storeId=:storeId", page.getPageNo(), page.getPageSize(), map);
		List<TShStoreGoodsSale> list = (List<TShStoreGoodsSale>) page.getResult();
		if(list!=null){
			TShGoodsBasic tsgb = null;
			TShGoodsImage tShGoodsImage = null;
			for(TShStoreGoodsSale tsgs:list){
				tsgb = tShGoodsBasicManager.findUniqueBy("id", tsgs.getGoodsId());
				if(tsgb!=null){
					tShGoodsImage = tShGoodsImageService.getIndexImage(tsgb.getId(), tsgb.getVersion());
					 if(tShGoodsImage!=null){
						 tsgb.setIndexImage(tShGoodsImage);
					 }
					
					tsgs.settShGoodsBasic(tsgb);
				}
			}
		}
		page.setResult(list);	
		return page;
	}

}