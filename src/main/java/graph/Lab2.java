package graph;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

import javax.sql.rowset.serial.SerialArray;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;

public class Lab2{
    static public SingleGraph myGraph;

    /**
     * Constructor of the class
     * 
     * @param args
     */
    public Lab2(String[] args) {
        myGraph = new SingleGraph("lab2 graph");
        // ex1();
        // ex2();
        //ex3();
        // exercise4Dijkstra();
        exercise5();
        // exercise6();
    }

    /**
     * 
     */
    public void exercise6() {
        myGraph = Tools.read("dgs/lab2/gridvaluated_30_1:20.dgs");
        myGraph.display(false);

        ArrayList<Node> priorityList = new ArrayList<>();
        Node v = Toolkit.randomNode(myGraph);
        v.addAttribute("distance", 0);
        v.addAttribute("ui.style", "fill-color:#00ff00;size:10px;");
        priorityList.add(v);
        // hitakey("start Dijkstra");
        while (!priorityList.isEmpty()) {
            Node w = priorityList.remove(0);
            mark(w);
            w.setAttribute("ui.label", "");
            Tools.pause(10);
            Iterator<Node> neighbors = w.getNeighborNodeIterator();
            while (neighbors.hasNext()) {
                Node t = neighbors.next();
                if (!t.hasAttribute("marked")) {
                    Edge link = w.getEdgeBetween(t);
                    link.setAttribute("ui.label", "");
                    int newDistance = (int) w.getAttribute("distance") + (int) link.getAttribute("distance");
                    if (priorityList.contains(t)) {
                        int currentDistance = t.getAttribute("distance");
                        if (newDistance < currentDistance) {
                            priorityList.remove(t);
                            t.addAttribute("distance", newDistance);
                            insertIntoPriorityList(priorityList, t);
                        }
                        link.addAttribute("ui.style", "fill-color:#AAAAAA;size:1;");
                    } else {
                        t.addAttribute("distance", newDistance);
                        insertIntoPriorityList(priorityList, t);
                        link.addAttribute("ui.style", "fill-color:#000000;size:2;");
                    }
                }
            }
        }
        v.addAttribute("ui.style", "fill-color:#00ff00;size:10px;");
    }
    /**
     * T
     */
    public void exercise5() {
        myGraph = Tools.read("dgs/lab2/gridvaluated_30_1:20.dgs");
        myGraph.display(false);

        for (Edge edge : myGraph.getEachEdge()) {
            edge.removeAttribute("ui.label");
        }

        ArrayList<Node> priorityList = new ArrayList<>();

        int radius = Integer.MAX_VALUE;
        int diameter = -1;
        
        for (Node v : myGraph.getEachNode()) {//for each node in graph
            v.addAttribute("distance", 0);
            v.removeAttribute("ui.label");
            int eccentricity = 0;
            priorityList.add(v);
            while (!priorityList.isEmpty()) {//calculate Dikstrja
                Node w = priorityList.remove(0);
                mark(w);
                Iterator<Node> neighbors = w.getNeighborNodeIterator();
                while (neighbors.hasNext()) {
                    Node t = neighbors.next();
                    if (!t.hasAttribute("marked")) {
                        Edge link = w.getEdgeBetween(t);
                        int newDistance = (int) w.getAttribute("distance") + (int) link.getAttribute("distance");
                        if (priorityList.contains(t)) {
                            int currentDistance = t.getAttribute("distance");
                            if (newDistance < currentDistance) {
                                priorityList.remove(t);
                                t.addAttribute("distance", newDistance);
                                eccentricity = Integer.max(eccentricity, newDistance);
                                insertIntoPriorityList(priorityList, t);
                            }
                        } else {
                            t.addAttribute("distance", newDistance);
                            eccentricity = Integer.max(eccentricity, newDistance);
                            insertIntoPriorityList(priorityList, t);
                        }
                    }
                }
            }
            // v.addAttribute("ui.style", "fill-color:#00ff00;size:10px;");
            radius = Integer.min(radius, eccentricity);//calculate current radius
            diameter = Integer.max(diameter, eccentricity);//calculate current diameter

            v.addAttribute("eccenticity", eccentricity);//save eccenticity
            for (Node tmp : myGraph.getEachNode()) {//remove all marks and distances calculated for this node
                unmark(tmp);
                tmp.removeAttribute("distance");
            }
        }
        System.out.println("radius: "+radius);
        System.out.println("diameter: "+diameter);

        int diffrence = Math.abs(radius-diameter);
        for (Node node : myGraph.getEachNode()) {//set gradient
            double tmpRadius = 1-(((int)node.getAttribute("eccenticity")- diffrence)/Double.valueOf(Integer.valueOf(radius).toString()));
            double tmpDiameter = (((int)node.getAttribute("eccenticity")- diffrence)/Double.valueOf(Integer.valueOf(diameter).toString()));

            int red = (int)(tmpDiameter * 255);
            int blue = (int)(tmpRadius * 255);
            node.addAttribute("ui.style", "fill-color: rgb(" + red + ",0,"+blue+");size:18px;");
        }
    }


