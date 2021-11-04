package com.hith.hithium.upload.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page <T>{
    //总页数
    private Long totalPage;

    //总记录数
    private Long totalCount;

    //每页显示集合
    private List<T> rows;

}
