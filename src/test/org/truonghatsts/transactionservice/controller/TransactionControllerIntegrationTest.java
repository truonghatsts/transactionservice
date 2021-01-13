package org.truonghatsts.transactionservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.truonghatsts.transactionservice.config.ApplicationProperties;
import org.truonghatsts.transactionservice.domain.enums.TransactionType;
import org.truonghatsts.transactionservice.domain.payload.TransactionPayload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.truonghatsts.transactionservice.constants.CodeMessage.*;

@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TransactionControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationProperties props;

    private static HttpHeaders headers;

    @Test
    @Order(1)
    void givenEmptyParentTransaction_whenSaveTransaction_thenReturnSuccessfully() throws Exception {

        TransactionPayload payload = new TransactionPayload();
        payload.setAmount(100d);
        payload.setType(TransactionType.DISBURSEMENT);

        headers = new HttpHeaders();
        headers.set("X-API-KEY", props.getSecurity().getApiKey());
        HttpEntity<TransactionPayload> body = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/transaction/1", HttpMethod.PUT, body, String.class);

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        assertEquals(PROCESSING_SUCCESSFULLY_CODE, jsonNode.get("code").intValue());
        assertEquals(PROCESSING_SUCCESSFULLY_MESSAGE, jsonNode.get("message").asText());
    }

    @Test
    @Order(2)
    void givenInvalidParentTransaction_whenSaveTransaction_thenReturnError() throws Exception {

        TransactionPayload payload = new TransactionPayload();
        payload.setAmount(100d);
        payload.setType(TransactionType.DISBURSEMENT);
        payload.setParentId(100L);

        HttpEntity<TransactionPayload> body = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/transaction/99", HttpMethod.PUT, body, String.class);

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        assertEquals(PARENT_TRANSACTION_NOT_FOUND_CODE, jsonNode.get("code").intValue());
        assertEquals(PARENT_TRANSACTION_NOT_FOUND_MESSAGE, jsonNode.get("message").asText());
    }

    @Test
    @Order(3)
    void givenValidParentTransaction_whenSaveTransaction_thenReturnSuccessfully() throws Exception {

        TransactionPayload firstRepayment = new TransactionPayload();
        firstRepayment.setAmount(-50d);
        firstRepayment.setType(TransactionType.REPAYMENT);
        firstRepayment.setParentId(1L);

        TransactionPayload secondRepayment = new TransactionPayload();
        secondRepayment.setAmount(-50d);
        secondRepayment.setType(TransactionType.REPAYMENT);
        secondRepayment.setParentId(1L);

        ResponseEntity<String> response1 = restTemplate.exchange("http://localhost:" + port + "/transaction/2",
                HttpMethod.PUT,
                new HttpEntity<>(firstRepayment, headers),
                String.class);

        ResponseEntity<String> response2 = restTemplate.exchange("http://localhost:" + port + "/transaction/3",
                HttpMethod.PUT,
                new HttpEntity<>(secondRepayment, headers),
                String.class);

        JsonNode jsonNode1 = objectMapper.readTree(response1.getBody());
        assertEquals(PROCESSING_SUCCESSFULLY_CODE, jsonNode1.get("code").intValue());
        assertEquals(PROCESSING_SUCCESSFULLY_MESSAGE, jsonNode1.get("message").asText());

        JsonNode jsonNode2 = objectMapper.readTree(response2.getBody());
        assertEquals(PROCESSING_SUCCESSFULLY_CODE, jsonNode2.get("code").intValue());
        assertEquals(PROCESSING_SUCCESSFULLY_MESSAGE, jsonNode2.get("message").asText());
    }

    @Test
    @Order(4)
    void whenGetTransaction_thenReturnTransaction() throws Exception {

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/transaction/1",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                String.class);
        String type = objectMapper.readTree(response.getBody()).get("data").get("type").asText();
        double amount = objectMapper.readTree(response.getBody()).get("data").get("amount").asDouble();
        assertEquals(TransactionType.DISBURSEMENT.toString(), type);
        assertEquals(100d, amount);
    }

    @Test
    @Order(5)
    void whenGetTransaction_thenReturnError() {

        ResponseEntity<Object> response = restTemplate.exchange("http://localhost:" + port + "/transaction/100",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(6)
    void whenFindByType_thenReturnTransactions() throws JsonProcessingException {

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/transaction/types/REPAYMENT",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                String.class);
        JsonNode data = objectMapper.readTree(response.getBody()).get("data");
        assertTrue(data.isArray());
        int count = 0;
        for (JsonNode node : data) {
            assertEquals(TransactionType.REPAYMENT.toString(), node.get("type").asText());
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    @Order(7)
    void givenValidParentId_whenSum_thenReturnSum() throws JsonProcessingException {

        ResponseEntity<String> disbursementResponse = restTemplate.exchange("http://localhost:" + port + "/transaction/sum/1",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                String.class);
        double disbursementSum = objectMapper.readTree(disbursementResponse.getBody()).get("data").get("sum").doubleValue();
        assertEquals(0d, disbursementSum);

        ResponseEntity<String> firstRepaymentResponse = restTemplate.exchange("http://localhost:" + port + "/transaction/sum/2",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                String.class);
        double firstRepaymentSum = objectMapper.readTree(firstRepaymentResponse.getBody()).get("data").get("sum").doubleValue();
        assertEquals(-50d, firstRepaymentSum);
    }

    @Test
    @Order(8)
    void givenInvalidParentId_whenSum_thenReturnError() throws JsonProcessingException {

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/transaction/sum/100",
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                String.class);
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        assertEquals(TRANSACTION_NOT_FOUND_CODE, jsonNode.get("code").intValue());
        assertEquals(TRANSACTION_NOT_FOUND_MESSAGE, jsonNode.get("message").asText());
    }
}