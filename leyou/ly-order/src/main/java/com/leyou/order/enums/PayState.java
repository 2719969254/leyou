package com.leyou.order.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum PayState {
    SUCCESS(1),
    FAIL(2),
    NOT_PAY(3)
    ;
    private Integer stateCode;

    public Integer intValue(){
        return this.stateCode;
    }
}
