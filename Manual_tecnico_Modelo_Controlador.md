# Manual técnico: Modelo y Controlador

Este documento complementa el [Manual de usuario](Manual_de_usuario.md). Describe con más detalle cómo están implementadas las operaciones en `Modelo/Grafo.java` y `Controlador/ControladorPrincipal.java`.

## 1. Responsabilidades

### Modelo (`Modelo.Grafo`)

El modelo administra los datos del grafo y contiene las reglas de las operaciones. Guarda nodos y conexiones, valida sus valores y calcula rutas. No conoce la ventana Swing ni lee campos de pantalla.

### Controlador (`Controlador.ControladorPrincipal`)

El controlador coordina la interfaz y el modelo. Conecta los botones a acciones, obtiene los valores de los campos correctos, invoca los métodos del modelo y presenta el resultado mediante la vista. También transforma errores de entrada en mensajes comprensibles para el usuario.

## 2. Modelo: `Grafo`

### Estructura interna

```java
private final Map<String, Map<String, Double>> adyacencia;
```

`adyacencia` es una lista de vecinos indexada por identificador de nodo. Cada nodo apunta a un mapa que contiene sus vecinos y el peso de la arista hacia cada uno. Por ejemplo:

```text
A → { B: 4.0, C: 2.0 }
B → { A: 4.0 }
C → { A: 2.0 }
```

La representación almacena ambas direcciones de cada conexión porque el grafo es no dirigido. `LinkedHashMap` conserva el orden de inserción, lo que ayuda a que los nodos se presenten de forma predecible.

### `agregarNodo(String id)`

Agrega un nodo al grafo.

1. Llama a `validarId` para rechazar `null` o una cadena vacía (incluida una compuesta solo por espacios).
2. Elimina espacios a los extremos con `trim()`.
3. Usa `computeIfAbsent` para crear su mapa de vecinos solo si el nodo todavía no existe.

Por ello, agregar un nodo existente no produce duplicados y no elimina las conexiones que ya tenga.

### `agregarArista(String origen, String destino, double peso)`

Agrega o actualiza una conexión entre dos nodos.

1. Normaliza los identificadores de origen y destino mediante `normalizar`.
2. Rechaza pesos negativos y valores no finitos (`NaN`, infinito positivo o negativo).
3. Se asegura de que los dos extremos existan llamando a `agregarNodo`.
4. Guarda el peso en los dos sentidos: origen → destino y destino → origen.

Si la conexión ya estaba guardada, `put` reemplaza su peso. El modelo no representa múltiples aristas entre el mismo par de nodos. Una conexión de un nodo consigo mismo se puede guardar, aunque no aporta una ruta útil para los algoritmos de camino simple.

**Observación sobre el mensaje de validación:** el código comprueba `peso < 0`, por lo que el valor cero es válido. El texto de excepción dice actualmente “número positivo”, aunque técnicamente cero también se acepta; se puede ajustar ese mensaje a “no negativo” para que sea exacto.

### `nodos()`

Devuelve el conjunto de identificadores guardados en el grafo como una vista no modificable del conjunto exterior. Se usa, por ejemplo, para actualizar el mensaje de estado y dibujar la lista de nodos.

### `aristas()`

Devuelve el mapa de adyacencia con una envoltura exterior no modificable. La vista lo recorre para dibujar las líneas y mostrar los pesos. Los métodos de cálculo leen la misma estructura interna.

### `caminoMasCorto(String origen, String destino)`

Implementa el algoritmo de Dijkstra y devuelve un objeto `Resultado`.

1. Llama a `comprobarNodos`; si los identificadores están vacíos o no existen, lanza `IllegalArgumentException`.
2. Crea `dist`, que registra la mejor distancia conocida desde el origen. Inicializa todos los nodos a infinito y el origen a cero.
3. Crea `prev`, que guarda el nodo anterior en la mejor ruta conocida a cada nodo.
4. Inserta el origen en una cola de prioridad que ordena los nodos por su distancia conocida.
5. Extrae el nodo de menor distancia. Para cada vecino, calcula `distancia actual + peso de la arista`.
6. Si esa suma mejora la distancia conocida, actualiza `dist`, registra el nodo actual como predecesor y vuelve a insertar al vecino en la cola.
7. Cuando se llega al destino, o se agota la cola, termina. Si el destino conserva distancia infinita, no hay ruta.
8. Si sí hay ruta, llama a `reconstruir` para recuperar la secuencia de nodos a partir de `prev`.

Dijkstra requiere pesos no negativos. La validación de `agregarArista` mantiene esa condición para los datos incorporados por la interfaz.

### `caminoMasLargo(String origen, String destino)`

