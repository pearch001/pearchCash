package com.pearchCash.payments.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private String status;
    private String message;
    private GenericData<Object> data;

    public Response(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
