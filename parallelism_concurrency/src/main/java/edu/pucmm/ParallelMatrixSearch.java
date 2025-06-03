package edu.pucmm;

import java.util.Random;

/**
 * @author me@fredpena.dev
 * @created 02/06/2025 - 20:46
 */
public class ParallelMatrixSearch {

    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_COUNT = 4;
    private static final int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
    private static final int TARGET = 1;
    private static volatile Boolean found = false;

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
        parallelSearch();
        endTime = System.nanoTime();
        System.out.println("Tiempo búsqueda paralela: " + ((endTime - startTime) / 1_000_000) + "ms");
    }

    // Recorre cada fila y columan de la matriz hasta encontrar el objetivo
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

    // Divide la matriz entre varios hilos los cuales buscan el numero objetivo por
    // sus filas asignadas, si un hilo encuentra el numero target entonces los demas
    // se detienen
    private static void parallelSearch() {
        Thread[] thread = new Thread[THREAD_COUNT];
        int rowByThread = MATRIX_SIZE / THREAD_COUNT;
        for (int i = 0; i < THREAD_COUNT; i++) {
            int start = i * rowByThread;
            int end;

            if (i == THREAD_COUNT - 1)
                end = MATRIX_SIZE;
            else
                end = start + rowByThread;

            System.out.println("Iniciando hilo " + i + " para filas: " + start + " a " + end);
            thread[i] = new SearchThread(matrix, start, end, found);
            thread[i].start();
        }
    }

    static class SearchThread extends Thread {
        private final int[][] matrix;
        private final int startRow;
        private final int endRow;
        public volatile boolean found;

        public SearchThread(int[][] matrix, int startRow, int endRow, boolean found) {
            this.matrix = matrix;
            this.startRow = startRow;
            this.endRow = endRow;
            this.found = found;
            this.setName("SearchThread-" + startRow + "-" + endRow);
        }

        @Override
        public void run() {
            for (int i = startRow; i < endRow && !found; i++) {
                for (int j = 0; j < MATRIX_SIZE && !found; j++) {
                    if (matrix[i][j] == TARGET) {
                        System.out.println("Número encontrado en la posición: (" + i + ", " + j + ")");
                        System.out.println("Hilo " + this.getName() + " encontró el número.");
                        return;
                    }
                }
            }
        }
    }

    private static void fillMatrixRandom() {
        Random rand = new Random();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                matrix[i][j] = rand.nextInt(1000);
            }
        }
    }
}