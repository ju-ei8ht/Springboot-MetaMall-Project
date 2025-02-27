package shop.mtcoding.metamall.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import shop.mtcoding.metamall.core.jwt.JwtProvider;
import shop.mtcoding.metamall.dto.ResponseDto;
import shop.mtcoding.metamall.dto.order.OrderRequest;
import shop.mtcoding.metamall.model.user.User;
import shop.mtcoding.metamall.model.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    private UserRepository userRepository;

    private HttpHeaders headers(User user){
        String jwt = JwtProvider.create(user);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        requestHeaders.add(JwtProvider.HEADER,jwt);

        return requestHeaders;
    }

    @Test
    @DirtiesContext
    @DisplayName("주문")
    void order() throws JsonProcessingException {
        //given
        OrderRequest.OrderProductDto product1 = OrderRequest.OrderProductDto.builder()
                .productId(1L)
                .count(1)
                .build();
        OrderRequest.OrderProductDto product2 = OrderRequest.OrderProductDto.builder()
                .productId(2L)
                .count(1)
                .build();
        List<OrderRequest.OrderProductDto> products = Stream.of(product1,product2).collect(Collectors.toList());
        OrderRequest.OrderDto order = new OrderRequest.OrderDto(products);
        User ssar = userRepository.findByUsername("ssar").orElse(null);
        HttpHeaders headers = headers(ssar);
        HttpEntity<?> requestEntity = new HttpEntity<>(order, headers);

        //when
        ResponseEntity<?> response = testRestTemplate
                .postForEntity(
                        "/api/order",
                        requestEntity,
                        ResponseDto.class
                );

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(om.writeValueAsString(response.getBody()));
        JsonNode productListNode = jsonNode.get("data").get("orderProductList");
        assertEquals(2,productListNode.size());
        assertEquals(0,productListNode.get(0).get("product").get("qty").asInt());
    }

    @Nested
    @DisplayName("주문조회")
    class Orders{
        @Test
        @DisplayName("USER")
        void getOrders1() throws JsonProcessingException {
            //given
            User ssar = userRepository.findByUsername("ssar").orElse(null);
            HttpHeaders headers = headers(ssar);
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            //when

            ResponseEntity<?> response = testRestTemplate
                    .exchange(
                            "/api/orders",
                            HttpMethod.GET,
                            requestEntity,
                            ResponseDto.class
                    );

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            ObjectMapper om = new ObjectMapper();
            JsonNode jsonNode = om.readTree(om.writeValueAsString(response.getBody()));
            JsonNode productListNode = jsonNode.get("data").get(0).get("orderProductList");
            assertEquals(2,productListNode.size());
        }

        @Test
        @DisplayName("SELLER")
        void getOrders2() throws JsonProcessingException {
            //given
            User seller = userRepository.findByUsername("seller1").orElse(null);
            HttpHeaders headers = headers(seller);
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            //when

            ResponseEntity<?> response = testRestTemplate
                    .exchange(
                            "/api/orders",
                            HttpMethod.GET,
                            requestEntity,
                            ResponseDto.class
                    );

            //then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            ObjectMapper om = new ObjectMapper();
            JsonNode jsonNode = om.readTree(om.writeValueAsString(response.getBody()));
            JsonNode productListNode = jsonNode.get("data").get(0).get("orderProductList");
            assertEquals(1,productListNode.size());
        }
    }

    @Test
    @DisplayName("모든 주문조회(관리자)")
    void getAllOrders(){
        //given
        User ssar = userRepository.findByUsername("admin").orElse(null);
        HttpHeaders headers = headers(ssar);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        //when

        ResponseEntity<?> response = testRestTemplate
                .exchange(
                        "/api/admin/orders",
                        HttpMethod.GET,
                        requestEntity,
                        ResponseDto.class
                );

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    @DisplayName("주문취소")
    void cancelOrder() throws JsonProcessingException {
        //given
        long id = 1;
        User ssar = userRepository.findByUsername("ssar").orElse(null);
        HttpHeaders headers = headers(ssar);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        //when
        ResponseEntity<?> response = testRestTemplate
                .exchange(
                        "/api/order/"+id,
                        HttpMethod.DELETE,
                        requestEntity,
                        ResponseDto.class
                );

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(om.writeValueAsString(response.getBody()));
        JsonNode productListNode = jsonNode.get("data").get("orderProductList");
        assertEquals(2,productListNode.get(0).get("product").get("qty").asInt());
    }
}