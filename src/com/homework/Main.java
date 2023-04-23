package com.homework;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

  public static final int LENGTH = 100;
  public static final int COUNT = 100000;
  public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(LENGTH);
  public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(LENGTH);
  public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(LENGTH);
  static Thread generatedTextThread;

  public static void main(String[] args) {
    generatedTextThread = new Thread(() -> {
      try {
        for (int i = 0; i < COUNT; i++) {
          String text = generateText("abc", COUNT);
          queueA.put(text);
          queueB.put(text);
          queueC.put(text);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    generatedTextThread.start();

    Thread a = createThread(queueA, 'a');
    Thread b = createThread(queueB, 'b');
    Thread c = createThread(queueC, 'c');
    a.start();
    b.start();
    c.start();
  }

  public static String generateText(String letters, int length) {
    Random random = new Random();
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < length; i++) {
      text.append(letters.charAt(random.nextInt(letters.length())));
    }
    return text.toString();
  }

  public static int findMax(BlockingQueue<String> queue, char letter) throws InterruptedException {
    int maxCount = 0;
    while (generatedTextThread.isAlive()) {
      String str = queue.take();
      int count = (int) str.chars().filter(chr -> chr == letter).count();
      if (maxCount < count) {
        maxCount = count;
      }
    }
    return maxCount;
  }

  public static Thread createThread(BlockingQueue<String> queue, char letter) {
    return new Thread(() -> {
      try {
        int maxCount = findMax(queue, letter);
        System.out.printf("Наибольшее количество символа %s %s%n", letter, maxCount);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }
}