    /**
     * we have to use a priority list. We will manage such a list ourselves.
     */
    public void exercise4Dijkstra() {
        myGraph = Tools.read("dgs/lab2/gridvaluated_10_2:20.dgs");
        myGraph.display(false);

        ArrayList<Node> priorityList = new ArrayList<>();
        Node v = Toolkit.randomNode(myGraph);
        v.addAttribute("distance", 0);
        v.addAttribute("ui.style", "fill-color:#00ff00;size:10px;");
        priorityList.add(v);
        // hitakey("start Dijkstra");
        while (!priorityList.isEmpty()) {
            Node w = priorityList.remove(0);
            mark(w);
            w.addAttribute("ui.label", "D:" + w.getAttribute("distance"));
            Tools.pause(10);
            Iterator<Node> neighbors = w.getNeighborNodeIterator();
            while (neighbors.hasNext()) {
                Node t = neighbors.next();
                if (!t.hasAttribute("marked")) {
                    Edge link = w.getEdgeBetween(t);
                    int newDistance = (int) w.getAttribute("distance") + (int) link.getAttribute("distance");
                    if (priorityList.contains(t)) {
                        int currentDistance = t.getAttribute("distance");
                        if (newDistance < currentDistance) {
                            priorityList.remove(t);
                            t.addAttribute("distance", newDistance);
                            insertIntoPriorityList(priorityList, t);
                        }
                    } else {
                        t.addAttribute("distance", newDistance);
                        insertIntoPriorityList(priorityList, t);
                    }
                }
            }
        }
        v.addAttribute("ui.style", "fill-color:#00ff00;size:10px;");
    }

    /**
     * insertion of a node into the priority list, priority based on the distance
     * attribute. The principle consists in finding the good position of the node in
     * parameter within the list by comparing the respective distances of the nodes
     * 
     * @param list
     * @param v
     */
    private void insertIntoPriorityList(ArrayList<Node> list, Node v) {
        boolean inserted = false;
        int position = 0;
        if (list.size() == 0) {
            list.add(v);
            inserted = true;
        }
        int referenceDistance = v.getAttribute("distance");
        while (!inserted && position < list.size()) {
            int currentDistance = (list.get(position)).getAttribute("distance");
            if (currentDistance > referenceDistance) {
                list.add(position, v);
                inserted = true;
            } else
                position++;
        }
        if (!inserted)
            list.add(v);
    }

    /**
     * BFS algorithm
     */
    public void bfs() {
        hitakey("before BFS");
        ArrayList<Node> nodesToBeProcessed = new ArrayList<>();
        Node v = Toolkit.randomNode(myGraph);
        nodesToBeProcessed.add(v);
        while (!nodesToBeProcessed.isEmpty()) {
            Node w = nodesToBeProcessed.remove(0);
            mark(w);
            Tools.pause(10);
            Iterator<Node> neighbors = w.getNeighborNodeIterator();
            while (neighbors.hasNext()) {
                Node t = neighbors.next();
                if (!t.hasAttribute("marked") && (!nodesToBeProcessed.contains(t))) {
                    nodesToBeProcessed.add(t);
                    Edge link = w.getEdgeBetween(t);
                    link.addAttribute("ui.style", "fill-color:#000000;size:5px;");
                }
            }
        }
    }

