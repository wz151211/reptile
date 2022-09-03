package com.ping.reptile.service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/8/27 16:14
 */
public class TestService {

    public int get(int pageNum, int pageSize) {

        List<String> list1 = new ArrayList<>(5000);
        List<String> list2 = new ArrayList<>(5000);
        List<String> list3 = new ArrayList<>(5000);
        List<String> list4 = new ArrayList<>(5000);


        System.out.println("pageNum=" + pageNum + "  pageSize=" + pageSize);

        return get(pageNum + 1, pageSize);
    }
}
