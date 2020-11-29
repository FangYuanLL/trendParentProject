package cn.how2j.trend.service;

import cn.how2j.trend.client.IndexDataClient;
import cn.how2j.trend.pojo.IndexData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BackTestService {

    @Autowired
    IndexDataClient indexDataClient;

    public List<IndexData> getIndexData(String code){
        List<IndexData> indexDataList = indexDataClient.getIndexData(code);

        Collections.reverse(indexDataList);

        for (IndexData indexData : indexDataList) {
            System.out.println(indexData.getData());
        }

        return indexDataList;
    }

}
