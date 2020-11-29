package cn.how2j.trend.service;

import cn.how2j.trend.pojo.IndexData;
import cn.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
/*
 * 获取redis数据
 */
@Service
@CacheConfig(cacheNames = "index-datas")
public class IndexDataService {

    @Cacheable(key = "'index-data-'+#p0")
    public List<IndexData> get(String code){
        return CollUtil.toList();
    }

}
