package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.model.Currency;
import com.ecommerce.payment.model.PaymentSatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentControllerTests {
    @Autowired
    private WebApplicationContext applicationContext;
    private MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeAll
    public void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

    @Test
    public void should_initiate_payment() throws Exception {
        var paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(10);
        paymentRequest.setCurrency(Currency.EURO);
        paymentRequest.setPayeeId("PK123444");
        paymentRequest.setPayerId("PK45555");
        paymentRequest.setPreferredGateway("STRIPE");
        String request = objectMapper.writeValueAsString(paymentRequest);

        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(post("/payments").contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(PaymentSatus.DONE.name()))
                .andDo(print());
    }

    @Test
    public void should_return_bad_request_when_payerId_is_missing() throws Exception {
        var paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(10);
        paymentRequest.setCurrency(Currency.EURO);
        // payerId is intentionally left null or empty to trigger the validation error
        paymentRequest.setPayeeId("PK123444");
        paymentRequest.setPreferredGateway("STRIPE");
        String request = objectMapper.writeValueAsString(paymentRequest);
        MediaType MEDIA_TYPE_JSON_UTF8 = new MediaType("application", "json", java.nio.charset.Charset.forName("UTF-8"));
        mockMvc.perform(post("/payments").contentType(MEDIA_TYPE_JSON_UTF8)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").doesNotExist())
                .andDo(print());
    }
}