    /**
     * DFS algorithm
     */
    public void dfs() {
        hitakey("before DFS");
        Stack<Node> nodesToBeProcessed = new Stack<>();
        Node v = Toolkit.randomNode(myGraph);
        nodesToBeProcessed.push(v);
        while (!nodesToBeProcessed.isEmpty()) {
            Node w = nodesToBeProcessed.pop();
            mark(w);
            Tools.pause(10);
            Iterator<Node> neighbors = w.getNeighborNodeIterator();
            while (neighbors.hasNext()) {
                Node t = neighbors.next();
                if (!t.hasAttribute("marked") && (!nodesToBeProcessed.contains(t))) {
                    nodesToBeProcessed.push(t);
                    Edge link = w.getEdgeBetween(t);
                    link.addAttribute("ui.style", "fill-color:#000000;size:5px;");
                }
            }
        }
    }

    private void mark(Node n) {
        n.addAttribute("marked", true);
        n.addAttribute("ui.style", "fill-color:black;size:5px;");
    }
    private void unmark(Node n) {
        n.removeAttribute("marked");
        n.addAttribute("ui.style", "fill-color:black;size:5px;");
    }

    private void ex3() {
        try {
            myGraph.read("dgs/lab2/completegrid_30.dgs");
            myGraph.addAttribute("ui.stylesheet", "graph {fill-color: #777777;}");
            Stack<Node> stack = new Stack<Node>();

            Node start = Toolkit.randomNode(myGraph);
            start.addAttribute("visited", "1");
            Iterator it = start.getNeighborNodeIterator();
            while (it.hasNext()) {
                stack.push((Node) it.next());
            }
            Node goal = Toolkit.randomNode(myGraph);
            while (start.equals(goal)) {// if start == goal than random again
                goal = Toolkit.randomNode(myGraph);
            }
            Node node = null;
            while ((node = stack.pop()) != null) {
                if(node.getAttribute("visited")!= null){
                    continue;
                }
                else{
                    node.addAttribute("visited", "1");
                }
                node.addAttribute("ui.style", "fill-color:#FFFF00;");

                if (goal.equals(node)) {
                    start.addAttribute("ui.style", "fill-color:#FF0000; size: 20px;");
                    goal.addAttribute("ui.style", "fill-color:#00FF00; size: 20px;");
                    break;
                } else {
                    it = node.getNeighborNodeIterator();
                    while (it.hasNext()) {
                        Node tmp = (Node) it.next();
                        if(tmp.getAttribute("visited")== null)
                            stack.add(tmp);// add to queue
                    }

                }
            }
            myGraph.display(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void ex2() {
        try {
            myGraph.read("dgs/lab2/completegrid_30.dgs");
            LinkedList<Node> list = new LinkedList<Node>();
            
            Node start = Toolkit.randomNode(myGraph);
            
            Iterator it = start.getNeighborNodeIterator();
            while(it.hasNext()){
                list.add((Node)it.next());
            }
            Node goal = Toolkit.randomNode(myGraph);
            while(start.equals(goal)){//if start == goal than random again
                goal = Toolkit.randomNode(myGraph);
            }
            Node node = null;
            while((node = list.getFirst())!= null){
                list.removeFirst();
                node.addAttribute("ui.style", "fill-color:#FFFF00;");
                
                if(goal.equals(node)){
                    start.addAttribute("ui.style", "fill-color:#FF0000; size: 20px;");
                    goal.addAttribute("ui.style", "fill-color:#00FF00; size: 20px;");
                    break;
                }
                else{
                    it = node.getNeighborNodeIterator();
                    while (it.hasNext()) {
                        list.add((Node)it.next());//add to queue
                    }

                }
            }
            myGraph.display(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void ex1() {
        try {
            myGraph.read("dgs/lab2/firstgraphlab2.dgs");

            for (Node n : myGraph.getEachNode()) {
                Iterator t = n.getNeighborNodeIterator();
                int sum = 0;
                while (t.hasNext()) {
                    Node tmp = (Node) t.next();
                    sum += (int) tmp.getAttribute("cost");
                }
                if (sum >= 30) {
                    n.addAttribute("ui.style", "fill-color:#ff00ff; size: 30px;");
                    n.addAttribute("ui.label", "cost: "+sum);
                }

            }

            myGraph.display(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        new Lab2(args);
    }



    private void hitakey(String msg) {
        System.err.println("\n-----------");
        System.err.println("\t" + msg);
        System.err.println("-----------");
        try {
            System.in.read();
        } catch (IOException ioe) {
        }
    }
}
