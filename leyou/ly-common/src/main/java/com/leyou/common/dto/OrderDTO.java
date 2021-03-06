package com.leyou.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

//   data transfer object
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    @NotNull
    private Long addressId;

    @NotNull
    private Integer paymentType;

    @NotNull
    private List<CartDTO> carts;
}
