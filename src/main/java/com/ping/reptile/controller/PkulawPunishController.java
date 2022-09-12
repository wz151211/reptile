package com.ping.reptile.controller;

import com.ping.reptile.model.vo.Result;
import com.ping.reptile.service.PkulawService;
import com.ping.reptile.service.PunishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: W.Z
 * @Date: 2022/8/27 17:37
 */
@RestController
@RequestMapping("/pkulaw")
@Slf4j
public class PkulawPunishController {

    @Autowired
    private PkulawService pkulawService;


    @GetMapping("page")
    public Result page(Integer pageNum, Integer pageSize) {

        pkulawService.page(pageNum, pageSize);
        Result result = new Result();
        result.setSuccess(true);
        return result;
    }

}