Busca el camino simple de mayor suma de pesos. “Simple” significa que un nodo no puede repetirse dentro de la misma ruta. Este límite es necesario: como el grafo no dirigido puede contener ciclos, si se permitiera repetir nodos, se podrían recorrer ciclos una y otra vez.

1. Valida que origen y destino existan.
2. Inicializa un acumulador `Mejor` para conservar la mejor distancia y ruta encontradas.
3. Llama a `buscarLargo` desde el origen con una ruta vacía, un conjunto de nodos visitados vacío y distancia cero.
4. Devuelve una ruta inalcanzable si la búsqueda no encontró ninguna; en caso contrario devuelve la ruta de mayor peso.

La búsqueda enumera rutas posibles y puede crecer exponencialmente conforme aumenta el tamaño y la conectividad del grafo. Es apropiada para ejemplos pequeños; no tiene el mismo rendimiento que Dijkstra.

### `buscarLargo(...)`

Es el recorrido recursivo que implementa la búsqueda exhaustiva del camino más largo. Sus parámetros son:

- `actual`: nodo en el que está la búsqueda.
- `destino`: nodo que se intenta alcanzar.
- `vistos`: nodos ya presentes en la ruta actual; impide ciclos.
- `camino`: ruta parcial en construcción.
- `distancia`: suma de pesos de la ruta parcial.
- `mejor`: objeto compartido para guardar el mejor resultado.

Agrega el nodo actual a `vistos` y `camino`. Si es el destino, compara la distancia actual con la mejor guardada. Si no lo es, explora recursivamente cada vecino que todavía no esté visitado. Al terminar esa rama, quita el nodo actual de la ruta y de `vistos`; este retroceso permite explorar otras alternativas.

### `comprobarNodos(String a, String b)`

Normaliza los dos identificadores y verifica que ambos estén registrados en `adyacencia`. Si falta alguno, lanza `IllegalArgumentException`. No verifica que estén conectados entre sí; esa conclusión corresponde al algoritmo de búsqueda.

### `reconstruir(String a, String b, Map<String, String> prev)`

Reconstruye una ruta de origen a destino después de Dijkstra. Empieza en el destino y sigue los predecesores guardados en `prev` hasta llegar al origen, insertando cada nodo al principio de una lista enlazada. El resultado queda ordenado desde el origen hacia el destino.

### `validarId(String id)` y `normalizar(String id)`

- `validarId` rechaza valores nulos o vacíos después de quitar espacios de los extremos.
- `normalizar` valida y devuelve el identificador recortado.

Son métodos auxiliares estáticos para aplicar la misma regla a nodos y extremos de aristas.

### Clase interna `Resultado`

Agrupa los datos producidos por una búsqueda:

- `camino`: copia inmutable de la secuencia de nodos.
- `distancia`: suma de los pesos de las aristas del camino.
- `camino()`: devuelve la secuencia.
- `distancia()`: devuelve la distancia total.
- `alcanzable()`: devuelve `true` si la ruta no está vacía.
- `inalcanzable()`: método privado que construye el resultado de una búsqueda sin ruta; usa una lista vacía e infinito como distancia.

Cuando origen y destino son el mismo nodo, Dijkstra devuelve una ruta que contiene ese nodo y distancia cero. Por tanto, `alcanzable()` es verdadero.

## 3. Controlador: `ControladorPrincipal`

### Atributos

```java
private final Grafo grafo = new Grafo();
private final VentanaPrincipal vista = new VentanaPrincipal();
```

El controlador mantiene una instancia del modelo durante la ejecución de la aplicación y una instancia de la ventana. Así, cada acción del usuario opera sobre el mismo grafo en memoria.

### Constructor `ControladorPrincipal()`

Registra los `ActionListener` de los botones. Cada listener envuelve la operación dentro de `ejecutar`, para que errores de validación y conversión se manejen con el mismo mecanismo.

#### Acción del botón `Agregar nodo`

1. Lee el campo `vista.nuevoNodo()`.
2. Llama a `grafo.agregarNodo(...)`.
3. Solicita redibujar el grafo sin una ruta resaltada mediante `vista.dibujarGrafo(grafo, List.of())`.
4. Muestra el conjunto de nodos con `vista.mensaje(...)`.

El campo usado aquí es el de **Nuevo nodo**; no es uno de los campos de extremos de una conexión.

#### Acción del botón `Agregar camino`

1. Lee `vista.edgeOrigen()` para el extremo **Desde**.
2. Lee `vista.edgeDestino()` para el extremo **Hasta**.
3. Lee `vista.distanciaArista()` y convierte el texto a `double` con `Double.parseDouble`.
4. Llama a `grafo.agregarArista(origen, destino, distancia)`.
5. Redibuja el grafo sin una ruta seleccionada.
6. Confirma en el mensaje el par de extremos, la distancia y los nodos actuales.

