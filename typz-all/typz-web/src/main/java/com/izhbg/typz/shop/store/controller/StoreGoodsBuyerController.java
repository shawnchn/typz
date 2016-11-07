package com.izhbg.typz.shop.store.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.izhbg.typz.base.common.datatable.dto.DataVO;
import com.izhbg.typz.base.page.Page;
import com.izhbg.typz.base.util.Ajax;
import com.izhbg.typz.base.util.Constants;
import com.izhbg.typz.shop.goods.dto.TShGoodsBasic;
import com.izhbg.typz.shop.goods.service.TShGoodsBasicService;
import com.izhbg.typz.shop.store.service.TShStoreGoodsBuyerService;
import com.izhbg.typz.shop.store.service.TShStoreService;
import com.mysql.jdbc.Messages;

import net.sf.json.JSONArray;

@Controller
@RequestMapping("/store")
public class StoreGoodsBuyerController {
	@Autowired
	private TShStoreGoodsBuyerService tShStoreGoodsBuyerService;
	@Autowired
	private TShGoodsBasicService tShGoodsBasicService;
	@Autowired
	private TShStoreService tShStoreService;
	/**
	 * 店铺产品列表
	 * @param page
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("goods-buyer-list")
	 public String list(@ModelAttribute  Page page,Model model,String storeId) throws Exception{
		page = tShStoreGoodsBuyerService.pageList(page,storeId);
		model.addAttribute("page", page);
		model.addAttribute("store", tShStoreService.getById(storeId));
		return "shop/store/store-buyer-list";
	}
	/**
	 * 添加店铺销售产品
	 * @param storeId
	 * @param checkdel
	 * @return
	 */
	@RequestMapping(value="goods-buyer-add",method=RequestMethod.POST)
	public @ResponseBody  String addSaleGoods(String storeId,String[] checkdel ){
		String result=null;
		try {
			tShStoreGoodsBuyerService.addBuyerGoods(storeId, checkdel);
			result = Ajax.JSONResult(Constants.RESULT_CODE_SUCCESS,Constants.SYSTEMMSG_SUCCESS);
		} catch (Exception e) {
			result = Ajax.JSONResult(Constants.RESULT_CODE_FAILED,Constants.SYSTEMMSG_FAILED);
		}
		return result;
	}
	/**
	 * 删除店铺销售产品
	 * @param checkdel
	 * @return
	 */
	@RequestMapping(value="goods-buyer-delete",method=RequestMethod.POST)
	public @ResponseBody  String delGoodsSale(String[] checkdel){
		String result=null;
		if(checkdel == null || checkdel.length < 1){
			result = Ajax.JSONResult(Constants.RESULT_CODE_ERROR, Messages.getString("systemMsg.fieldEmpty"));	
		}
		try {
			tShStoreGoodsBuyerService.deleteBatche(checkdel);
			result = Ajax.JSONResult(Constants.RESULT_CODE_SUCCESS,Constants.SYSTEMMSG_SUCCESS);
		} catch (Exception e) {
			result = Ajax.JSONResult(Constants.RESULT_CODE_FAILED,Constants.SYSTEMMSG_FAILED);
		}
		return result;
	}
	
	/**
	 * 
	 * @param iDisplayLength
	 * @param iDisplayStart
	 * @param sSearch
	 * @param sEcho
	 * @return
	 */
	@RequestMapping(value="goods-buyer-goodsList",method=RequestMethod.GET)
	public @ResponseBody  String goodsList(@RequestParam(value="iDisplayLength",required=true,defaultValue="0") int iDisplayLength,
										   @RequestParam(value="iDisplayStart",required=true,defaultValue="0") int iDisplayStart,
										   @RequestParam(value="sSearch",required=false) String sSearch,
										   @RequestParam(value="sEcho",required=true,defaultValue="0") int sEcho){
		String result_e=null;
		Page page = new Page();
		page.setPageSize(iDisplayLength);
		if(iDisplayStart==0)
			page.setPageNo(1);
		else
			page.setPageNo(iDisplayStart/iDisplayLength+1);
		try {
			page = tShGoodsBasicService.pageList(page);
			List<TShGoodsBasic> tShGoodsBasics = (List<TShGoodsBasic>) page.getResult();
			DataVO<TShGoodsBasic> result = new DataVO<>();
			result.setsEcho(sEcho+1);
			result.setData(tShGoodsBasics);
			result.setRecordsTotal(Integer.parseInt(page.getTotalCount()+""));
			result.setRecordsFiltered(Integer.parseInt(page.getTotalCount()+""));
			result.setiDisplayStart(iDisplayStart);
			result_e = JSONArray.fromObject(result).toString();
			result_e = result_e.substring(1,result_e.length()-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result_e;
	}
}