package task2;

import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCount {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Вхідний текст для аналізу
        String text = "Hello world this is a sample text. Hello everyone. Welcome to the world of concurrency.";

        String[] parts = text.split("\\."); // Розділяємо текст на частини за крапкою

        // ConcurrentHashMap для підрахунку слів
        ConcurrentHashMap<String, AtomicInteger> wordCounts = new ConcurrentHashMap<>();
        
        // Створюємо пул потоків
        ExecutorService executor = Executors.newFixedThreadPool(parts.length);
        
        // Список Future для збирання результатів
        List<Future<Void>> futures = new ArrayList<>();

        // Створюємо Callable задачі для підрахунку слів у кожній частині
        for (String part : parts) {
            Callable<Void> task = () -> {
                String[] words = part.toLowerCase().split("\\W+"); // Розбиваємо на слова
                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordCounts.computeIfAbsent(word, k -> new AtomicInteger(0)).incrementAndGet();
                    }
                }
                return null; // Повертаємо null, оскільки результат не потрібен
            };

            // Виконуємо задачу і додаємо до списку Future
            Future<Void> future = executor.submit(task);
            futures.add(future);
        }

        // Очікуємо завершення всіх задач і перевіряємо статус за допомогою isDone()
        for (Future<Void> future : futures) {
            while (!future.isDone()) {
                System.out.println("Задача ще виконується...");
                Thread.sleep(500); // Очікуємо 500 мс перед наступною перевіркою
            }
        }

        // Виводимо результати після завершення всіх задач
        executor.shutdown(); // Завершуємо пул потоків
        executor.awaitTermination(1, TimeUnit.MINUTES); // Чекаємо на завершення всіх потоків

        System.out.println("Підрахунок слів завершено. Результати:");
        wordCounts.forEach((word, count) -> System.out.println(word + ": " + count));
    }
}
