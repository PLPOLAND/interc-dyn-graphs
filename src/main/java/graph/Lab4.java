package graph;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.rowset.serial.SerialArray;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import scala.annotation.elidable;

import org.graphstream.graph.Node;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;


public class Lab4 {
    SingleGraph myGraph;
    Random random;
    static int boxSize = 10;
    static int sleepTime = 10;

    static Integer iteration = 0;

    static Viewer viewer;

    public Lab4(String[] args){

        // random = new Random(1);
        random = new Random(System.currentTimeMillis());

        // ex1(100,0.1);
        ex2(15,8*15,15,100);
        
    }

    private void ex2(int n, int threads, int _boxSize, int _sleepTime) {
        boxSize = _boxSize;
        sleepTime = _sleepTime;
        Ex2Entity entity[] = new  Ex2Entity[threads];

        

        myGraph = Tools.grid(n, false, false);
        viewer = myGraph.display(false);
        for (Node node : myGraph.getEachNode()) {
            int r = random.nextInt(255);
            int g = random.nextInt(255);
            int b = random.nextInt(255);

            while (0.2126 * r + 0.7152 * g + 0.0722 * b > 220) {
                r = random.nextInt(255);
                g = random.nextInt(255);
                b = random.nextInt(255);
            }
            node.addAttribute("ui.style", "fill-color: rgb("+r+","+g+","+b+");shape: box;size:"+boxSize+"px;");
            node.addAttribute("alive", 0);
            node.addAttribute("color", ""+r+","+g+","+b+"");
            for (Edge edge : node.getEachEdge()) {
                edge.addAttribute("ui.style", "fill-color: rgb(255,255,255);");
            }
        }

        // Tools.pause(1000);
        for (int i = 0; i < threads; i++)
            entity[i] = new Ex2Entity(myGraph);
        for (Ex2Entity ex2Entity : entity) {
            ex2Entity.start();
        }
        boolean isConsensus = false;
        String consensusColor = "";
        while (!isConsensus) {
            consensusColor = Toolkit.randomNode(myGraph).getAttribute("color");
            synchronized (myGraph) {
                for (Node node : myGraph.getEachNode()) {
                    if(consensusColor.equals(node.getAttribute("color"))){
                        isConsensus = true;
                    }
                    else{
                        isConsensus = false;
                        break;
                    }
                }
            }
        }
        System.out.println("Killing Nodes:");
        System.out.flush();
        for (Ex2Entity ex2Entity : entity) {
            ex2Entity.endWork = true;
        }
        Tools.pause(1000);//wait for thread's to end work
        System.out.println("Consensus Approched after: "+ iteration+" iterations");
    }

