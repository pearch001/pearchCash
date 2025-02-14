package com.pearchCash.payments.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GenericData<T> {
    T obj;
    Integer totalCount;
    public GenericData(T obj){
        this.obj = obj;
    }
}
