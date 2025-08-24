import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

class Factura {
    private String numero;
    private String cliente;
    private double monto;
    private String fecha;

    public Factura(String numero, String cliente, double monto, String fecha) {
        this.numero = numero;
        this.cliente = cliente;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getNumero() {
        return numero;
    }

    public String getCliente() {
        return cliente;
    }

    public double getMonto() {
        return monto;
    }

    public String getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return "Factura N°: " + numero +
                "\nCliente: " + cliente +
                "\nMonto: $" + String.format("%.2f", monto) +
                "\nFecha: " + fecha + "\n";
    }
}

public class facturasdv {   // 👈 el nombre coincide con facturasdv.java
    private static final ArrayList<Factura> listaFacturas = new ArrayList<Factura>();
    private static final String ARCHIVO = "facturas.txt";
    private static final String SEPARADOR = "-------------------------------";

    public static void main(String[] args) {
        // Cargar al iniciar (si existe)
        cargarFacturasDesdeArchivo();

        int opcion = -1;
        while (opcion != 4) {
            String menu =
                    "===== MENÚ FACTURAS =====\n" +
                    "1. Registrar factura\n" +
                    "2. Consultar factura (por número)\n" +
                    "3. Mostrar todas las facturas (desde archivo)\n" +
                    "4. Salir\n" +
                    "Seleccione una opción:";
            String entrada = JOptionPane.showInputDialog(null, menu, "Sistema de Facturas", JOptionPane.QUESTION_MESSAGE);
            if (entrada == null) { // Cancelar = salir
                break;
            }

            try {
                opcion = Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingrese un número de opción válido (1-4).");
                continue;
            }

            switch (opcion) {
                case 1:
                    registrarFactura();
                    break;
                case 2:
                    consultarFactura();
                    break;
                case 3:
                    mostrarFacturas();
                    break;
                case 4:
                    JOptionPane.showMessageDialog(null, "Saliendo del sistema...");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida.");
            }
        }
    }

    private static void registrarFactura() {
        String numero = JOptionPane.showInputDialog("Ingrese número de factura:");
        if (numero == null || numero.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Número de factura requerido.");
            return;
        }
        numero = numero.trim();

        if (buscarPorNumero(numero) != null) {
            JOptionPane.showMessageDialog(null, "Ya existe una factura con ese número.");
            return;
        }

        String cliente = JOptionPane.showInputDialog("Ingrese nombre del cliente:");
        if (cliente == null || cliente.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nombre de cliente requerido.");
            return;
        }
        cliente = cliente.trim();

        String montoStr = JOptionPane.showInputDialog("Ingrese monto de la factura (use punto o coma):");
        if (montoStr == null || montoStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Monto requerido.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Monto inválido.");
            return;
        }

        String fecha = JOptionPane.showInputDialog("Ingrese fecha (dd/mm/aaaa):");
        if (fecha == null || fecha.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Fecha requerida.");
            return;
        }
        fecha = fecha.trim();

        Factura factura = new Factura(numero, cliente, monto, fecha);
        listaFacturas.add(factura);
        guardarFacturaEnArchivo(factura);

        JOptionPane.showMessageDialog(null, "Factura registrada con éxito.");
    }

    private static void consultarFactura() {
        String numero = JOptionPane.showInputDialog("Ingrese el número de factura a consultar:");
        if (numero == null || numero.trim().isEmpty()) {
            return;
        }
        numero = numero.trim();

        Factura f = buscarPorNumero(numero);
        if (f != null) {
            JOptionPane.showMessageDialog(null, f.toString());
        } else {
            JOptionPane.showMessageDialog(null, "Factura no se encuentra registrada.");
        }
    }

    private static void mostrarFacturas() {
        StringBuilder sb = new StringBuilder();
        File file = new File(ARCHIVO);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "No hay facturas registradas todavía.");
            return;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ARCHIVO));
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo de facturas.");
            return;
        } finally {
            try { if (br != null) br.close(); } catch (IOException ignored) {}
        }

        if (sb.length() == 0) {
            JOptionPane.showMessageDialog(null, "No hay facturas registradas todavía.");
        } else {
            JOptionPane.showMessageDialog(null, sb.toString());
        }
    }

    private static void guardarFacturaEnArchivo(Factura factura) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(ARCHIVO, true));
            bw.write(factura.toString());
            bw.write(SEPARADOR);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar la factura en archivo.");
        } finally {
            try { if (bw != null) bw.close(); } catch (IOException ignored) {}
        }
    }

    private static Factura buscarPorNumero(String numero) {
        // Buscar en memoria primero
        for (Factura f : listaFacturas) {
            if (f.getNumero().equalsIgnoreCase(numero)) {
                return f;
            }
        }
        // Si no está en memoria, intentar cargar desde archivo
        ArrayList<Factura> delArchivo = cargarFacturasDesdeArchivo();
        for (Factura f : delArchivo) {
            if (f.getNumero().equalsIgnoreCase(numero)) {
                return f;
            }
        }
        return null;
    }

    private static ArrayList<Factura> cargarFacturasDesdeArchivo() {
        ArrayList<Factura> cargadas = new ArrayList<Factura>();
        File file = new File(ARCHIVO);
        if (!file.exists()) {
            return cargadas;
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ARCHIVO));
            String linea;
            String num = null, cli = null, fec = null;
            Double mon = null;

            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("Factura N°: ")) {
                    num = linea.substring("Factura N°: ".length()).trim();
                } else if (linea.startsWith("Cliente: ")) {
                    cli = linea.substring("Cliente: ".length()).trim();
                } else if (linea.startsWith("Monto: $")) {
                    String montoStr = linea.substring("Monto: $".length()).trim().replace(',', '.');
                    try {
                        mon = Double.parseDouble(montoStr);
                    } catch (NumberFormatException e) {
                        mon = 0.0;
                    }
                } else if (linea.startsWith("Fecha: ")) {
                    fec = linea.substring("Fecha: ".length()).trim();
                } else if (linea.startsWith(SEPARADOR)) {
                    if (num != null && cli != null && mon != null && fec != null) {
                        Factura f = new Factura(num, cli, mon.doubleValue(), fec);
                        cargadas.add(f);
                        if (buscarEnMemoria(num) == null) {
                            listaFacturas.add(f);
                        }
                    }
                    num = null; cli = null; mon = null; fec = null;
                }
            }
        } catch (IOException ignored) {
        } finally {
            try { if (br != null) br.close(); } catch (IOException ignored) {}
        }
        return cargadas;
    }

    private static Factura buscarEnMemoria(String numero) {
        for (Factura f : listaFacturas) {
            if (f.getNumero().equalsIgnoreCase(numero)) return f;
        }
        return null;
    }
}