Si la distancia no se puede convertir a número, `Double.parseDouble` genera una excepción que `ejecutar` presenta como aviso. En la interfaz actual, los decimales se ingresan con punto, por ejemplo `2.5`.

#### Acciones de búsqueda

Los botones **Camino más corto** y **Camino más largo** llaman a `resolverGrafo(false)` y `resolverGrafo(true)`, respectivamente. Ambos usan los campos de búsqueda `vista.grafoOrigen()` y `vista.grafoDestino()`; no usan los campos **Desde** y **Hasta** del formulario de conexión.

### `iniciar()`

Hace visible la ventana llamando a `vista.setVisible(true)`. `Main` crea el controlador dentro del hilo de eventos de Swing y llama a este método, que es el punto de entrada para mostrar la aplicación.

### `resolverGrafo(boolean largo)`

Coordina el cálculo y presentación de una ruta:

1. Lee origen y destino de los campos inferiores de búsqueda.
2. Si `largo` es `true`, llama a `grafo.caminoMasLargo`; si es `false`, llama a `grafo.caminoMasCorto`.
3. Pasa la ruta resultante a `vista.dibujarGrafo` para resaltarla.
4. Si el resultado es alcanzable, compone un mensaje con el tipo de búsqueda, los nodos de la ruta separados por flechas y su distancia total.
5. Si no existe ruta, muestra el mensaje correspondiente.

La función no implementa los algoritmos; delega el cálculo al modelo.

### `ejecutar(Runnable accion)`

Ejecuta una acción dentro de un bloque `try/catch`. Si ocurre una excepción, presenta su mensaje en un cuadro de diálogo Swing titulado “Dato inválido”. Se usa para capturar, entre otros, identificadores vacíos, nodos que no existen y distancias mal escritas o negativas.

## 4. Flujo completo: registrar y buscar

Al crear una conexión `A`–`B` con distancia `4`:

```text
Vista: captura Desde=A, Hasta=B, Distancia=4
  → Controlador: lee esos tres campos y convierte 4 a double
  → Modelo: valida extremos y peso, crea A/B si faltan y guarda A↔B con peso 4
  → Controlador: pide actualizar la vista
  → Vista: vuelve a dibujar los nodos, la línea y su etiqueta 4
```

Al buscar el camino más corto entre `A` y `D`:

```text
Vista: captura Origen de búsqueda=A y Destino=D
  → Controlador: llama a caminoMasCorto(A, D)
  → Modelo: ejecuta Dijkstra y devuelve Resultado(camino, distancia)
  → Controlador: solicita dibujar la ruta y prepara el texto
  → Vista: resalta la ruta y presenta los nodos y la distancia total
```

## 5. Resumen de métodos por clase

| Clase | Método | Responsabilidad |
| --- | --- | --- |
| `Grafo` | `agregarNodo` | Valida y registra un nodo único. |
| `Grafo` | `agregarArista` | Valida y guarda una conexión no dirigida con peso. |
| `Grafo` | `nodos` | Expone los identificadores de nodos. |
| `Grafo` | `aristas` | Expone la estructura de conexiones para su lectura y dibujo. |
| `Grafo` | `caminoMasCorto` | Calcula la ruta mínima con Dijkstra. |
| `Grafo` | `caminoMasLargo` | Inicia la búsqueda del camino simple máximo. |
| `Grafo` | `buscarLargo` | Explora recursivamente las rutas simples posibles. |
| `Grafo` | `comprobarNodos` | Valida que origen y destino estén registrados. |
| `Grafo` | `reconstruir` | Reconstruye el camino a partir de predecesores. |
| `Grafo` | `validarId`, `normalizar` | Valida y recorta identificadores. |
| `Resultado` | `camino`, `distancia`, `alcanzable` | Permite consultar los datos de la búsqueda. |
| `ControladorPrincipal` | Constructor | Conecta eventos de botones con acciones. |
| `ControladorPrincipal` | `iniciar` | Muestra la ventana. |
| `ControladorPrincipal` | `resolverGrafo` | Coordina consultas de ruta y actualiza la vista. |
| `ControladorPrincipal` | `ejecutar` | Captura errores y muestra avisos. |

## 6. Consideraciones técnicas

- Los datos viven en memoria: cerrar la aplicación elimina el grafo actual.
- Dijkstra solo es correcto para pesos no negativos; el modelo valida esta condición al insertar aristas.
- El camino más largo se define aquí como **camino simple de máxima distancia**, no como cualquier recorrido que pueda repetir ciclos.
- La enumeración exhaustiva de caminos simples puede ser costosa en grafos grandes o densos.
- El modelo evita los componentes de Swing, de modo que sus algoritmos pueden reutilizarse desde otra interfaz o desde código independiente.
