package edu.pucmm;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author me@fredpena.dev
 * @created 03/06/2025 - 18:00
 */
public class ProducerConsumerConcurrent {

    private static final int QUEUE_CAPACITY     = 50;
    private static final int PRODUCER_COUNT     = 3;
    private static final int CONSUMER_COUNT     = 3;
    private static final int TOTAL_ITEMS        = 300; // total numeros a producir
    private static final int POISON_PILL        = -1;  // valor especial para terminar consumidores

    // Cola bloqueante con capacidad limitada
    private static final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    // Mapa para contar cuantos elementos consumio cada hilo consumidor
    private static final ConcurrentHashMap<Thread, Integer> consumoMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // Calcular cuantos items produce cada productor
        int itemsPerProducer = TOTAL_ITEMS / PRODUCER_COUNT;

        // Crear y arrancar hilos productores
        Thread[] producers = new Thread[PRODUCER_COUNT];
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producers[i] = new ProducerThread(queue, itemsPerProducer);
            producers[i].setName("Producer-" + (i + 1));
            producers[i].start();
        }

        // Crear y arrancar hilos consumidores
        Thread[] consumers = new Thread[CONSUMER_COUNT];
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumers[i] = new ConsumerThread(queue, consumoMap);
            consumers[i].setName("Consumer-" + (i + 1));
            consumoMap.put(consumers[i], 0); // inicializar conteo en cero
            consumers[i].start();
        }

        // Medir tiempo total de procesamiento
        long startTime = System.nanoTime();

        // Esperar a que terminen todos los productores
        for (Thread p : producers) {
            try {
                p.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Una vez que todos productores terminaron, insertar POISON_PILL para cada consumidor
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            try {
                queue.put(POISON_PILL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Esperar a que terminen todos los consumidores
        for (Thread c : consumers) {
            try {
                c.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        long endTime = System.nanoTime();
        System.out.println("Tiempo total de procesamiento: " 
                + ((endTime - startTime) / 1_000_000) + " ms");

        // Mostrar cuantos elementos consumio cada hilo consumidor
        System.out.println("Consumo por cada consumidor:");
        for (Thread c : consumers) {
            int count = consumoMap.getOrDefault(c, 0);
            System.out.println(c.getName() + " consumio " + count + " elementos.");
        }
    }

    // Clase productor que genera numeros aleatorios y los coloca en la cola
    static class ProducerThread extends Thread {
        private final BlockingQueue<Integer> queue;
        private final int itemsToProduce;
        private final Random rand = new Random();

        public ProducerThread(BlockingQueue<Integer> queue, int itemsToProduce) {
            this.queue = queue;
            this.itemsToProduce = itemsToProduce;
        }

        @Override
        public void run() {
            for (int i = 0; i < itemsToProduce; i++) {
                int numero = rand.nextInt(100);
                try {
                    queue.put(numero); // bloquea si la cola esta llena
                    // System.out.println(getName() + " produjo: " + numero);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    // Clase consumidor que extrae numeros y los procesa (aqui sumamos al conteo)
    static class ConsumerThread extends Thread {
        private final BlockingQueue<Integer> queue;
        private final ConcurrentHashMap<Thread, Integer> consumoMap;

        public ConsumerThread(BlockingQueue<Integer> queue, ConcurrentHashMap<Thread, Integer> consumoMap) {
            this.queue = queue;
            this.consumoMap = consumoMap;
        }

        @Override
        public void run() {
            int sumaLocal = 0;
            while (true) {
                int valor;
                try {
                    valor = queue.take(); // bloquea si la cola esta vacia
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                // Si es POISON_PILL, terminamos el bucle
                if (valor == POISON_PILL) {
                    break;
                }

                // Procesar el numero (en este caso, simplemente incrementamos un contador)
                sumaLocal++;
            }
            // Al terminar, registrar cuantos elementos consumo este hilo
            consumoMap.put(this, sumaLocal);
        }
    }
}
