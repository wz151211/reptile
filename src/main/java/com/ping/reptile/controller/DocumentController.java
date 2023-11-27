package com.ping.reptile.controller;

import com.ping.reptile.model.vo.Result;
import com.ping.reptile.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 23:15
 */
@RestController
@RequestMapping("/doc")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("page")
    public Result page(Integer pageNum, Integer pageSize) {
        documentService.page(pageNum, pageSize);
        Result result = new Result();
        result.setSuccess(true);
        return result;
    }

}
