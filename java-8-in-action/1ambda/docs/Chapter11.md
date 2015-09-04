# Completable Future

## Java Future

자바 5 부터는 비동기 계산을 모델링하는 `Future` 인터페이스가 추가되었다. `Future` 를 이용하려면 `Callable` 을 `ExecutorService` 에 제출(**sumbit**) 하면 된다.

```java
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
```

스레드에 비해 직관적인 고수준 동시성을 제공함에도 불구하고 자바의 `Future` 는 다음과 같은 단점이 부족하다

- 두개의 비동기 계산을 하나로 합치는 것
- Future Collection 의 모든 Task 가 완료되길 기다리는 것 (`scalaz.sequence`)
- Future Collection 에서 가장 빨리 완료되는 Future 를 이용하는 것
- 프로그램적으로 Future 를 완료 (번역이;)
- Event 방식으로 Future 완료를 Noti (`Future.get` 처럼 기다리면서 블록되지 않고)

자바 8에서는 `CompletableFuture` 를 이용해 위에서 언급한 기능들을 구현할 수 있다.

<br/>

## CompletableFuture

```java
public class Shop {

    private String shopName;

    private static final Random generator = new Random();

    public Shop(String shopName) { this.shopName = shopName; }

    public static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void randomDelay() {
        try {
            Thread.sleep(generator.nextInt(1500));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public double getPrice(String product) {
        return calculatePrice(product);
    }

    private double calculatePrice(String product) {
        delay();
        return generator.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public Future<Double> getPricesAsync(String product) {
        CompletableFuture<Double> f = new CompletableFuture<>();

        new Thread(() -> {
            double price = calculatePrice(product);
            f.complete(price);
        }).start();

        return f;
    }


    public Future<Double> getPricesAsyncException(String product) {
        CompletableFuture<Double> f = new CompletableFuture<>();

        new Thread(() -> {
            double price = calculatePrice(product);
            f.completeExceptionally(new RuntimeException("example"));
        }).start();

        return f;
    }
}
```

```java
    @Test
    public void test_getPriceAsync() {

        long operationTime = Performance.getConsumedTime(() -> {
            Future<Double> futurePrice = s.getPricesAsync("MacBook 11");

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
```

`CompletableFuture.completeExceptionally` 를 이용해서 예외를 전달할 수 있다. 클라이언트는 `ExecutionException` 을 전달받는다.
 
```java
@Test(expected = ExecutionException.class)
public void test_getPriceAsyncException() throws ExecutionException, InterruptedException {
    s.getPricesAsyncException("asdasd").get();
}
```

`getPriceAsync` 를 다음처럼 더 간단히 만들수도 있다. 

```java
public Future<Double> getPriceAsync(String product) {
    return CompletableFuture.supplyAsync(() -> calculatePrice(product));
}
```

`CompletableFuture.supplyAsync()` 는 비동기 연산의 실행 결과를 `complete` 에 넘겨준다. 인자가 두개인 `supplyAsync` 를 이용하면 
스레드 풀도 조절할 수 있다.

```java
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier,
                                                   Executor executor) {
    return asyncSupplyStage(screenExecutor(executor), supplier);
}
```

## Non-Blocking

`findPrices` 함수를 3 개의 버전으로 구현할 수 있다. 

```java
public static List<String> findPricesSync(List<Shop> shops, String product) {
    return shops
        .stream()
        .map(s -> String.format("%s price is %.2f", s.getName(), s.getPrice(product)))
        .collect(toList());
}

public static List<String> findPricesAsyncUsingParallelStream(List<Shop> shops, String product) {
    return shops
        .parallelStream()
        .map(s -> String.format("%s price is %.2f", s.getName(), s.getPrice(product)))
        .collect(toList());
}

public static List<String> findPricesAsyncUsingCompletableFuture(List<Shop> shops, String product) {
    List<CompletableFuture<String>> futures = shops
        .stream()
        .map(s -> CompletableFuture.supplyAsync(() ->
            String.format("%s price is %.2f", s.getName(), s.getPrice(product)
            )))
        .collect(toList());

    return futures
        .stream()
        .map(CompletableFuture::join)
        .collect(toList());
}
```

테스트와 퍼포먼스 결과를 보면,

```java
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
        List<String> priceTexts = Shop.findPricesAsyncUsingCompletableFuture(shops, "MacBook 11");
        assertEquals(shops.size(), priceTexts.size());
    });

    System.out.println("getPrices Sync : " + time1);
    System.out.println("getPrices Async Parallel Stream : " + time2);
    System.out.println("getPrices Async Completable Future: " + time3);
}

// output
getPrices Sync : 1846
getPrices Async Parallel Stream : 413
getPrices Async Completable Future: 411
```

`parallelStream` 의 경우 `Runtime.getRuntime().availableProcessors()` 가 반환하는 코어 수만큼 쓸 수 있지만, 
`asyncSupply` 의 경우 `Executor` 를 직접 제공할 수 있기 때문에 코어가 제공하는 스레드 수 이상을 요구하는 IO 연산에 대한 퍼포먼스를 증가시킬 수 있다.

