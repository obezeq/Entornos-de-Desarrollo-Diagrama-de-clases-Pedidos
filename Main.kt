import java.time.LocalDate;

// CLASE TIPO ENUMERADA ("enum")
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

fun main() {

}