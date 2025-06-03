# Primer Parcial - ICC-303 - Programación Paralela y Concurrente

1. **Fecha de Entrega:** A completar en el tiempo designado durante el examen.
2. **Lenguaje de Programación:** Java 17 o superior
3. **Formato de Entrega:** Hacer un **fork** de este repositorio, trabajar en su propio fork y subir el código con las
   soluciones a GitHub.

## Instrucciones Generales

- El código debe ser de **producción original** del estudiante.
- **No se permite** copiar código de otras fuentes, sitios web o compañeros.
- Asegurarse de que el código compile y ejecute correctamente.
- El proyecto debe estar correctamente estructurado y documentado.

## Problema 1: Búsqueda Paralela en una Matriz

### Descripción

Implementar un programa que busque un número objetivo en una matriz grande utilizando **paralelismo con memoria
compartida**.

### Requerimientos

- Crear una matriz cuadrada de `1000 x 1000` y llenarla con valores aleatorios.
- Implementar la búsqueda secuencial y medir el tiempo.
- Dividir la matriz entre varios hilos (por filas o bloques).
- Cada hilo busca el número objetivo en su segmento.
- Detener todos los hilos en cuanto uno encuentre el valor (sin seguir buscando innecesariamente).
- Medir y comparar el tiempo de ejecución con la versión secuencial.

### Sugerencias

- Usa `AtomicBoolean` para indicar si ya se encontró el valor.
- Utiliza `System.nanoTime()` para medir los tiempos de ejecución.
-

### Archivos sugeridos

```java
public class ParallelMatrixSearch {

    private static final int MATRIX_SIZE = 1000;
    private static final int THREAD_COUNT = 4;
    private static final int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
    private static final int TARGET = 256; // Número a buscar

    public static void main(String[] args) {
        // Inicializar la matriz con valores aleatorios
        // fillMatrixRandom();

        // Medir el tiempo de ejecución de la búsqueda secuencial
        // long startTime = System.nanoTime();
        // sequentialSearch();
        // long endTime = System.nanoTime();
        // System.out.println("Tiempo búsqueda secuencial: " + ((endTime - startTime) / 1_000_000) + "ms");

        // Medir el tiempo de ejecución de la búsqueda paralela
        // startTime = System.nanoTime();
        // parallelSearch();
        // endTime = System.nanoTime();
        // System.out.println("Tiempo búsqueda paralela: " + ((endTime - startTime) / 1_000_000) + "ms");
    }

    private static void sequentialSearch() {
        // Implementar búsqueda secuencial
    }

    private static void parallelSearch() {
        // Implementar búsqueda paralela
        // Sugerencia: usar AtomicBoolean para indicar si ya se encontró el número y detener hilos
    }

    private static void fillMatrixRandom() {
        Random rand = new Random();
        for (int i = 0; i < MATRIX_SIZE; i++) {
            for (int j = 0; j < MATRIX_SIZE; j++) {
                matrix[i][j] = rand.nextInt(1000); // Rango arbitrario
            }
        }
    }
}
```

## Problema 2: Productor - Consumidor Concurrente

### Descripción

Implementar el patrón clásico de **Productor-Consumidor** usando una `BlockingQueue` en Java.

### Requerimientos

- Crear una cola bloqueante con capacidad limitada (`ArrayBlockingQueue`).
- Implementar `n` productores que generen números aleatorios y los coloquen en la cola.
- Implementar `n` consumidores que extraigan y procesen los números de la cola (ej. sumarlos).
- Ejecutar el sistema con múltiples productores y consumidores simultáneos.
- Medir el tiempo total de procesamiento.
- Mostrar cuántos elementos consumió cada hilo consumidor.

### Archivos sugeridos

```java
public class ProducerConsumer {
    private static final int QUEUE_CAPACITY = 10;
    private static final int PRODUCER_COUNT = 2;
    private static final int CONSUMER_COUNT = 2;
    private static final int PRODUCE_COUNT = 100;

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        // Sugerencia: Usar ExecutorService o crear threads manualmente para iniciar Productores y Consumidores
    }

    static class Producer implements Runnable {
        @Override
        public void run() {
            // Generar PRODUCE_COUNT números aleatorios y colocarlos en la cola
            // Sugerencia: usar Thread.sleep(10) para simular tiempo de producción
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            // Extraer elementos de la cola y procesarlos (ej: sumarlos)
            // Sugerencia: llevar la suma total por hilo y reportar al final
        }
    }
}

```

## Recomendaciones

- Utiliza `ExecutorService` o lanza hilos manualmente.
- Documenta tu código con comentarios claros.
- Si se desea medir carga por consumidor, puede usarse un `ConcurrentHashMap<Thread, Integer>`.

## Criterios de Evaluación

| Criterio                                    | P1 | P2 |
|---------------------------------------------|----|----|
| Implementación funcional                    | 20 | 20 |
| Uso correcto de hilos/colas                 | 20 | 20 |
| Sincronización eficiente                    | 20 | 20 |
| Medición y análisis de tiempos              | 10 | 10 |
| Claridad, estilo y documentación del código | 20 | 20 |
| Estructura del proyecto y uso de GitHub     | 10 | 10 |
| **Total**                                   

## Buenas prácticas

- Evita bloqueos innecesarios.
- No sobrecargues el sistema con muchos hilos.
- Usa colecciones e interfaces modernas de `java.util.concurrent`.
- Realiza pruebas antes de hacer el PR.

---

## Entrega

- Asegúrate de:
    - Tener todos los archivos necesarios en tu fork.
    - Subir una versión que compile sin errores.
    - No dejar archivos temporales ni sin usar.

---

¡Buena suerte y feliz codificación!
