package com.pinyougou.search.service.impl;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
	
	@Autowired
	private SolrTemplate solrTemplate;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String, Object> map = new HashMap<>();
		
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));

		
		//1.关键字搜索
		Map<String, Object> mapList = searchList(searchMap);
		//2.查询分类条件
		List<String> categoryList = findCategoryList(searchMap);
		map.put("categoryList", categoryList);
		//3.查询品牌和规格
		if(!"".equals(searchMap.get("category"))){//选择了分类
			Map<String, Object> brandAndSpecMap = findBrandAndSpecList((String)searchMap.get("category"));
			map.putAll(brandAndSpecMap);
		}else{
			if(categoryList!=null&&categoryList.size()>0){
				Map<String, Object> brandAndSpecMap = findBrandAndSpecList(categoryList.get(0));
				map.putAll(brandAndSpecMap);
			}
		}
		
		
		
		map.putAll(mapList);
		
		return map;
	}
	
	
	
	
	/**
	 * 根据分类名称查询模板id，根据模板id查询品牌和规格数据
	 * @param category
	 * @return
	 */
	private Map<String, Object> findBrandAndSpecList(String category){
		Map<String, Object> map= new HashMap<>();
		Long typeId = (Long) redisTemplate.boundHashOps("categoryList").get(category);//从缓存中获取模板id
		List<Map> brandList=(List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
		List<Map> specList=(List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
		
		map.put("brandList", brandList);
		map.put("specList", specList);
		
		return map;
	}
	/**
	 * 根据关键字分组查询分类列表
	 * @param searchMap
	 * @return
	 */
	private List<String> findCategoryList(Map<String, Object> searchMap){
		List<String> categoryList=new ArrayList<>();
		
		Query query =new SimpleQuery();
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		//设置分组字段 分类
		GroupOptions groupOptions =new GroupOptions();
		groupOptions.addGroupByField("item_category");
		query.setGroupOptions(groupOptions );
		//返回分组页
		GroupPage<TbItem> queryForGroupPage = solrTemplate.queryForGroupPage(query , TbItem.class);
		//返回分组结果集
		GroupResult<TbItem> groupResult = queryForGroupPage.getGroupResult("item_category");
		//返回分组入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//分组入口集合
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for(GroupEntry<TbItem> groupEntry:content){
			categoryList.add(groupEntry.getGroupValue());
			System.out.println(groupEntry.getGroupValue());
		}
		return categoryList;
	}
	
	
	
	/**
	 * 根据关键字搜索列表
	 * @param keywords
	 * @return
	 */
	private Map<String , Object> searchList(Map<String, Object> searchMap) {
		Map<String, Object> map = new HashMap<>();
		HighlightQuery query = new SimpleHighlightQuery();
		//高亮设置
		
		HighlightOptions highlightOptions = new HighlightOptions();
		highlightOptions.addField("item_title");//对title字段进行高亮显示
		highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
		highlightOptions.setSimplePostfix("</em>");//高亮后缀
		query.setHighlightOptions(highlightOptions );
		
		//1.1根据关键字进行条件查询
		Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria );
		//1.2按分类筛选
		if(!"".equals(searchMap.get("category"))){			
			Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//1.3按品牌筛选
		if(!"".equals(searchMap.get("brand"))){			
			Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//1.4过滤规格
		if(searchMap.get("spec")!=null){
				Map<String,String> specMap= (Map) searchMap.get("spec");
				for(String key:specMap.keySet() ){
					Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
					FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
					query.addFilterQuery(filterQuery);				
				}			
		}
		//1.5根据价格进行过滤
		if (!"".equals(searchMap.get("price"))) {
			String[] price = ((String)searchMap.get("price")).split("-",-1);
			if (!"0".equals(price[0])) {//如果最小值不为0，那么考虑最小值
				FilterQuery filterQuery= new SimpleFilterQuery();
				Criteria filterCriteria =new Criteria("item_price").greaterThanEqual(price[0]);
				filterQuery.addCriteria(filterCriteria );
				query.addFilterQuery(filterQuery);
			}
			if (!"*".equals(price[1])) {//如果最大值不为*，那么考虑最大值
				FilterQuery filterQuery= new SimpleFilterQuery();
				Criteria filterCriteria =new Criteria("item_price").lessThanEqual(price[1]);
				filterQuery.addCriteria(filterCriteria );
				query.addFilterQuery(filterQuery);
			}
			
		}
		//1.6 分页查询
		Integer pageNo = (Integer) searchMap.get("pageNo");
		if(pageNo==null){
			pageNo=1;//默认第一页
		}
		Integer pageSize=(Integer) searchMap.get("pageSize");//每页记录数 
		if(pageSize==null){
			pageSize=20;//默认20  默认是10
		}
		query.setOffset((pageNo-1)*pageSize);//从第几条记录查询
		query.setRows(pageSize);	
		//1.7排序查询
		String sortValue = (String) searchMap.get("sort");//排序方式 ASC DESC
		String sortField = (String) searchMap.get("sortField");//排序的字段
		if(sortValue!=null&&!"".equals(sortValue)){
			if("ASC".equals(sortValue)){
				Sort sort=new Sort(Direction.ASC, "item_"+sortField);
				query.addSort(sort);
			}
			if("DESC".equals(sortValue)){
				Sort sort=new Sort(Direction.DESC, "item_"+sortField);
				query.addSort(sort);
			}
		}

		
		
		
		
		//高亮页
		HighlightPage<TbItem> queryForHighlightPage = solrTemplate.queryForHighlightPage(query , TbItem.class);
		//获取高亮入口集合
		List<HighlightEntry<TbItem>> list = queryForHighlightPage.getHighlighted();
		for (HighlightEntry<TbItem> highlightEntry : list) {
			TbItem item = highlightEntry.getEntity();//获取原实体类	得到没有高亮过的对象
			if (highlightEntry.getHighlights().size() > 0 && highlightEntry.getHighlights().get(0).getSnipplets().size()>0) {
				List<Highlight> item2 = highlightEntry.getHighlights();//得到高亮的集合
				String highString = item2.get(0).getSnipplets().get(0);
				
				item.setTitle(highString);//获取高亮内容  设置到title字段上
			}
			
			
		}
		
		List<TbItem> content = queryForHighlightPage.getContent();
		map.put("rows", content);
		long total = queryForHighlightPage.getTotalElements();//总记录数
		map.put("total", total);
		int totalPages = queryForHighlightPage.getTotalPages();//总页数
		map.put("totalPages", totalPages);
		return map;
		
	}




	@Override
	public void importList(List<TbItem> list) {
		solrTemplate.saveBeans(list);	
		solrTemplate.commit();

		
	}




	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		System.out.println("删除商品ID"+goodsIdList);
		Query query=new SimpleQuery();		
		Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();

		
	}

	
	

}
