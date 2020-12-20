package cn.how2j.trend.service;

import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.utils.SpringContextUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "index-datas")
public class IndexDataService {
    private Map<String, List<IndexData>> indexDatas = new HashMap<>();

    @Autowired
    RestTemplate restTemplate;

    //默认的HystrixCommand超时时间为1s，获取数据时间长会导致直接调用fallbackMethod方法
    //如果断路调用的就是fallbackMethod声明的方法，且fresh返回的值是fallbackMethod的返回值
    @HystrixCommand(fallbackMethod = "third_part_not_connect", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    })
    public List<IndexData> fresh(String code) {
        List<IndexData> list = fetch_data(code);
        System.out.println("33:" + list.size());
        indexDatas.put(code, list);
        IndexDataService indexDataService = SpringContextUtil.getBean(IndexDataService.class);
        indexDataService.remove(code);
        return indexDataService.store(code);
    }


    public List<IndexData> fetch_data(String code) {
        System.out.println("fetch_data");
        List<Map> list = restTemplate.getForObject("http://127.0.0.1:8090/indexes/" + code + ".json", List.class);
        System.out.println("list.size:" + list.size());
        return map2IndexData(list);
    }

    @CacheEvict(key = "'index-data-'+#p0")
    public void remove(String code) {

    }

    //存储的是return返回的数据
    @Cacheable(key = "'index-data-'+#p0")
    public List<IndexData> store(String code) {
        return indexDatas.get(code);
    }

    @Cacheable(key = "'index-data-'+#p0")
    public List<IndexData> get(String code) {
        return CollUtil.toList();
    }

    public List<IndexData> third_part_not_connect(String code) {
        System.out.println("11 third_part_not_connect");
        IndexData indexData = new IndexData();
        indexData.setData("n/a");
        indexData.setClosePoint(0);
        //list.add(indexData);
        return CollectionUtil.toList(indexData);
    }

    private List<IndexData> map2IndexData(List<Map> temp) {
        List<IndexData> indexDatas = new ArrayList<>();
        for (Map map : temp) {
            String date = map.get("date").toString();
            float closePoint = Convert.toFloat(map.get("closePoint"));
            IndexData indexData = new IndexData();

            indexData.setData(date);
            indexData.setClosePoint(closePoint);
            indexDatas.add(indexData);
        }

        return indexDatas;
    }
}
