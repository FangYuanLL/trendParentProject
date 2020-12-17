package cn.how2j.trend.client;

import cn.how2j.trend.pojo.IndexData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


//@FeignClient注解：value和名为value的服务建立连接
@FeignClient(value = "index-data-service",fallback = IndexDataClientFeignHystrix.class)
public interface IndexDataClient {
    @GetMapping("/data/{code}") //映射方式：通过路径(/data/{code})来访问index-data-service的get方法获取数据
    public List<IndexData> getIndexData(@PathVariable("code") String code);
}
