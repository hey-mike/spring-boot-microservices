package com.mluo.microservices.core.product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mluo.api.core.product.Product;
import com.mluo.microservices.core.product.persistance.ProductEntity;
import com.mluo.microservices.core.product.serivces.ProductMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

public class MapperTests {
    private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Product api = new Product(123, "n", 1, "sa");

        ProductEntity entity = mapper.apiToEntity(api);


        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getWeight(), entity.getWeight());

        Product api2 = mapper.entityToApi(entity);
        printObject(api);
        printObject(entity);
        printObject(api2);


        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getName(), api2.getName());
        assertEquals(api.getWeight(), api2.getWeight());
        assertNull(api2.getServiceAddress());
    }

    public void printObject(Object object) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(object));
    }
}
