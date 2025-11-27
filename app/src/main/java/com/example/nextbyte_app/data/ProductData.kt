package com.example.nextbyte_app.data
import com.example.nextbyte_app.R
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val imageResId: Int, // La imagen grande del producto
    var isFavorited: Boolean = false
)

// Tu "base de datos" de productos.
val allProducts = listOf(
    Product(101
        , "Ajazz AK820" //Nombre Producto
        , "Su construcción integra 5 capas de aislamiento acústico " +
                "(algodón PORON, IXPE, PET, esponja y silicona)," +
                " ofreciendo una experiencia de tecleo ultra silenciosa sin sacrificar la respuesta mecánica," +
                " ideal para sesiones intensas de gaming o programación nocturna.." //Descripcion
        , 39999 //Valor
        , R.drawable.promo1), //Imagen.

    Product(205, "HyperX Cloud III - Auriculares Gaming\""
        , "Los HyperX Cloud III son la evolución de nuestros legendarios Cloud II, que son reconocidos por su confort, calidad de sonido y durabilidad. Con la espuma viscoelástica de calidad característica de HyperX en las almohadillas de la diadema y de las orejas," +
                " ofrece un ajuste cómodo perfecto para largas sesiones de juego." +
                " También cuenta con unos nuevos transductores angulares recalibrados de 53 mm para una experiencia de escucha óptima." +
                " El micrófono mejorado de 10 mm captura con gran claridad las conversaciones dentro del juego y las llamadas. " +
                "Los sencillos controles del casco de los auriculares te ofrecen un acceso rápido y directo para silenciar el micrófono o ajustar el volumen." +
                " El indicador LED de silencio del micrófono muestra claramente si tienes el micrófono silenciado. " +
                "Compatibles con PC, PS5, PS4, Xbox Series X|S, Xbox One, Nintendo Switch, Mac y dispositivos móviles." +
                " Disfruta de una comodidad y un sonido inigualables en tus plataformas favoritas."
        , 79999
        , R.drawable.promo2),

    Product(312,
        "Monitor Gamer Xiaomi G34WQi"
        , "El Monitor Gamer Xiaomi G34WQi es una pantalla curva de 34 pulgadas diseñada para ofrecer una experiencia visual inmersiva y de alto rendimiento," +
                " especialmente orientada a jugadores y usuarios que requieren fluidez y calidad de imagen superior. " +
                "Su resolución WQHD (3440 x 1440) con formato ultrapanorámico 21:9 amplía el campo visual," +
                " mientras que su alta tasa de refresco de 180 Hz y tiempo de respuesta de 1 ms (MPRT) aseguran imágenes suaves y sin desenfoques en escenas rápidas."
        , 220999
        , R.drawable.promo3)

)