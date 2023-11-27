package com.ping.reptile;

import com.alibaba.fastjson.JSON;
import com.ping.reptile.mapper.CauseMapper;
import com.ping.reptile.model.entity.CauseEntity;
import com.ping.reptile.model.vo.Dict;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class TestDict {

    @Autowired
    private CauseMapper causeMapper;

    @Test
    private void test() {
        List<Dict> causes = new ArrayList<>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:doc/*.txt");
            for (Resource resource : resources) {
                if ("cause.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    causes.addAll(JSON.parseArray(text, Dict.class));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Dict cause : causes) {
            CauseEntity entity = new CauseEntity();
            entity.setId(cause.getId().toString());
            entity.setPid(cause.getParent());
            entity.setName(cause.getText());
            causeMapper.insert(entity);
        }
    }
}
