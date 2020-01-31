
package com.techbow.datadashboard.controller;

import com.techbow.datadashboard.model.dao.DataDao;
import com.techbow.datadashboard.model.dvo.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController   // @Controller + @ResponseBody
@RequestMapping("api/v1")
public class DataDashboardController {

    @Autowired // we achieve decoupling by DI
    @Qualifier("dataJpaDao")
    private DataDao dataDao;

    // @Autowired
    // private Cache cache
    
    @PostMapping("/data")
    public Data createData(@RequestBody Data data) {
        return dataDao.save(data);
    }

    @Cacheable(value = "dataCache", key = "#id")
    @GetMapping("/data/{id}")
    public Data getDataById(@PathVariable Long id) {
        System.out.println("\nCalling getDataById from DAO for data: " + id + "\n");
        return dataDao.findById(id);
    }

    // 返回所有种类的数据给用户,你自己可以限定返回前多少行数据
    @GetMapping("/data")
    public List<Data> getAllData(@RequestParam(name = "limit", required = false) String limit) {
        // 把这个命令转发给model层，其实是在model层实现的
        return dataDao.findAll(limit);
    }

    // 例如微信朋友圈步数排列，然后取步数排名在一定range之间
    // 按照clinetID去找
    // localhost: 8080/api/v1/data/client/666?field=step_count&sort=desc||start==7&&end==10
    @GetMapping("/data/client/{clientId}")
    public List<Data> findDataByClientId(  //这个功能叫 findDataByClientId
            @PathVariable Long clientId,
            @RequestParam(name = "field", required = false) String field,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end
    ) {
        return dataDao.findByClientId(clientId, field, sort, start, end);
    }


    @CachePut(value = "dataCache", key = "#id")
    @PutMapping("/data/{id}")
    public Data updateData(@PathVariable Long id, @RequestBody Data data) {
        return dataDao.update(id, data);
    }
}
