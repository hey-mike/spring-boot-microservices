package com.mluo.microservices.core.product;

import com.mluo.api.core.product.Product;
import com.mluo.microservices.core.product.persistance.ProductEntity;
import com.mluo.microservices.core.product.serivces.ProductMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.*;

public class MapperTests {
    private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Product api = new Product(1, "n", 1, "sa");

        ProductEntity entity = mapper.apiToEntity(api);
        System.out.println(entity);

        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getWeight(), entity.getWeight());

        Product api2 = mapper.entityToApi(entity);

        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getName(), api2.getName());
        assertEquals(api.getWeight(), api2.getWeight());
        assertNull(api2.getServiceAddress());
    }
}
