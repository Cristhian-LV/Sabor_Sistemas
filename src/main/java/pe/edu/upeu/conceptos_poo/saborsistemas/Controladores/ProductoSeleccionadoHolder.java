package pe.edu.upeu.conceptos_poo.saborsistemas.Controladores;

// Usaremos una clase interna aquí para simplificar, pero podría ser una clase DTO separada
public class ProductoSeleccionadoHolder {
    private static ProductoItem producto; // Usa la clase interna ProductoItem

    public static void setProducto(ProductoItem p) {
        producto = p;
    }

    public static ProductoItem getProducto() {
        return producto;
    }

    public static void limpiar() {
        producto = null;
    }

    // Clase interna para pasar datos simplificados entre controladores
    public static class ProductoItem {
        private final String codigo; // Puede ser el ID como String
        private final String nombre;
        private final double precio;
        private final int stock; // Usamos int para simplificar

        public ProductoItem(String codigo, String nombre, double precio, int stock) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.precio = precio;
            this.stock = stock;
        }

        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public double getPrecio() { return precio; }
        public int getStock() { return stock; }
    }
}