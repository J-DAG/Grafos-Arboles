# Manual de usuario: Explorador de grafos

## 1. Introducción

Esta aplicación permite construir y visualizar un **grafo ponderado no dirigido**. Cada nodo representa un punto y cada camino conecta dos puntos con una distancia numérica. Una vez configurado el grafo, puedes consultar el camino de menor distancia o el camino simple de mayor distancia entre dos nodos.

La aplicación abre directamente la ventana **Explorador de grafos**. El módulo de árboles y la pantalla de menú fueron retirados; todas las operaciones actuales corresponden al grafo.

## 2. Elementos de la ventana

La ventana se divide en tres zonas:

1. **Formulario superior:** sirve para crear nodos y caminos.
2. **Área central:** dibuja los nodos, las conexiones y el valor de distancia de cada camino.
3. **Zona inferior:** permite seleccionar origen y destino para una búsqueda y muestra su resultado.

Los campos para crear una conexión y los campos de búsqueda son independientes:

| Campo o control | Uso |
| --- | --- |
| **Nuevo nodo** | Nombre del nodo que se agregará al grafo. |
| **Agregar nodo** | Guarda el nodo indicado en el campo anterior. |
| **Desde** | Nodo inicial de una conexión que se va a crear. |
| **Hasta** | Nodo final de esa conexión. |
| **Distancia** | Peso numérico de la conexión entre los dos nodos. |
| **Agregar camino** | Agrega o actualiza la conexión con la distancia indicada. |
| **Origen de búsqueda** | Punto inicial para calcular una ruta. |
| **Destino** | Punto final para calcular una ruta. |
| **Camino más corto** | Calcula la ruta de menor distancia mediante Dijkstra. |
| **Camino más largo** | Busca la ruta simple de mayor distancia entre los dos puntos. |

## 3. Crear los nodos

1. Escribe un identificador en **Nuevo nodo**. Por ejemplo: `A`.
2. Pulsa **Agregar nodo**.
3. Repite los pasos para los demás puntos: `B`, `C`, `D`, etc.

El identificador no puede estar vacío. Si agregas un nodo que ya existe, no se crea una copia: el grafo conserva un solo nodo con ese identificador. Se eliminan los espacios al principio y al final del nombre, por lo que ` A ` se guarda como `A`.

Los nodos también se crean automáticamente al agregar un camino. Por ejemplo, si agregas una conexión desde `A` hasta `B` sin haber creado antes esos nodos, ambos aparecerán en el grafo. Agregarlos explícitamente primero puede ayudar a organizar la configuración y visualizar los puntos aislados (sin conexiones).

## 4. Crear caminos y asignar distancias

Para conectar dos nodos:

1. En **Desde**, escribe el identificador del primer nodo.
2. En **Hasta**, escribe el identificador del otro nodo.
3. En **Distancia**, escribe el peso numérico de esa conexión. Se admiten números enteros o decimales, por ejemplo `8` o `2.5`.
4. Pulsa **Agregar camino**.

La conexión aparecerá como una línea entre los nodos. La distancia asignada se muestra junto a la línea. El mensaje inferior confirma los extremos y el valor guardado.

### Propiedades de las conexiones

- Las conexiones son **no dirigidas**. Una conexión `A`–`B` también permite viajar de `B` a `A` con la misma distancia.
- La distancia debe ser un número finito y **no negativo**. Valores negativos, vacíos o no numéricos se rechazan y la aplicación muestra un aviso.
- Si vuelves a agregar un camino entre los mismos dos nodos, su distancia anterior se reemplaza por la nueva. No se crean conexiones paralelas.
- Se permite distancia `0`.
- Un nodo puede permanecer aislado; en ese caso no existe ruta hacia otros nodos hasta que agregues caminos que lo conecten.

## 5. Ejemplo de configuración

Agrega estas conexiones usando los campos **Desde**, **Hasta** y **Distancia**:

| Desde | Hasta | Distancia |
| --- | --- | ---: |
| A | B | 4 |
| A | C | 2 |
| C | B | 1 |
| B | D | 5 |
| C | D | 8 |

El dibujo mostrará los cuatro nodos y cada camino con su distancia. La conexión C–B tiene distancia 1, así que la ruta A–C–B–D mide `2 + 1 + 5 = 8`.

## 6. Consultar el camino más corto

1. En **Origen de búsqueda**, escribe el nodo desde el que quieres partir. Para el ejemplo, escribe `A`.
2. En **Destino**, escribe el punto al que quieres llegar. Para el ejemplo, escribe `D`.
3. Pulsa **Camino más corto**.

