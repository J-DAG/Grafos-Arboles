package Vista;

import Modelo.Arbol;
import Modelo.Grafo;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class VentanaPrincipal extends JFrame {
    private final JTextField grafoOrigen=new JTextField(7), grafoDestino=new JTextField(7), nuevoNodo=new JTextField(7);
    private final JTextField edgeOrigen=new JTextField(7), edgeDestino=new JTextField(7), distanciaArista=new JTextField(6);
    private final JTextField padre=new JTextField(7), hijo=new JTextField(7), arbolOrigen=new JTextField(7), arbolDestino=new JTextField(7);
    private final JTextArea salidaGrafo=new JTextArea(4,30), salidaArbol=new JTextArea(4,30);
    private final LienzoGrafo lienzoGrafo=new LienzoGrafo(); private final LienzoArbol lienzoArbol=new LienzoArbol();
    private final JButton agregarNodo=new JButton("Agregar nodo"), agregarArista=new JButton("Agregar camino"), corto=new JButton("Camino más corto"), largo=new JButton("Camino más largo");
    private final JButton relacion=new JButton("Agregar relación"), buscarArbol=new JButton("Buscar camino");

    public VentanaPrincipal() {
        super("Explorador de grafos y árboles"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(900,650); setLocationRelativeTo(null);
        JTabbedPane tabs=new JTabbedPane(); tabs.addTab("Menú principal", crearInicio()); tabs.addTab("Grafo", crearGrafo()); tabs.addTab("Opción Árbol", crearArbol()); setContentPane(tabs);
    }
    private JPanel crearInicio() {
        JPanel p=new JPanel(new GridBagLayout()); JPanel tarjeta=new JPanel(); tarjeta.setLayout(new BoxLayout(tarjeta,BoxLayout.Y_AXIS));
        JLabel titulo=new JLabel("Árboles y caminos"); titulo.setFont(titulo.getFont().deriveFont(Font.BOLD,26f));
        tarjeta.add(titulo); tarjeta.add(Box.createVerticalStrut(12)); tarjeta.add(new JLabel("Elige un módulo para configurar estructuras y explorar rutas."));
        JButton g=new JButton("Abrir módulo Grafo"), a=new JButton("Abrir módulo Árbol"); tarjeta.add(Box.createVerticalStrut(20)); tarjeta.add(g); tarjeta.add(Box.createVerticalStrut(8)); tarjeta.add(a);
        g.addActionListener(e->seleccionar(1)); a.addActionListener(e->seleccionar(2)); p.add(tarjeta); return p;
    }
    private JPanel crearGrafo() {
        JPanel p=new JPanel(new BorderLayout(8,8)); JPanel top=new JPanel(); top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
        JPanel filaNodos=new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaNodos.add(new JLabel("Nuevo nodo:")); filaNodos.add(nuevoNodo); filaNodos.add(agregarNodo);
        JPanel filaCaminos=new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaCaminos.add(new JLabel("Crear camino (no dirigido):")); filaCaminos.add(new JLabel("Desde:")); filaCaminos.add(edgeOrigen);
        filaCaminos.add(new JLabel("Hasta:")); filaCaminos.add(edgeDestino); filaCaminos.add(new JLabel("Distancia:")); filaCaminos.add(distanciaArista); filaCaminos.add(agregarArista);
        top.add(filaNodos); top.add(filaCaminos);
        JPanel acciones=new JPanel(new FlowLayout(FlowLayout.LEFT)); acciones.add(new JLabel("Origen")); acciones.add(grafoOrigen); acciones.add(new JLabel("Destino")); acciones.add(grafoDestino); acciones.add(corto); acciones.add(largo);
        salidaGrafo.setEditable(false); salidaGrafo.setLineWrap(true); JPanel sur=new JPanel(new BorderLayout()); sur.add(acciones,BorderLayout.NORTH); sur.add(new JScrollPane(salidaGrafo));
        p.add(top,BorderLayout.NORTH); p.add(lienzoGrafo,BorderLayout.CENTER); p.add(sur,BorderLayout.SOUTH); return p;
    }
    private JPanel crearArbol() {
        JPanel p=new JPanel(new BorderLayout(8,8)); JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT)); top.add(new JLabel("Padre")); top.add(padre); top.add(new JLabel("Hijo")); top.add(hijo); top.add(relacion);
        JPanel acciones=new JPanel(new FlowLayout(FlowLayout.LEFT)); acciones.add(new JLabel("Desde")); acciones.add(arbolOrigen); acciones.add(new JLabel("Hasta")); acciones.add(arbolDestino); acciones.add(buscarArbol);
        salidaArbol.setEditable(false); salidaArbol.setLineWrap(true); JPanel sur=new JPanel(new BorderLayout()); sur.add(acciones,BorderLayout.NORTH); sur.add(new JScrollPane(salidaArbol));
        p.add(top,BorderLayout.NORTH); p.add(lienzoArbol,BorderLayout.CENTER); p.add(sur,BorderLayout.SOUTH); return p;
    }
    private void seleccionar(int n) { ((JTabbedPane)getContentPane()).setSelectedIndex(n); }
    public JTextField grafoOrigen(){return grafoOrigen;} public JTextField grafoDestino(){return grafoDestino;}
    public JTextField nuevoNodo(){return nuevoNodo;} public JTextField edgeOrigen(){return edgeOrigen;} public JTextField edgeDestino(){return edgeDestino;} public JTextField distanciaArista(){return distanciaArista;}
    public JTextField padre(){return padre;} public JTextField hijo(){return hijo;} public JTextField arbolOrigen(){return arbolOrigen;} public JTextField arbolDestino(){return arbolDestino;}
    public JButton agregarNodo(){return agregarNodo;} public JButton agregarArista(){return agregarArista;} public JButton corto(){return corto;} public JButton largo(){return largo;} public JButton relacion(){return relacion;} public JButton buscarArbol(){return buscarArbol;}
    public void mensajeGrafo(String s){salidaGrafo.setText(s);} public void mensajeArbol(String s){salidaArbol.setText(s);}
    public void dibujarGrafo(Grafo g, java.util.List<String> ruta){lienzoGrafo.setData(g,ruta);}
    public void dibujarArbol(Arbol a, java.util.List<String> ruta){lienzoArbol.setData(a,ruta);}

    private static class LienzoGrafo extends JPanel {
        private Grafo grafo=new Grafo(); private java.util.List<String> ruta=java.util.List.of();
        LienzoGrafo(){setBackground(Color.WHITE);setPreferredSize(new Dimension(700,350));}
        void setData(Grafo g,java.util.List<String> r){grafo=g;ruta=java.util.List.copyOf(r);repaint();}
        protected void paintComponent(Graphics graphics){super.paintComponent(graphics); Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            java.util.List<String> ns=new ArrayList<>(grafo.nodos()); Map<String,Point> pos=new LinkedHashMap<>(); int cx=getWidth()/2,cy=getHeight()/2,rad=Math.max(45,Math.min(getWidth(),getHeight())/2-45);
            for(int i=0;i<ns.size();i++){double ang=2*Math.PI*i/Math.max(1,ns.size())-Math.PI/2;pos.put(ns.get(i),new Point(cx+(int)(rad*Math.cos(ang)),cy+(int)(rad*Math.sin(ang))));}
            Set<String> rutaAristas=new HashSet<>();for(int i=1;i<ruta.size();i++)rutaAristas.add(clave(ruta.get(i-1),ruta.get(i)));
            for(String a:ns)for(var e:grafo.aristas().get(a).entrySet())if(a.compareTo(e.getKey())<0){Point x=pos.get(a),y=pos.get(e.getKey()); boolean activo=rutaAristas.contains(clave(a,e.getKey()));g.setColor(activo?new Color(220,65,55):Color.GRAY);g.setStroke(new BasicStroke(activo?3f:1.5f));g.drawLine(x.x,x.y,y.x,y.y);g.setColor(Color.DARK_GRAY);g.drawString(String.valueOf(e.getValue()),(x.x+y.x)/2,(x.y+y.y)/2);}
            for(String n:ns){Point q=pos.get(n);boolean activo=ruta.contains(n);g.setColor(activo?new Color(255,220,120):new Color(205,230,250));g.fillOval(q.x-20,q.y-20,40,40);g.setColor(Color.DARK_GRAY);g.drawOval(q.x-20,q.y-20,40,40);g.drawString(n,q.x-5,q.y+5);}g.dispose();}
        private String clave(String a,String b){return a.compareTo(b)<0?a+"\u0000"+b:b+"\u0000"+a;}
    }
    private static class LienzoArbol extends JPanel {
        private Arbol arbol=new Arbol();private java.util.List<String> ruta=java.util.List.of();private Map<String,Point> pos=new HashMap<>();
        LienzoArbol(){setBackground(Color.WHITE);setPreferredSize(new Dimension(700,350));}
        void setData(Arbol a,java.util.List<String> r){arbol=a;ruta=java.util.List.copyOf(r);repaint();}
        protected void paintComponent(Graphics graphics){super.paintComponent(graphics);Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);pos.clear();if(arbol.raiz()!=null)ubicar(arbol.raiz(),getWidth()/2,35,Math.max(60,getWidth()/Math.max(3,arbol.nodos().size()+1)));Set<String> re=new HashSet<>();for(int i=1;i<ruta.size();i++)re.add(ruta.get(i-1)+"\u0000"+ruta.get(i));
            for(String n:arbol.nodos())for(String h:arbol.hijos(n)){Point x=pos.get(n),y=pos.get(h);if(x!=null&&y!=null){g.setColor(re.contains(n+"\u0000"+h)?new Color(220,65,55):Color.GRAY);g.setStroke(new BasicStroke(re.contains(n+"\u0000"+h)?3f:1.5f));g.drawLine(x.x,x.y,y.x,y.y);}}
            for(String n:arbol.nodos()){Point q=pos.get(n);if(q==null)continue;g.setColor(ruta.contains(n)?new Color(255,220,120):new Color(205,230,250));g.fillOval(q.x-20,q.y-20,40,40);g.setColor(Color.DARK_GRAY);g.drawOval(q.x-20,q.y-20,40,40);g.drawString(n,q.x-5,q.y+5);}g.dispose();}
        private int ubicar(String n,int x,int y,int ancho){pos.put(n,new Point(x,y));java.util.List<String> hs=arbol.hijos(n);if(hs.isEmpty())return x;int total=(hs.size()-1)*ancho;for(int i=0;i<hs.size();i++)ubicar(hs.get(i),x-total/2+i*ancho,y+75,Math.max(35,ancho/2));return x;}
    }
}
