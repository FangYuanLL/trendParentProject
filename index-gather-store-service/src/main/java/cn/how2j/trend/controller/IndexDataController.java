package cn.how2j.trend.controller;

import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import cn.how2j.trend.pojo.IndexData;
import cn.how2j.trend.service.IndexDataService;

@RestController
public class IndexDataController {
    @Autowired
    IndexDataService indexDataService;

//  http://127.0.0.1:8001/freshIndexData/000300
//  http://127.0.0.1:8001/getIndexData/000300
//  http://127.0.0.1:8001/removeIndexData/000300

    @GetMapping("/freshIndexData/{code}")
    public String fresh(@PathVariable("code") String code) throws Exception {
        List<IndexData> list = indexDataService.fresh(code);
        return (list.get(0).getData() + " " + list.get(0).getClosePoint());
    }

    @GetMapping("/getIndexData/{code}")
    public List<IndexData> get(@PathVariable("code") String code) throws Exception {
        return indexDataService.get(code);
    }

    @GetMapping("/removeIndexData/{code}")
    public String remove(@PathVariable("code") String code) throws Exception {
        indexDataService.remove(code);
        return "remove index data successfully";
    }
}