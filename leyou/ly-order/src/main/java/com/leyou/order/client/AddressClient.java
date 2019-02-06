package com.leyou.order.client;

import com.leyou.order.dto.AddressDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class AddressClient {
    public static final List<AddressDTO> addressList = new ArrayList<AddressDTO>(){
        {
            AddressDTO address = new AddressDTO();
            address.setId(1L);
            address.setAddress("航头镇航头路18号传智播客 3号楼");
            address.setCity("上海");
            address.setDistrict("浦东新区");
            address.setIsDefault(true);
            address.setName("虎哥");
            address.setPhone("15800000000");
            address.setState("上海");
            address.setZipCode("210000");
            add(address);
        }
    };

    public static AddressDTO findById(Long id){
        for (AddressDTO addressDTO : addressList) {
            if(addressDTO.getId() == id) return addressDTO;
        }
        return null;
    }
}
