package com.ping.reptile.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

@Service
public class TextWordService {

    public void export(){
        XWPFDocument document = new XWPFDocument();
    }
}
