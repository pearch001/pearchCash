package com.pearchCash.payments.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String code;
    private String message;
    private GenericData<Object> data;

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
