package com.gfg.product.exception;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ErrorMessage {

    private int statusCode;
    private Date date;
    private String message;
    private String path;

}
