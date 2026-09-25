package Vista;

import Modelo.Grafo;
import javax.swing.*;
import java.awt.*;
import java.util.*;

/** Ventana única para configurar el grafo y consultar sus caminos. */
public class VentanaPrincipal extends JFrame {
    private final JTextField nuevoNodo = new JTextField(8);
    private final JTextField edgeOrigen = new JTextField(8);
    private final JTextField edgeDestino = new JTextField(8);
    private final JTextField distanciaArista = new JTextField(7);
    private final JTextField grafoOrigen = new JTextField(8);
    private final JTextField grafoDestino = new JTextField(8);
    private final JTextArea salida = new JTextArea(4, 40);
    private final LienzoGrafo lienzo = new LienzoGrafo();
    private final JButton agregarNodo = new JButton("Agregar nodo");
    private final JButton agregarArista = new JButton("Agregar camino");
    private final JButton corto = new JButton("Camino más corto");
    private final JButton largo = new JButton("Camino más largo");
    private final JButton nuevoGrafo = new JButton("Nuevo grafo");

    public VentanaPrincipal() {
        super("Explorador de grafos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        JPanel filaNodos = new JPanel(new BorderLayout());
        JPanel controlesNodo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlesNodo.add(new JLabel("Nuevo nodo:")); controlesNodo.add(nuevoNodo); controlesNodo.add(agregarNodo);
        JPanel controlesGrafo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlesGrafo.add(nuevoGrafo);
        filaNodos.add(controlesNodo, BorderLayout.WEST);
        filaNodos.add(controlesGrafo, BorderLayout.EAST);
        JPanel filaCaminos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaCaminos.add(new JLabel("Crear camino:"));
        filaCaminos.add(new JLabel("Desde:")); filaCaminos.add(edgeOrigen);
        filaCaminos.add(new JLabel("Hasta:")); filaCaminos.add(edgeDestino);
        filaCaminos.add(new JLabel("Distancia:")); filaCaminos.add(distanciaArista);
        filaCaminos.add(agregarArista);
        formulario.add(filaNodos); formulario.add(filaCaminos);

        JPanel filaConsulta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaConsulta.add(new JLabel("Origen de búsqueda:")); filaConsulta.add(grafoOrigen);
        filaConsulta.add(new JLabel("Destino:")); filaConsulta.add(grafoDestino);
        filaConsulta.add(corto); filaConsulta.add(largo);
        salida.setEditable(false); salida.setLineWrap(true); salida.setWrapStyleWord(true);
        JPanel inferior = new JPanel(new BorderLayout());
        inferior.add(filaConsulta, BorderLayout.NORTH); inferior.add(new JScrollPane(salida), BorderLayout.CENTER);

        add(formulario, BorderLayout.NORTH); add(lienzo, BorderLayout.CENTER); add(inferior, BorderLayout.SOUTH);
    }

    public JTextField nuevoNodo() { return nuevoNodo; }
    public JTextField edgeOrigen() { return edgeOrigen; }
    public JTextField edgeDestino() { return edgeDestino; }
    public JTextField distanciaArista() { return distanciaArista; }
    public JTextField grafoOrigen() { return grafoOrigen; }
    public JTextField grafoDestino() { return grafoDestino; }
    public JButton agregarNodo() { return agregarNodo; }
    public JButton agregarArista() { return agregarArista; }
    public JButton corto() { return corto; }
    public JButton largo() { return largo; }
    public JButton nuevoGrafo() { return nuevoGrafo; }
    public void limpiarCampos() {
        nuevoNodo.setText(""); edgeOrigen.setText(""); edgeDestino.setText(""); distanciaArista.setText("");
        grafoOrigen.setText(""); grafoDestino.setText("");
    }
    public void mensaje(String texto) { salida.setText(texto); }
    public void dibujarGrafo(Grafo grafo, java.util.List<String> ruta) { lienzo.setData(grafo, ruta); }

    private static class LienzoGrafo extends JPanel {
        private Grafo grafo = new Grafo();
        private java.util.List<String> ruta = java.util.List.of();
        LienzoGrafo() { setBackground(Color.WHITE); setPreferredSize(new Dimension(700, 350)); }
        void setData(Grafo grafo, java.util.List<String> ruta) {
            this.grafo = grafo; this.ruta = java.util.List.copyOf(ruta); repaint();
        }
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            java.util.List<String> nodos = new ArrayList<>(grafo.nodos());
            Map<String, Point> posiciones = new LinkedHashMap<>();
            int cx = getWidth() / 2, cy = getHeight() / 2;
            int radio = Math.max(45, Math.min(getWidth(), getHeight()) / 2 - 45);
            for (int i = 0; i < nodos.size(); i++) {
                double angulo = 2 * Math.PI * i / Math.max(1, nodos.size()) - Math.PI / 2;
                posiciones.put(nodos.get(i), new Point(cx + (int)(radio * Math.cos(angulo)), cy + (int)(radio * Math.sin(angulo))));
            }
            Set<String> aristasRuta = new HashSet<>();
            for (int i = 1; i < ruta.size(); i++) aristasRuta.add(clave(ruta.get(i - 1), ruta.get(i)));
            for (String desde : nodos) for (var arista : grafo.aristas().get(desde).entrySet()) {
                String hasta = arista.getKey();
                if (desde.compareTo(hasta) >= 0) continue;
                Point a = posiciones.get(desde), b = posiciones.get(hasta);
                boolean seleccionada = aristasRuta.contains(clave(desde, hasta));
                g.setColor(seleccionada ? new Color(220, 65, 55) : Color.GRAY);
                g.setStroke(new BasicStroke(seleccionada ? 3f : 1.5f));
                g.drawLine(a.x, a.y, b.x, b.y);
                g.setColor(Color.DARK_GRAY);
                g.drawString(String.valueOf(arista.getValue()), (a.x + b.x) / 2, (a.y + b.y) / 2);
            }
            for (String nodo : nodos) {
                Point p = posiciones.get(nodo);
                g.setColor(ruta.contains(nodo) ? new Color(255, 220, 120) : new Color(205, 230, 250));
                g.fillOval(p.x - 20, p.y - 20, 40, 40);
                g.setColor(Color.DARK_GRAY); g.drawOval(p.x - 20, p.y - 20, 40, 40);
                g.drawString(nodo, p.x - 5, p.y + 5);
            }
            g.dispose();
        }
        private String clave(String a, String b) { return a.compareTo(b) < 0 ? a + "\u0000" + b : b + "\u0000" + a; }
    }
}
