package chapter11;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public class Shop {

    private String shopName;

    private static final Random generator = new Random();

    private static final Executor executor = Executors.newFixedThreadPool(100, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });

    public Shop(String shopName) { this.shopName = shopName; }

    public String getName() { return shopName; }

    public double getPrice(String product) {
        return calculatePrice(product);
    }

    public String getPriceTextRandomDelay(String product) {
        Util.randomDelay();
        return createPriceText(product);
    }

    public String getPriceText(String product) {
        Util.delay();
        return createPriceText(product);
    }

    private String createPriceText(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[
            generator.nextInt(Discount.Code.values().length)
            ];

        return String.format("%s:%.2f:%s", shopName, price, code);
    }

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

    public static List<String> getDiscountPricesSync(List<Shop> shops, String product) {
        return shops
            .stream()
            .map(s -> s.getPriceText(product))
            .map(Quote::parse)
            .map(Discount::applyDiscount)
            .collect(toList());
    }

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

    private double calculatePrice(String product) {
        Util.delay();
        return createPrice(product);
    }

    private double calculatePriceRandomDelay(String product) {
        Util.randomDelay();
        return createPrice(product);
    }

    private double createPrice(String product) {
        return generator.nextDouble() * product.charAt(0) + product.charAt(1);
    }

    public Future<Double> getPriceAsync(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

    public Future<Double> getPriceAsync2(String product) {
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
                ), executor))
            .collect(toList());

        return futures
            .stream()
            .map(CompletableFuture::join)
            .collect(toList());
    }

    public static List<String> findPricesAsyncUsingCompletableFuture1(List<Shop> shops, String product) {
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


    public static List<String> findCheapestPriceSync(List<Shop> shops, String product) {
        return Collections.emptyList();
//        return shops
//            .stream()
//            .map(s -> )
    }
}
