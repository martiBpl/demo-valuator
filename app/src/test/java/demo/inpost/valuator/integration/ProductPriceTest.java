package demo.inpost.valuator.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.inpost.valuator.discount.ProductDiscountDbo;
import demo.inpost.valuator.discount.ProductDiscountRepository;
import demo.inpost.valuator.product.ProductDbo;
import demo.inpost.valuator.product.ProductRepository;
import demo.inpost.valuator.product.dto.ProductPriceDto;
import demo.inpost.valuator.product.service.FixedPercentageProductDiscountStrategy;
import demo.inpost.valuator.product.service.ProductDiscountStrategy;
import demo.inpost.valuator.product.service.QuantityBasedProductDiscountStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductPriceTest {
    public static final String ONE = "1";
    public static final String THREE = "3";
    public static final String FIVE = "5";
    public static final String TEN_AND_A_HALF = "10.5";
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDiscountRepository productDiscountRepository;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void missingQuantityParam_shouldRespondBadRequest() {
        var response = whenGetProductPriceEndpointCalled(UUID.randomUUID().toString(), null);
        thenShouldReceiveBadRequestErrorResponse(response, "Missing required parameter: quantity");
    }

    @Test
    void invalidQuantityParam_shouldRespondBadRequest() {
        var response = whenGetProductPriceEndpointCalled(UUID.randomUUID().toString(), "aaa");
        thenShouldReceiveBadRequestErrorResponse(response, "Invalid parameter: quantity");
    }

    @Test
    void invalidProductIdPathParam_shouldRespondBadRequest() {
        String productId = "productId";
        var response = whenGetProductPriceEndpointCalled(productId, ONE);
        thenShouldReceiveBadRequestErrorResponse(response, "Invalid id parameter");
    }

    @Test
    void missingProduct_shouldRespondNotFound() {
        String productId = UUID.randomUUID().toString();
        var response = whenGetProductPriceEndpointCalled(productId, ONE);
        thenShouldReceiveNotFoundErrorResponse(response, productId);
    }

    @Test
    void singleProductWithoutDiscounts_shouldNotApplyDiscount() {
        BigDecimal itemPrice = new BigDecimal("10.5");
        UUID productId = givenPersistedProduct(itemPrice);
        var response = whenGetProductPriceEndpointCalled(productId.toString(), ONE);

        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(itemPrice)
                .originalItemPrice(itemPrice)
                .itemDiscount(BigDecimal.ZERO)
                .totalPriceAfterDiscount(itemPrice)
                .originalTotalPrice(itemPrice)
                .totalDiscount(BigDecimal.ZERO)
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void differentProductWithoutDiscounts_shouldNotApplyDiscount() {
        BigDecimal itemPrice = new BigDecimal("10.5");
        UUID productId = givenPersistedProduct(itemPrice);
        UUID productId2 = givenPersistedProduct(itemPrice);
        var strategy = givenFixedPercentageDiscountStrategy(BigDecimal.TEN);
        givenPersistedProductDiscount(productId2, strategy, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), ONE);

        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(itemPrice)
                .originalItemPrice(itemPrice)
                .itemDiscount(BigDecimal.ZERO)
                .totalPriceAfterDiscount(itemPrice)
                .originalTotalPrice(itemPrice)
                .totalDiscount(BigDecimal.ZERO)
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void multipleProductsWithoutDiscounts_shouldNotApplyDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var response = whenGetProductPriceEndpointCalled(productId.toString(), THREE);

        BigDecimal expectedTotalPrice = new BigDecimal("31.5");
        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(itemPrice)
                .originalItemPrice(itemPrice)
                .itemDiscount(BigDecimal.ZERO)
                .totalPriceAfterDiscount(expectedTotalPrice)
                .originalTotalPrice(expectedTotalPrice)
                .totalDiscount(BigDecimal.ZERO)
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void singleProductWithSingleFixedPercentageActiveDiscount_shouldApplyDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var strategy = givenFixedPercentageDiscountStrategy(new BigDecimal(FIVE));
        givenPersistedProductDiscount(productId, strategy, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), ONE);

        BigDecimal itemPriceAfterDiscount = new BigDecimal("9.97");
        BigDecimal itemDiscount = new BigDecimal("0.53");
        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(itemPriceAfterDiscount)
                .originalItemPrice(itemPrice)
                .itemDiscount(itemDiscount)
                .totalPriceAfterDiscount(itemPriceAfterDiscount)
                .originalTotalPrice(itemPrice)
                .totalDiscount(itemDiscount)
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void multipleProductWithSingleFixedPercentageActiveDiscount_shouldApplyDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var strategy = givenFixedPercentageDiscountStrategy(new BigDecimal(FIVE));
        givenPersistedProductDiscount(productId, strategy, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), THREE);

        BigDecimal itemPriceAfterDiscount = new BigDecimal("9.97");
        BigDecimal itemDiscount = new BigDecimal("0.53");
        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(itemPriceAfterDiscount)
                .originalItemPrice(itemPrice)
                .itemDiscount(itemDiscount)
                .totalPriceAfterDiscount(new BigDecimal("29.91"))
                .originalTotalPrice(new BigDecimal("31.5"))
                .totalDiscount(new BigDecimal("1.59"))
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void singleProductWithMultipleFixedPercentageActiveDiscount_shouldApplyDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var strategy = givenFixedPercentageDiscountStrategy(BigDecimal.ONE);
        var strategy2 = givenFixedPercentageDiscountStrategy(new BigDecimal(FIVE));
        givenPersistedProductDiscount(productId, strategy, Instant.now().plus(1, ChronoUnit.DAYS));
        givenPersistedProductDiscount(productId, strategy2, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), ONE);

        BigDecimal itemPriceAfterDiscount = new BigDecimal("9.97");
        BigDecimal itemDiscount = new BigDecimal("0.53");
        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(itemPriceAfterDiscount)
                .originalItemPrice(itemPrice)
                .itemDiscount(itemDiscount)
                .totalPriceAfterDiscount(itemPriceAfterDiscount)
                .originalTotalPrice(itemPrice)
                .totalDiscount(itemDiscount)
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void multipleProductWithMultipleFixedPercentageActiveDiscount_shouldApplyDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var strategy = givenFixedPercentageDiscountStrategy(BigDecimal.ONE);
        var strategy2 = givenFixedPercentageDiscountStrategy(new BigDecimal(FIVE));
        givenPersistedProductDiscount(productId, strategy, Instant.now().plus(1, ChronoUnit.DAYS));
        givenPersistedProductDiscount(productId, strategy2, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), THREE);

        BigDecimal itemPriceAfterDiscount = new BigDecimal("9.97");
        BigDecimal itemDiscount = new BigDecimal("0.53");
        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(itemPriceAfterDiscount)
                .originalItemPrice(itemPrice)
                .itemDiscount(itemDiscount)
                .totalPriceAfterDiscount(new BigDecimal("29.91"))
                .originalTotalPrice(new BigDecimal("31.5"))
                .totalDiscount(new BigDecimal("1.59"))
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void singleProductWithSingleQuantityBasedActiveDiscount_shouldNotApplyDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var thresholds = givenExampleThresholdMap();
        var strategy = givenQuantityBasedDiscountStrategy(thresholds);
        givenPersistedProductDiscount(productId, strategy, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), ONE);

        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(itemPrice)
                .originalItemPrice(itemPrice)
                .itemDiscount(BigDecimal.ZERO)
                .totalPriceAfterDiscount(itemPrice)
                .originalTotalPrice(itemPrice)
                .totalDiscount(BigDecimal.ZERO)
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void multipleProductWithSingleQuantityBasedActiveDiscount_shouldApplyFirstThresholdDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var thresholds = givenExampleThresholdMap();
        var strategy = givenQuantityBasedDiscountStrategy(thresholds);
        givenPersistedProductDiscount(productId, strategy, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), FIVE);

        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(new BigDecimal("10.39"))
                .originalItemPrice(itemPrice)
                .itemDiscount(new BigDecimal("0.11"))
                .totalPriceAfterDiscount(new BigDecimal("51.95"))
                .originalTotalPrice(new BigDecimal("52.5"))
                .totalDiscount(new BigDecimal("0.55"))
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void multipleProductWithSingleQuantityBasedActiveDiscount_shouldApplySecondThresholdDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var thresholds = givenExampleThresholdMap();
        var strategy = givenQuantityBasedDiscountStrategy(thresholds);
        givenPersistedProductDiscount(productId, strategy, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), TEN_AND_A_HALF);

        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(new BigDecimal("9.97"))
                .originalItemPrice(itemPrice)
                .itemDiscount(new BigDecimal("0.53"))
                .totalPriceAfterDiscount(new BigDecimal("104.69"))
                .originalTotalPrice(new BigDecimal("110.25"))
                .totalDiscount(new BigDecimal("5.56"))
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }

    @Test
    void multipleProductWithMultipleActiveDiscount_shouldApplyFixedPercentageDiscount() {
        BigDecimal itemPrice = new BigDecimal(TEN_AND_A_HALF);
        UUID productId = givenPersistedProduct(itemPrice);
        var thresholds = givenExampleThresholdMap();
        var strategy1 = givenQuantityBasedDiscountStrategy(thresholds);
        var strategy2 = givenFixedPercentageDiscountStrategy(BigDecimal.TEN);
        givenPersistedProductDiscount(productId, strategy1, Instant.now().plus(1, ChronoUnit.DAYS));
        givenPersistedProductDiscount(productId, strategy2, Instant.now().plus(1, ChronoUnit.DAYS));

        var response = whenGetProductPriceEndpointCalled(productId.toString(), FIVE);

        var expectedDto = ProductPriceDto.builder()
                .itemPriceAfterDiscount(new BigDecimal("9.45"))
                .originalItemPrice(itemPrice)
                .itemDiscount(new BigDecimal("1.05"))
                .totalPriceAfterDiscount(new BigDecimal("47.25"))
                .originalTotalPrice(new BigDecimal("52.5"))
                .totalDiscount(new BigDecimal("5.25"))
                .build();
        thenResponseShouldBeOk(response, expectedDto);
    }


    private UUID givenPersistedProduct(BigDecimal itemPrice) {
        var dbo = ProductDbo.builder()
                .price(itemPrice)
                .productId(UUID.randomUUID())
                .name("test product " + UUID.randomUUID())
                .build();
        return productRepository.save(dbo).getProductId();
    }

    private void givenPersistedProductDiscount(UUID productId, ProductDiscountStrategy strategy, Instant activeTo) {
        var dbo = ProductDiscountDbo.builder()
                .productId(productId)
                .activeTo(activeTo)
                .strategy(strategy)
                .build();

        productDiscountRepository.save(dbo);
    }

    private FixedPercentageProductDiscountStrategy givenFixedPercentageDiscountStrategy(BigDecimal percentage) {
        return new FixedPercentageProductDiscountStrategy(percentage);
    }

    private QuantityBasedProductDiscountStrategy givenQuantityBasedDiscountStrategy(Map<BigDecimal, BigDecimal> thresholds) {
        return new QuantityBasedProductDiscountStrategy(thresholds);
    }

    private Map<BigDecimal, BigDecimal> givenExampleThresholdMap() {
        return Map.of(BigDecimal.TEN, new BigDecimal(FIVE), new BigDecimal(FIVE), new BigDecimal(ONE));
    }


    private WebTestClient.ResponseSpec whenGetProductPriceEndpointCalled(String productId, String quantity) {

        return webTestClient.get()
                .uri((builder) -> {
                    builder.path(format("/product/%s/price", productId));
                    if (quantity != null) builder.queryParam("quantity", quantity);
                    return builder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    private void thenResponseShouldBeOk(WebTestClient.ResponseSpec response, ProductPriceDto expected) {
        response.expectStatus().is2xxSuccessful();
        try {
            response.expectBody().json(objectMapper.writeValueAsString(expected));
        } catch (JsonProcessingException ex) {
            Assertions.fail("Failed to parse expected");
        }
    }

    private void thenShouldReceiveBadRequestErrorResponse(WebTestClient.ResponseSpec response, String expectedMessage) {
        response.expectStatus().isBadRequest()
                .expectBody().jsonPath("message", expectedMessage)
        ;
    }

    private void thenShouldReceiveNotFoundErrorResponse(WebTestClient.ResponseSpec response, String productId) {
        response.expectStatus().isNotFound()
                .expectBody().jsonPath("message", format("Product %s not found.", productId));
    }
}
