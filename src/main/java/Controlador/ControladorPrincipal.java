package Controlador;

import Modelo.Grafo;
import Vista.VentanaPrincipal;
import javax.swing.*;
import java.util.List;

public class ControladorPrincipal {
    private final Grafo grafo = new Grafo();
    private final VentanaPrincipal vista = new VentanaPrincipal();

    public ControladorPrincipal() {
        vista.agregarNodo().addActionListener(e -> ejecutar(() -> {
            grafo.agregarNodo(vista.nuevoNodo().getText());
            vista.dibujarGrafo(grafo, List.of());
            vista.mensaje("Nodo agregado. Nodos: " + grafo.nodos());
        }));
        vista.agregarArista().addActionListener(e -> ejecutar(() -> {
            String origen = vista.edgeOrigen().getText();
            String destino = vista.edgeDestino().getText();
            double distancia = Double.parseDouble(vista.distanciaArista().getText().trim());
            grafo.agregarArista(origen, destino, distancia);
            vista.dibujarGrafo(grafo, List.of());
            vista.mensaje("Camino agregado: " + origen.trim() + " ↔ " + destino.trim()
                    + " | Distancia: " + distancia + " | Nodos: " + grafo.nodos());
        }));
        vista.corto().addActionListener(e -> resolverGrafo(false));
        vista.largo().addActionListener(e -> resolverGrafo(true));
    }

    public void iniciar() { vista.setVisible(true); }

    private void resolverGrafo(boolean largo) {
        ejecutar(() -> {
            Grafo.Resultado resultado = largo
                    ? grafo.caminoMasLargo(vista.grafoOrigen().getText(), vista.grafoDestino().getText())
                    : grafo.caminoMasCorto(vista.grafoOrigen().getText(), vista.grafoDestino().getText());
            vista.dibujarGrafo(grafo, resultado.camino());
            vista.mensaje(resultado.alcanzable()
                    ? "Camino " + (largo ? "más largo" : "más corto") + ": "
                    + String.join(" → ", resultado.camino()) + " | Distancia total: " + resultado.distancia()
                    : "No existe un camino entre esos nodos.");
        });
    }

    private void ejecutar(Runnable accion) {
        try { accion.run(); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Dato inválido", JOptionPane.WARNING_MESSAGE);
        }
    }
}
