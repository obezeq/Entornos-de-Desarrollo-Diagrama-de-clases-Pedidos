import java.time.LocalDate

// CLASE DE ENUMARICÓN PARA EL ESTADO DEL PEDIDO
enum class EstadoPedido { PDTE, PGDO, PCDO, ENVDO, ENTGDO }

// CLASE DE PRODUCTO DATA CLASS.
data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val impuestos: Double,
    var stock: Int
) {

    fun actualizarStock(cantidad: Int) {
        require(stock + cantidad >= 0) { "Stock no puede ser negativo" }
        stock += cantidad
    }
}

// CLASE DE CLIENTE
data class Cliente(
    val id: String,
    val nombre: String,
    val direccion: String,
    private val pedidos: MutableList<Pedido> = mutableListOf()  // Privado para encapsulación
) {
    fun listarPedidos(): List<Pedido> = pedidos.toList()
}

// CLASE PARA EL PEDIDO, Y TE ACTUALIZA EL ESTADO MANUALMENTE CON EL METODO INCLUDIDO Y AGREGAR PAGO
class Pedido(
    val id: String,
    val cliente: Cliente,
    val fecha: LocalDate,
    val lineas: MutableList<LineaPedido> = mutableListOf(),
    var estado: EstadoPedido = EstadoPedido.PDTE
) {
    private val pagos: MutableList<Pago> = mutableListOf()

    fun calcularCosteTotal(): Double = lineas.sumOf { it.subtotal() }

    fun actualizarEstado(nuevoEstado: EstadoPedido) {
        estado = nuevoEstado
    }

    fun agregarPago(pago: Pago) {
        pagos.add(pago)
        if (pagos.sumOf { it.cantidad } >= calcularCosteTotal()) {
            estado = EstadoPedido.PGDO
        }
    }
}

// CLASE PARA EL PAGO, QUE ES UNA CLASE DE TIPO HERENCIA "sealed" PARA CADA TIPO DE PAGOO.
sealed class Pago(val cantidad: Double, val fechaPago: LocalDate)

class Tarjeta(
    cantidad: Double,
    fechaPago: LocalDate,
    val numero: String,
    val fechaCaducidad: LocalDate,
    val tipoTarjeta: String
) : Pago(cantidad, fechaPago)

class Efectivo(
    cantidad: Double,
    fechaPago: LocalDate,
    val tipoMoneda: String
) : Pago(cantidad, fechaPago)

class Cheque(
    cantidad: Double,
    fechaPago: LocalDate,
    val nombreBanco: String,
    val titular: String
) : Pago(cantidad, fechaPago)

// CLASE PARA LA LINEA DEL PEDIDO, Y MANTENEMOS LA LÓGICA DLE CALCULO
class LineaPedido(
    val producto: Producto,
    val cantidad: Int
) {
    fun subtotal(): Double = cantidad * (producto.precio * (1 + producto.impuestos))
}

fun main() {

    val producto = Producto(
        "qclvbQs69hhaCmImXBlFOw==",
        "Laptop",
        "Portátil de 15 pulgadas",
        999.99,
        0.21,
        10
    )

    val cliente = Cliente("C1", "Ana", "Calle Principal 123")

    val pedido = Pedido("P1", cliente, LocalDate.now())
    pedido.lineas.add(LineaPedido(producto, 2))

    println("Total pedido: ${pedido.calcularCosteTotal()}") /* OUTPUT ESPERADO: 'Total pedido: 2419.9758' */
}