# Actividad 5.5: Diagrama de clases y generación de código para sistema de pedidos

## Diagrama de clases UML
![Diagrama de clases UML]()

**Explicación del diseño:**
1. **Clases principales**:
    - **`Cliente`**: Almacena datos del cliente y sus pedidos.
    - **`Pedido`**: Contiene información del pedido (fecha, estado, productos, pagos) y lógica para calcular costes.
    - **`Producto`**: Detalles del producto (nombre, precio, stock, impuestos).
    - **`LineaPedido`**: Relación entre `Pedido` y `Producto` con la cantidad solicitada.
    - **`Pago`**: Clase abstracta con subclases para cada método de pago (`Tarjeta`, `Efectivo`, `Cheque`).
    - **`EstadoPedido`**: Enum para los estados del pedido (`pdte`, `pgdo`, `pcdo`, `envdo`, `entgdo`).

2. **Relaciones**:
    - Un `Cliente` realiza múltiples `Pedidos` (agregación).
    - Un `Pedido` contiene múltiples `LineaPedido` (composición).
    - `LineaPedido` asocia un `Producto`.
    - Un `Pedido` puede tener múltiples `Pagos`.

---

### a) Elementos UML y su relación con POO
| **Concepto UML**       | **Equivalente en POO (Kotlin)**                     |
|-------------------------|----------------------------------------------------|
| Clase                   | `class` / `data class`                             |
| Herencia                | `sealed class` (para `Pago`)                       |
| Asociación              | Referencias entre objetos (ej: `Pedido` → `Cliente`) |
| Agregación              | Listas de objetos (ej: `Cliente` tiene `Pedidos`)  |
| Composición             | Objetos creados y destruidos con el contenedor     |
| Enum                    | `enum class` (para `EstadoPedido`)                 |
| Atributos/Métodos       | Propiedades y funciones en clases                  |

---

### b) Herramienta utilizada para el diagrama UML
**Herramienta elegida: Lucidchart**
- **Razones**:
    - Interfaz intuitiva con arrastrar y soltar.
    - Integración con IA para sugerencias de diseño.
    - Colaboración en tiempo real (útil para revisiones).
    - Exportación en múltiples formatos (PNG, PDF).

**Comparación con GitMind**:
- GitMind es más limitado en personalización de elementos UML.
- Lucidchart ofrece más plantillas específicas para diagramas de clases.

---

### c) Conversión del diagrama UML a código Kotlin

**Esquema del código**:

```kotlin
// ENUMS
enum class EstadoPedido { PDTE, PGDO, PCDO, ENVDO, ENTGDO }

// CLASES PRINCIPALES
data class Cliente(
    val id: String,
    val nombre: String,
    val pedidos: MutableList<Pedido> = mutableListOf()
)

data class Producto(
    val id: String,
    val nombre: String,
    val precio: Double,
    val impuesto: Double,
    var stock: Int
)

// COMPOSICIÓN Y ASOCIACIONES
class LineaPedido(
    val producto: Producto,
    val cantidad: Int
) {
    fun subtotal(): Double = cantidad * (producto.precio * (1 + producto.impuesto))
}

class Pedido(
    val cliente: Cliente,
    val fecha: LocalDate,
    val lineas: MutableList<LineaPedido> = mutableListOf(),
    var estado: EstadoPedido = EstadoPedido.PDTE
) {
    private val pagos: MutableList<Pago> = mutableListOf()

    fun calcularTotal(): Double = lineas.sumOf { it.subtotal() }

    fun agregarPago(pago: Pago) {
        pagos.add(pago)
        if (pagos.sumOf { it.monto } >= calcularTotal()) estado = EstadoPedido.PGDO
    }
}

// HERENCIA (SEALED CLASS)
sealed class Pago(val monto: Double, val fecha: LocalDate)

class Tarjeta(
    monto: Double,
    fecha: LocalDate,
    val numero: String,
    val expiracion: String,
    val tipo: String
) : Pago(monto, fecha)

class Efectivo(
    monto: Double,
    fecha: LocalDate,
    val moneda: String
) : Pago(monto, fecha)

class Cheque(
    monto: Double,
    fecha: LocalDate,
    val nombreBanco: String,
    val titular: String
) : Pago(monto, fecha)
```

**Decisiones clave**:
1. **`sealed class` para pagos**: Permite modelar métodos de pago con atributos específicos de forma segura.
2. **Cálculo de total**: Se realiza iterando sobre las `LineaPedido`, incluyendo impuestos.
3. **Gestión de estados**: El estado se actualiza automáticamente al registrar pagos.
4. **Stock en `Producto`**: Se deja como propiedad mutable para actualizarlo al procesar pedidos.

---

**Nota final**: Este esquema omite detalles como validaciones de stock o persistencia para simplificar, pero sería el punto de partida para implementar el sistema completo.