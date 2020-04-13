package com.mluo.microservices.composite.product;

import com.mluo.api.composite.product.ProductAggregate;
import com.mluo.api.composite.product.RecommendationSummary;
import com.mluo.api.composite.product.ReviewSummary;
import com.mluo.api.core.product.Product;
import com.mluo.api.core.recommendation.Recommendation;
import com.mluo.api.core.review.Review;
import com.mluo.api.event.Event;
import com.mluo.microservices.composite.product.services.ProductCompositeIntegration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.BlockingQueue;

import static com.mluo.api.event.Event.Type.CREATE;
import static com.mluo.api.event.Event.Type.DELETE;
import static com.mluo.microservices.composite.product.IsSameEvent.sameEventExceptCreatedAt;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class MessagingTests {

    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    BlockingQueue<Message<?>> queueProducts = null;
    BlockingQueue<Message<?>> queueRecommendations = null;
    BlockingQueue<Message<?>> queueReviews = null;


    @Autowired
    private WebTestClient client;

    @Autowired
    private ProductCompositeIntegration.MessageSources channels;

    @Autowired
    private MessageCollector collector;

    @BeforeEach
    public void setUp() {
        queueProducts = getQueue(channels.outputProducts());
        queueRecommendations = getQueue(channels.outputRecommendations());
        queueReviews = getQueue(channels.outputReviews());
    }

    @Test
    public void createCompositeProduct1() {

        ProductAggregate composite = new ProductAggregate(1, "name", 1, null, null, null);
        postAndVerifyProduct(composite, OK);

        // Assert one expected new product events queued up
        assertEquals(1, queueProducts.size());

        Event<Integer, Product> expectedEvent = new Event(CREATE, composite.getProductId(), new Product(composite.getProductId(), composite.getName(), composite.getWeight(), null));
        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        // Assert none recommendations and review events
        assertEquals(0, queueRecommendations.size());
        assertEquals(0, queueReviews.size());
    }

    //
//    @Test
//    public void createCompositeProduct2() {
//
//        ProductAggregate composite = new ProductAggregate(1, "name", 1,
//                singletonList(new RecommendationSummary(1, "a", 1, "c")),
//                singletonList(new ReviewSummary(1, "a", "s", "c")), null);
//
//        postAndVerifyProduct(composite, OK);
//
//        // Assert one create product event queued up
//        assertEquals(1, queueProducts.size());
//
//        Event<Integer, Product> expectedProductEvent = new Event(CREATE, composite.getProductId(), new Product(composite.getProductId(), composite.getName(), composite.getWeight(), null));
//        assertThat(queueProducts, receivesPayloadThat(sameEventExceptCreatedAt(expectedProductEvent)));
//
//        // Assert one create recommendation event queued up
//        assertEquals(1, queueRecommendations.size());
//
//        RecommendationSummary rec = composite.getRecommendations().get(0);
//        Event<Integer, Product> expectedRecommendationEvent = new Event(CREATE, composite.getProductId(), new Recommendation(composite.getProductId(), rec.getRecommendationId(), rec.getAuthor(), rec.getRate(), rec.getContent(), null));
//        assertThat(queueRecommendations, receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));
//
//        // Assert one create review event queued up
//        assertEquals(1, queueReviews.size());
//
//        ReviewSummary rev = composite.getReviews().get(0);
//        Event<Integer, Product> expectedReviewEvent = new Event(CREATE, composite.getProductId(), new Review(composite.getProductId(), rev.getReviewId(), rev.getAuthor(), rev.getSubject(), rev.getContent(), null));
//        assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
//    }
//
//    @Test
//    public void deleteCompositeProduct() {
//
//        deleteAndVerifyProduct(1, OK);
//
//        // Assert one delete product event queued up
//        assertEquals(1, queueProducts.size());
//
//        Event<Integer, Product> expectedEvent = new Event(DELETE, 1, null);
//        assertThat(queueProducts, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));
//
//        // Assert one delete recommendation event queued up
//        assertEquals(1, queueRecommendations.size());
//
//        Event<Integer, Product> expectedRecommendationEvent = new Event(DELETE, 1, null);
//        assertThat(queueRecommendations, receivesPayloadThat(sameEventExceptCreatedAt(expectedRecommendationEvent)));
//
//        // Assert one delete review event queued up
//        assertEquals(1, queueReviews.size());
//
//        Event<Integer, Product> expectedReviewEvent = new Event(DELETE, 1, null);
//        assertThat(queueReviews, receivesPayloadThat(sameEventExceptCreatedAt(expectedReviewEvent)));
//    }
//
    private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
        return collector.forChannel(messageChannel);
    }

    private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
        client.post()
                .uri("/product-composite")
                .body(just(compositeProduct), ProductAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }
//
//    private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
//        client.delete()
//                .uri("/product-composite/" + productId)
//                .exchange()
//                .expectStatus().isEqualTo(expectedStatus);
//    }
}
