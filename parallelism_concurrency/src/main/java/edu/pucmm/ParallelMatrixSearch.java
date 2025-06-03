package edu.pucmm;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author me@fredpena.dev
 * @created 02/06/2025 - 20:46
 */
public class ParallelMatrixSearch {

    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_COUNT = 4;
    private static final int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
    private static final int TARGET = 1;
    private static final AtomicBoolean found = new AtomicBoolean(false);

    public static void main(String[] args) {
        // Inicializar la matriz con valores aleatorios
        fillMatrixRandom();

        // Medir el tiempo de ejecución de la búsqueda secuencial
        System.out.println("Buscando en secuencial");
        long startTime = System.nanoTime();
        sequentialSearch();
        long endTime = System.nanoTime();
        System.out.println("Tiempo búsqueda secuencial: " + ((endTime - startTime) / 1_000_000) + "ms");

        // Medir el tiempo de ejecución de la búsqueda paralela
        System.out.println("Buscando en paralelo");
        startTime = System.nanoTime();
        boolean encontradoParalelo = parallelSearch();
        endTime = System.nanoTime();
        System.out.println("Tiempo búsqueda paralela: " + ((endTime - startTime) / 1_000_000) + "ms");
    }

    // Recorre cada fila y columna de la matriz hasta encontrar el objetivo
    private static void sequentialSearch() {
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                if (matrix[i][j] == TARGET) {
                    System.out.println("Número encontrado en la posición: (" + i + ", " + j + ")");
                    return;
                }
            }
        }
    }

    // Divide la matriz entre varios hilos los cuales buscan el número objetivo por
    // sus filas asignadas, si un hilo encuentra el número target entonces los demás
    // se detienen
    private static boolean parallelSearch() {
        Thread[] threads = new Thread[THREAD_COUNT];
        int rowsPerThread = MATRIX_SIZE / THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            int startRow = i * rowsPerThread;
            int endRow = (i == THREAD_COUNT - 1) ? MATRIX_SIZE : (startRow + rowsPerThread);
            threads[i] = new SearchThread(matrix, startRow, endRow, found);
            threads[i].start();
        }

        // Esperar a que terminen todos los hilos
        for (int i = 0; i < THREAD_COUNT; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Devolver si alguno lo encontró
        return found.get();
    }

    // Clase hilo que busca el target en el rango de filas proporcionado
    static class SearchThread extends Thread {
        private final int[][] matrix;
        private final int startRow;
        private final int endRow;
        private final AtomicBoolean found;

        public SearchThread(int[][] matrix, int startRow, int endRow, AtomicBoolean found) {
            this.matrix = matrix;
            this.startRow = startRow;
            this.endRow = endRow;
            this.found = found;
            this.setName("SearchThread-" + startRow + "-" + endRow);
        }

        @Override
        public void run() {
            for (int i = startRow; i < endRow && !found.get(); i++) {
                for (int j = 0; j < MATRIX_SIZE && !found.get(); j++) {
                    if (matrix[i][j] == TARGET) {
                        // Solo el primer hilo que cambie found de false a true imprime
                        if (found.compareAndSet(false, true)) {
                            System.out.println("Número encontrado en la posición: ("
                                    + i + ", " + j + ") por " + getName());
                        }
                        return;
                    }
                }
            }
        }
    }

    // Llena la matris con valores al azar
    private static void fillMatrixRandom() {
        Random rand = new Random();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                matrix[i][j] = rand.nextInt(1000);
            }
        }
    }
}