```java
private static final Executor executor = Executors.newFixedThreadPool(100, new ThreadFactory() {
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    }
});

public static List<String> findPricesAsyncUsingCompletableFuture1(List<Shop> shops, String product) {
        List<CompletableFuture<String>> futures = shops
            .stream()
            .map(s -> CompletableFuture.supplyAsync(() ->
                String.format("%s price is %.2f", s.getName(), s.getPrice(product)
                ), executors))
            .collect(toList());

        return futures
            .stream()
            .map(CompletableFuture::join)
            .collect(toList());
    }
    
// output
getPrices Sync : 1842
getPrices Async Parallel Stream : 409
getPrices Async Completable Future: 409
getPrices Async Completable Future with Custom Executor: 207
```

따라서 다음과 같은 두 가지 결론을 내릴 수 있다.

- I/O 가 포함되지 않은 계산 중심의 연산을 수행할때는 프로세스 코어 수 이상의 스레드를 활용할 수 없으므로 병렬 스트림을 사용하는 것이 낫다
- I/O 가 많이 포함된 연산은 적당한 스레드 수를 결정하고, `CompletableFuture` 에 `Executor` 를 제공해서 사용하면 훨씬 빠르다

<br/>

## Pipeline

```java
import static chapter11.Util.delay;
import static chapter11.Util.format;

public class Discount {
    public enum Code {
        NONE(0), SILVER(5), PLATINUM(15), DIAMOND(20);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price: " +
            Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    private static double apply(double price, Code code) {
        delay();
        return format(price * (100 - code.percentage) / 100);
    }
}

public class Quote {

    private final String shopName;
    private final double price;
    private final Discount.Code discountCode;

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Discount.Code getDiscountCode() {
        return discountCode;
    }


    public Quote(String shopName, double price, Discount.Code code) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = code;
    }

    public static Quote parse(String s) {
        String[] splited = s.split(":");
        String shopName = splited[0];
        double price = Double.parseDouble(splited[1]);
        Discount.Code code = Discount.Code.valueOf(splited[2]);

        return new Quote(shopName, price, code);
    }
}


public static List<String> getDiscountPricesSync(List<Shop> shops, String product) {
    return shops
        .stream()
        .map(s -> s.getPriceText(product))
        .map(Quote::parse)
        .map(Discount::applyDiscount)
        .collect(toList());
}
```

`getDiscountPricesSync` 구현에서는 `Shop.getPriceText` 와 `Discount.apply` 를 순차적으로 적용하므로 느리다. 따라서 
여기에 `CompletableFuture` 를 적용해서 성능을 개선하면 다음과 같다.

```java
public static List<String> getDiscountPricesAsync(List<Shop> shops, String product) {
    List<CompletableFuture<String>> futures = shops
        .stream()
        .map(s -> CompletableFuture.supplyAsync(() -> s.getPriceText(product), executor))
        .map(f -> f.thenApply(Quote::parse))
        .map(f -> f.thenCompose(quote ->
            CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)))
        .collect(toList());

    return futures
        .stream()
        .map(CompletableFuture::join)
        .collect(toList());
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

// output
getDiscountPrices Sync: 3676
getDiscountPrices Async: 415
```

## Compose, Combine

- `CompletableFuture.thenCompose` 는 한 퓨처의 실행 결과를 다음 퓨쳐의 입력으로 사용한다.
- `CompletableFuture.thenCompose` 는 두개의 퓨처를 `BiFunction` 을 이용해 조합한다.

```java
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

public class ExchangeService {
    
    public enum Money {
        USD, FRANC
    }

    public static double getRate(Money m1, Money m2) {
        Util.delay();
        return 1.5;
    }
}
```

만약 `price * rate` 를 별개의 스레드에서 수행하고 싶다면, `thenCombineAsync` 를 사용하면 된다.

## thenAccept, allOf

```java
public static void supplyAllPrices(List<Shop> shops, String product, Consumer<String> callback) {
    CompletableFuture[] futures = shops
        .stream()
        .map(s -> CompletableFuture.supplyAsync(() -> s.getPriceTextRandomDelay(product), executor))
        .map(f -> f.thenApply(Quote::parse))
        .map(f -> f.thenCompose(quote ->
            CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)))
        .map(f -> f.thenAcceptAsync(callback))
        .toArray(CompletableFuture[]::new);

    CompletableFuture.allOf(futures).join();
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

// output
MyFavoriteShop price: 91.35 (discounted) done in 610
BuyItAll price: 90.13 (discounted) done in 807
Nxm price: 157.63 (discounted) done in 1196
Samsung Store price: 122.36 (discounted) done in 1386
SAGA price: 87.36 (discounted) done in 1551
LG Store price: 123.28 (discounted) done in 1707
BestPrice price: 93.13 (discounted) done in 1737
LetsSaveBig price: 115.02 (discounted) done in 1823
Apple Store price: 138.81 (discounted) done in 1840
supplyAllPrices time: 1795
```

- `allOf` 는 `CompletableFuture` 배열을 받아 `CompletableFuture<Void>` 를 반환한다. 여기에 `join` 등의 메소드를 이용해 모든 퓨처를 기다릴 수 있다.
- `anyOf` 는 `CompletableFuture` 배열을 받아 처음으로 완료된 `CompletableFuture<Object>` 를 반환한다.

