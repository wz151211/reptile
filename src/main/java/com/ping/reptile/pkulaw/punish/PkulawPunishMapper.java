package com.ping.reptile.pkulaw.punish;

import com.ping.reptile.pkulaw.punish.vo.PunishEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class PkulawPunishMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(PunishEntity entity) {
        mongoTemplate.save(entity);
    }

    public long getCountById(String id) {
        Query query = new Query();
        Criteria criteria = Criteria.where("id").is(id);
        query.addCriteria(criteria);
        return mongoTemplate.count(query, PunishEntity.class);
    }

}
