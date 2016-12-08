package Routage;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created by Florian on 23/03/2016.
 */
public class Routage {

    public static void main(String[] args) {
    	
        int score=0;
        int scoreTemp;
        
        String idTemp;
        String result="";
        
        Edge temp;
        Node n1temp,n2temp;
        
        TreeMap<Integer,Node> tri;
        Iterator<Node> ite;
        ArrayList<Node> neighbours;
        
        boolean premier=true;
        
        Graph graph = new SingleGraph("Routage");
        
        try {
            graph.read("./graphe.dgs");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        graph.display(false);
        graph.addAttribute("ui.stylesheet", "url(./style.css)");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        
        for(Node n : graph.getNodeSet()){
            for(Node end : graph.getNodeSet()){
                
            	//Parcours des voisins et attribution des scores
                ite = n.getNeighborNodeIterator();
                neighbours = new ArrayList<Node>();
                
                while(ite.hasNext())neighbours.add(ite.next());
                
                for(Node neighbour : neighbours){
                    
                	temp = n.getEdgeBetween(neighbour);
                    scoreTemp=(Integer)((Edge)n.getEdgeBetween(neighbour)).getAttribute("length");
                    score += scoreTemp;
                    n1temp=temp.getNode0();
                    n2temp=temp.getNode1();
                    idTemp=temp.getId();
                    graph.removeEdge((Edge)n.getEdgeBetween(neighbour));
                    score=0;
                    
                    if(!end.equals(n)){
                        score +=getLength(graph,neighbour,end);
                        neighbour.setAttribute("score",score);
                    }
                    
                    graph.addEdge(idTemp,n1temp,n2temp,false);
                    graph.getEdge(idTemp).setAttribute("length",scoreTemp);
                }
                
                //Classement des scores
                tri = new TreeMap<Integer,Node>();
                ite = n.getNeighborNodeIterator();
                
                while(ite.hasNext()) {
                    
                	Node neighbour = ite.next();
                    
                	if (!end.equals(n)) {
                        tri.put((Integer) neighbour.getAttribute("score"), neighbour);
                    }
                }
                //Création de la table finale pour cette fin
                if(!n.equals(end)) {
                    
                	result += n.getId() + " -> " + end.getId() + " : ";
                    for (Node no : tri.values()) result += no.getId() + " ";
                    result += "\n";
                }
            }
            premier=true;
        }
        
        for (Node n : graph.getNodeSet()) n.setAttribute("ui.label", n.getId());
        for(Edge e : graph.getEachEdge()) e.setAttribute("ui.label",""+(Integer)e.getAttribute("length"));
        
        System.out.println(result);
    }

    public static int getLength(Graph g,Node start, Node end){
        
    	int length=0;
        
    	Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra.init(g);
        dijkstra.setSource(start);
        dijkstra.compute();
        
        for (Edge e : dijkstra.getPathEdges(end)) length+=(Integer)e.getAttribute("length");
        return length;
    }
}
