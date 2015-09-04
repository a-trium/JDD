package chapter11;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static chapter11.ExchangeService.Money.*;

@RunWith(JUnit4.class)
public class Chapter11Spec {
    Shop s;
    List<Shop> shops;

    @Before
    public void setUp() {
        s = new Shop("BestShop");
        shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("Samsung Store"),
            new Shop("LG Store"),
            new Shop("Apple Store"),
            new Shop("SAGA"),
            new Shop("Nxm"),
            new Shop("BuyItAll")
        );
    }

    public void test_basic_future() {
        ExecutorService es = Executors.newCachedThreadPool();
        Future<Double> future = es.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return 451.010410 * 401059.104010;
            }
        });

        // do something
        try {
            Double result = future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace(); /* 현재 스레드 대기 중 인터럽트 발생 */
        } catch (ExecutionException e) {
            e.printStackTrace(); /* Callable 실행중 예외 발생 */
        } catch (TimeoutException e) {
            e.printStackTrace(); /* timed out */
        }
    }

    @Test
    public void test_getPriceAsync() {

        long operationTime = Performance.getConsumedTime(() -> {
            Future<Double> futurePrice = s.getPriceAsync2("MacBook 11");

            try {
                double price = futurePrice.get();
                System.out.println("Price " + price);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Time: " + operationTime);
    }

    @Test(expected = ExecutionException.class)
    public void test_getPriceAsyncException() throws ExecutionException, InterruptedException {
        s.getPricesAsyncException("asdasd").get();
    }

    @Test
    public void test_findPrices() {
        long time1 = Performance.getConsumedTime(() -> {
                List<String> priceTexts = Shop.findPricesSync(shops, "MacBook 11");
                assertEquals(shops.size(), priceTexts.size());
            });

        long time2 = Performance.getConsumedTime(() -> {
            List<String> priceTexts = Shop.findPricesAsyncUsingParallelStream(shops, "MacBook 11");
            assertEquals(shops.size(), priceTexts.size());
        });

        long time3 = Performance.getConsumedTime(() -> {
            List<String> priceTexts = Shop.findPricesAsyncUsingCompletableFuture1(shops, "MacBook 11");
            assertEquals(shops.size(), priceTexts.size());
        });

        long time4 = Performance.getConsumedTime(() -> {
            List<String> priceTexts = Shop.findPricesAsyncUsingCompletableFuture(shops, "MacBook 11");
            assertEquals(shops.size(), priceTexts.size());
        });

        System.out.println("getPrices Sync : " + time1);
        System.out.println("getPrices Async Parallel Stream : " + time2);
        System.out.println("getPrices Async Completable Future: " + time3);
        System.out.println("getPrices Async Completable Future with Custom Executor: " + time4);
    }

    @Test
    public void test_findDiscountPrices() {
        long time1 = Performance.getConsumedTime(() -> {
            List<String> discountPrices =
                Shop.getDiscountPricesSync(shops, "MaxBook 11");

            assertEquals(shops.size(), discountPrices.size());
        });

        long time2 = Performance.getConsumedTime(() -> {
            List<String> discountPrices =
                Shop.getDiscountPricesAsync(shops, "MaxBook 11");

            assertEquals(shops.size(), discountPrices.size());
        });

        System.out.println("getDiscountPrices Sync: " + time1);
        System.out.println("getDiscountPrices Async: " + time2);
    }

    @Test
    public void test_findPriceInUSD() {
        Shop s1 = shops.get(0);
        String product = "MacBook 11";

        Future<Double> priceInUSD =
            CompletableFuture.supplyAsync(() -> s.getPrice(product))
            .thenCombine(
                CompletableFuture.supplyAsync(() -> ExchangeService.getRate(USD, FRANC)),
                (price, rate) -> price * rate
            );
    }

    @Test
    public void test_supplyAllPrices() {
        long start = System.nanoTime();
        long time1 = Performance.getConsumedTime(() -> {
            Shop.supplyAllPrices(shops, "Mac Book 13", (price) -> {
                long diff = (System.nanoTime() - start) / 1000000;
                System.out.println(price + " (discounted) done in " + diff);
            });
        });

        System.out.println("supplyAllPrices time: " + time1);
    }
}