    //GAME_OF_LIFE
    public void ex1(int n ,double alive) {
        boxSize = 5;

        myGraph = Tools.grid(n,true,false);
        myGraph.display(false);
        for (Node node : myGraph.getEachNode()) {
            node.addAttribute("ui.style", "fill-color: rgb(255,255,255);shape: box;size:"+boxSize+"px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
            node.addAttribute("alive", 0);
            for (Edge edge : node.getEachEdge()) {
                edge.addAttribute("ui.style", "fill-color: rgb(255,255,255);");
            }
        }

        initState(myGraph, alive);
        Tools.hitakey("hit to start GOL");
        while (true) {
            Tools.pause(100);
            for (Node node : myGraph.getEachNode()) {//for each cell(node)
                int howManyAliveNeighbor = 0;
                Iterator<Node> iter = node.getNeighborNodeIterator();
                
                while (iter.hasNext()) {//count alive neighbor
                    Node friend = iter.next();
                    if ((int)friend.getAttribute("alive")==1) {
                     howManyAliveNeighbor++;   
                    }
                }

                if((int) node.getAttribute("alive") == 1){//GL logic
                    if(howManyAliveNeighbor == 2 || howManyAliveNeighbor == 3){
                        ;
                    }
                    else{
                        markDead(node);
                    }
                }
                else{
                    if(howManyAliveNeighbor == 3){
                        markAlive(node);
                    }
                }
            }
        }

    }

    private void initState(SingleGraph graph, double proc){
        int howManyNodes= graph.getNodeCount();
        int toMakeAlive = (int)(howManyNodes * proc);

        while (toMakeAlive-- !=0) {
            Node n = Toolkit.randomNode(myGraph,random);
            if((int)n.getAttribute("alive") == 0){
                markAlive(n);
            }
            else{
                toMakeAlive++;
            }
        }

    }

    private void markAlive(Node node){
        node.removeAttribute("ui.style");
        node.addAttribute("ui.style",
                "fill-color: #000000;shape: box;size:"+boxSize+"px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
        node.changeAttribute("alive", 1);
    }
    private void markDead(Node node){
        node.removeAttribute("ui.style");
        node.addAttribute("ui.style",
                "fill-color: #FFFFFF;shape: box;size:"+boxSize+"px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
        node.changeAttribute("alive", 0);
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        new Lab4(args);
    }
}


/**
 * ex2 thread entity 
 */
class Ex2Entity extends Thread{
    boolean endWork = false;

    boolean makeNext = false;

    static SingleGraph graph;

    HashMap<String,Integer> colors;
    
    ReentrantLock lock = new ReentrantLock();
    ReentrantLock lock2 = new ReentrantLock();

    String bestColor = "";
    int bestColorVal = 0;

    Random rand = new Random(System.currentTimeMillis());
    Ex2Entity(SingleGraph graph2){
        super();
        this.graph = graph2;
        makeNext = true;

    }


    public void run(){
        while (!endWork) {
            try {
                Tools.pause(Lab4.sleepTime);
                // System.out.println("!kill");
                synchronized(Lab4.iteration){
                    Lab4.iteration++;
                }
                bestColor ="";
                bestColorVal= 0;
                // if (makeNext) {
                    // System.out.println("makeNext: " + this.getName());
                    colors = new HashMap<>();
                    Node node = Toolkit.randomNode(graph);
                    if(lock.tryLock(10, TimeUnit.SECONDS)){
                        try {
                            // System.out.println(this.getName() + " startof :" + node.getId());
                            Iterator<Node> neigh = node.getNeighborNodeIterator();
                            while (neigh.hasNext()) {// compute number of colors
                                Node currNode = neigh.next();
                                String color = getColorOfNode(currNode);
                                if (colors.containsKey(color)) {
                                    colors.replace(color, colors.get(color) + 1);//increment amount of this color
                                } else {
                                    colors.put(color, 1);// add new color to map;
                                }

                            }
                            //find the most popular color 
                            colors.forEach((color, val) -> {
                                if (val > bestColorVal) {
                                    bestColorVal = val;
                                    bestColor = color;
                                }
                            });


                            if (bestColorVal == 1) {// if there is no most color, just rand it;
                                
                                bestColor = (String) colors.keySet().toArray()[rand.nextInt(colors.size())];
                            }

                            // set new color to neighbor
                            neigh = node.getNeighborNodeIterator();
                            while (neigh.hasNext()) {// set new color

                                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                                Node currNode = neigh.next();
                                    try {//TODO there is the problem with NoSuchElementException and with breaking viewer of graph...
                                        changeColorOfNode(currNode);
                                    } 
                                    catch(NoSuchElementException e){
                                        System.out.println("Error on " + this.getName());
                                        System.out.flush();
                                        e.printStackTrace();
                                        System.exit(-1);
                                        // graph.display(false);
                                        // currNode.removeAttribute("ui.style");
                                        // currNode.addAttribute("ui.style", "fill-color: rgb(" + bestColor+ ");shape: box;size:" + Lab4.boxSize+ "px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
                                    }
                                    finally{
                                        lock.unlock();
                                    }
                                }
                            }

                            changeColorOfNode(node);

                            makeNext = false;
                            // System.out.println(this.getName() + " end of :" + node.getId());
                        } finally {
                            lock.unlock();
                        }
                        
                    }
                    
                // }
                } catch (Exception e) {
                    e.printStackTrace();
                    endWork = true;
                }
            }
            System.out.println("End of thread: " + this.getName());
            
    }

    private synchronized String getColorOfNode(Node currNode) {
        synchronized(graph){
            return currNode.getAttribute("color");
        }
    }

    private synchronized void changeColorOfNode(Node currNode) {
        synchronized(graph){
        currNode.setAttribute("color", bestColor);
        currNode.setAttribute("ui.style", "fill-color: rgb(" + bestColor
                + ");shape: box;size:" + Lab4.boxSize
                + "px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
        }
    }


    /**
     * allow thread to make next thing
     */
    public void makeNext(){
        this.makeNext = true;
    }

    /**
     * stopping thread
     */
    public void killThread(){
        endWork = true;
    }
}
