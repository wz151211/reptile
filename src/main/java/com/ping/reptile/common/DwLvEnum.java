package com.ping.reptile.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public enum DwLvEnum {

    高检(1), 省检(2), 市检(3), 区检(4);

    private final int value;
}