La aplicación calcula el camino de menor suma de distancias mediante el algoritmo de Dijkstra. Para el ejemplo, una ruta mínima es `A → C → B → D`, con distancia total `8`.

El resultado aparece en la zona inferior y el dibujo resalta la ruta: las conexiones elegidas se muestran en rojo y los nodos recorridos en amarillo. Las demás conexiones continúan visibles para que puedas comparar las alternativas.

### ¿Qué significa la distancia total?

La distancia total es la suma de los pesos de las conexiones usadas. Si la ruta es `A → C → B → D`, se suman A–C, C–B y B–D. Los nodos no agregan distancia por sí mismos; solo cuentan los pesos de sus conexiones.

## 7. Consultar el camino más largo

Escribe el origen y el destino en los mismos campos de búsqueda y pulsa **Camino más largo**. La aplicación busca la ruta simple de mayor distancia entre esos puntos; una ruta simple no repite nodos. El resultado y el resaltado se muestran igual que para el camino más corto.

La búsqueda exhaustiva del camino más largo puede tardar bastante cuando el grafo tiene muchos caminos posibles. Para grafos pequeños o medianos de práctica, es útil para comparar rutas.

## 8. Cómo se conectan las partes de la aplicación (MVC)

El código está organizado con el patrón **Modelo–Vista–Controlador (MVC)**. Cada parte tiene una responsabilidad diferente:

### Vista: `Vista/VentanaPrincipal.java`

Construye la ventana Swing, presenta los campos y botones, captura las acciones del usuario y dibuja el grafo. La vista distingue visualmente entre el formulario para crear nodos/caminos y los campos para buscar una ruta. No decide qué ruta es la más corta; solicita esa operación al controlador.

### Controlador: `Controlador/ControladorPrincipal.java`

Conecta los botones de la vista con las operaciones del modelo. Cuando pulsas **Agregar camino**, lee específicamente **Desde**, **Hasta** y **Distancia**, convierte la distancia a número y solicita al modelo que guarde la conexión. Cuando pulsas uno de los botones de búsqueda, toma el origen y el destino de la sección de consulta, llama al algoritmo correspondiente, y entrega el resultado a la vista para mostrarlo y dibujarlo. Si los datos no son válidos, presenta un mensaje de aviso.

### Modelo: `Modelo/Grafo.java`

Guarda los nodos y las conexiones con sus distancias. Valida los datos, implementa Dijkstra para el camino más corto y busca caminos simples para calcular el camino más largo. Devuelve la secuencia de nodos y la distancia total; la presentación de esos datos corresponde a la vista.

### Flujo de una operación

```text
Usuario → Vista (campos y botón) → Controlador → Modelo (grafo/algoritmo)
       ← Vista (mensaje y dibujo) ← Controlador ← Resultado
```

Por ejemplo, al pulsar **Camino más corto**, la vista permite ingresar los extremos y recibe el resultado visual. El controlador lee esos extremos y coordina la consulta. El modelo calcula la ruta y su distancia. Finalmente, el controlador envía la ruta a la vista, que presenta el total y la resalta en el dibujo.

## 9. Mensajes y situaciones frecuentes

- **“El nodo no puede estar vacío”**: completa el campo del nodo, origen o destino correspondiente.
- **“El peso debe ser un número finito no negativo”**: revisa el valor de **Distancia**; no uses letras, infinito ni números negativos.
- **“El origen y el destino deben existir en el grafo”**: agrega ambos nodos o al menos una conexión que los cree antes de buscarlos.
- **“No existe un camino entre esos nodos”**: los nodos existen, pero no hay conexiones que unan sus componentes.
- Si origen y destino son el mismo nodo, la ruta tiene distancia cero.

## 10. Recomendaciones de uso

1. Usa nombres cortos y consistentes para los nodos (`A`, `B`, `Centro`, `Terminal`).
2. Revisa los tres campos de conexión antes de pulsar **Agregar camino**: **Desde**, **Hasta** y **Distancia**.
3. Comprueba los valores dibujados sobre las líneas antes de buscar rutas.
4. Verifica que el **Origen de búsqueda** y el **Destino** estén escritos en los campos inferiores, que son distintos de los campos usados para crear conexiones.
5. Compara el camino mostrado con las distancias de sus conexiones para entender el total calculado.

## 11. Alcance y conservación de datos

El grafo se mantiene en memoria mientras la aplicación está abierta. Actualmente no hay controles para guardar o cargar una configuración en un archivo; al cerrar la aplicación, la configuración se pierde. Para conservarla, anota los nodos y conexiones antes de cerrar.
