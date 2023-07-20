package com.ping.reptile.pkulaw.punish;

import com.ping.reptile.pkulaw.punish.vo.AreaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaTempService {
    @Autowired
    private AreaTempMapper areaMapper;

    public AreaEntity find(String city, String county) {
        return areaMapper.find(city, county);
    }
}
