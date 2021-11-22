package graph;

import org.graphstream.graph.Node;
import org.graphstream.graph.Edge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.algorithm.Toolkit; 


public class Lab1 {

    SingleGraph myGraph;

    /**
     * Constructor of the class
     * 
     * @param args
     */
    public Lab1(String[] args) {
        myGraph = new SingleGraph("template graph");
        // exc1_2();
         exc3();
    }
    private void exc3() {
        try {
            myGraph.read("dgs/dgs.dgs");
            myGraph.display(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void exc1_2() {
        Node ANode = myGraph.addNode("A");
        Node BNode = myGraph.addNode("B");
        Node CNode = myGraph.addNode("C");
        Node DNode = myGraph.addNode("D");
        myGraph.addEdge("A-B", ANode.getId(), BNode.getId(), false);
        myGraph.addEdge("B-C", BNode.getId(), CNode.getId(), false);
        myGraph.addEdge("C-D", CNode.getId(), DNode.getId(), false);
        myGraph.addEdge("D-A", DNode.getId(), ANode.getId(), false);

        ANode.addAttribute("x", 10);
        ANode.addAttribute("y", 10);
        BNode.addAttribute("x", -10);
        BNode.addAttribute("y", -10);
        CNode.addAttribute("x", 10);
        CNode.addAttribute("y", -10);
        DNode.addAttribute("x", -10);
        DNode.addAttribute("y", 10);

        String shortStyle = "graph {fill-color: lightblue; " + "padding: 40px; }";
        myGraph.addAttribute("ui.stylesheet", shortStyle);


        Node n = Toolkit.randomNode(myGraph);
        n.addAttribute("ui.style", "fill-color:#ff00ff; shape:cross; size: 30px;");

        myGraph.display(false);
    }

    /**
     * the main just chooses the viewer and instantiates the class
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        new Lab1(args);
    }

}