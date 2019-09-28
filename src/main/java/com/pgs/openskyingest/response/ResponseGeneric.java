package com.pgs.openskyingest.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ResponseGeneric {
    private boolean error;
    private String msg;
    private Object data;
}
