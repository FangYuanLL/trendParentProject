package cn.how2j.trend.controller;


import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.service.BackTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BackTestController {

    @Autowired
    BackTestService backTestService;

    @GetMapping("/simulate/{code}")
    public Map<String,Object> getIndexData(@PathVariable("code") String code){
        List<IndexData> indexDataList = backTestService.getIndexData(code);
        Map<String,Object> result = new HashMap<>();
        result.put("indexDatas", indexDataList);
        return result;
    }

}
