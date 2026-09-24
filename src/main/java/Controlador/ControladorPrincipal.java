package Controlador;

import Modelo.Arbol;
import Modelo.Grafo;
import Vista.VentanaPrincipal;
import javax.swing.*;
import java.util.List;

public class ControladorPrincipal {
    private final Grafo grafo=new Grafo(); private final Arbol arbol=new Arbol(); private final VentanaPrincipal vista=new VentanaPrincipal();
    public ControladorPrincipal(){
        vista.agregarNodo().addActionListener(e->accionGrafo(()->{grafo.agregarNodo(vista.nuevoNodo().getText());vista.dibujarGrafo(grafo,List.of());vista.mensajeGrafo("Nodo agregado. Nodos: "+grafo.nodos());}));
        vista.agregarArista().addActionListener(e->accionGrafo(()->{double distancia=Double.parseDouble(vista.distanciaArista().getText().trim());grafo.agregarArista(vista.edgeOrigen().getText(),vista.edgeDestino().getText(),distancia);vista.dibujarGrafo(grafo,List.of());vista.mensajeGrafo("Camino agregado: "+vista.edgeOrigen().getText().trim()+" ↔ "+vista.edgeDestino().getText().trim()+" | Distancia: "+distancia+". Nodos: "+grafo.nodos());}));
        vista.corto().addActionListener(e->resolverGrafo(false));vista.largo().addActionListener(e->resolverGrafo(true));
        vista.relacion().addActionListener(e->accionArbol(()->{arbol.agregarRelacion(vista.padre().getText(),vista.hijo().getText());vista.dibujarArbol(arbol,List.of());vista.mensajeArbol("Relación agregada. Raíz: "+arbol.raiz()+" | Nodos: "+arbol.nodos());}));
        vista.buscarArbol().addActionListener(e->accionArbol(()->{List<String> ruta=arbol.camino(vista.arbolOrigen().getText(),vista.arbolDestino().getText());vista.dibujarArbol(arbol,ruta);vista.mensajeArbol(ruta.isEmpty()?"No existe una ruta entre esos nodos del árbol.":"Camino: "+String.join(" → ",ruta)+" | Distancia: "+(ruta.size()-1)+" aristas");}));
    }
    public void iniciar(){vista.setVisible(true);}
    private void resolverGrafo(boolean largo){accionGrafo(()->{Grafo.Resultado r=largo?grafo.caminoMasLargo(vista.grafoOrigen().getText(),vista.grafoDestino().getText()):grafo.caminoMasCorto(vista.grafoOrigen().getText(),vista.grafoDestino().getText());vista.dibujarGrafo(grafo,r.camino());vista.mensajeGrafo(r.alcanzable()?"Camino "+(largo?"más largo":"más corto")+": "+String.join(" → ",r.camino())+" | Distancia total: "+r.distancia():"No existe un camino entre esos nodos.");});}
    private void accionGrafo(Runnable r){try{r.run();}catch(Exception ex){JOptionPane.showMessageDialog(vista,ex.getMessage(),"Dato inválido",JOptionPane.WARNING_MESSAGE);}}
    private void accionArbol(Runnable r){try{r.run();}catch(Exception ex){JOptionPane.showMessageDialog(vista,ex.getMessage(),"Dato inválido",JOptionPane.WARNING_MESSAGE);}}
}
