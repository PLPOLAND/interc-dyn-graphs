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


/**
 * Class with implements of ex from lab4
 */
public class Lab4 {
    SingleGraph myGraph;
    static public Random random;
    static int boxSize = 10;
    static int sleepTime = 10;

    static Integer iteration = 0;

    public Lab4(String[] args) {

        // random = new Random(1321);
        random = new Random(System.currentTimeMillis());

        // ex1(100,0.1);
        // ex2(15,8*15,15,100);
        // ex2(30,100,15,50, true);
        ex2_multirun(20, 100, 20, 100, 10, true);

    }


    private int ex2(int n, int threads, int _boxSize, int _sleepTime, boolean moore) {
        return ex2(n, threads, _boxSize, _sleepTime, moore,"","graph");
    }

    /**
     * 
     * @param n size of grid (N x N)
     * @param threads how many threads should be run
     * @param _boxSize size of the node
     * @param _sleepTime waiting time between next threds executions
     * @return number of iterations needed to aproch Consensus
     */
    private int ex2(int n, int threads, int _boxSize, int _sleepTime, boolean moore, String postFix, String graphName) {

        Random random = new Random(System.currentTimeMillis());
        // Random random = new Random(1321);
        SingleGraph myGraph = null;
        boxSize = _boxSize;
        sleepTime = _sleepTime;
        Ex2Entity entity[] = new Ex2Entity[threads];

        myGraph = Tools.grid(n, true, false, postFix,graphName);
        myGraph.display(false);
        for (Node node : myGraph.getEachNode()) {
            int r = random.nextInt(255);
            int g = random.nextInt(255);
            int b = random.nextInt(255);

            while (0.2126 * r + 0.7152 * g + 0.0722 * b > 200) {//to make colors darker
                r = random.nextInt(255);
                g = random.nextInt(255);
                b = random.nextInt(255);
            }
            node.addAttribute("ui.style",
                    "fill-color: rgb(" + r + "," + g + "," + b + ");shape: box;size:" + boxSize + "px;");
            node.addAttribute("color", "" + r + "," + g + "," + b + "");
            for (Edge edge : node.getEachEdge()) {
                edge.addAttribute("ui.style", "fill-color: rgb(255,255,255);");
            }
        }

        boolean isConsensus = false;
        String consensusColor = "";

        if (threads == 1) {//if 1 thread than print statistics for run (for gnuplot)
            for (int i = 0; i < threads; i++)
                entity[i] = new Ex2Entity(myGraph, true);//use local thread variable to store iterations
            
            int maxColorsNumber = 0;
            GnuplotPrint print = new GnuplotPrint("iterations.txt");
            print.printHeader("");
            while (!isConsensus) {
                entity[0].proceedConsensus();// make decisions for one node and it's neighobour

                consensusColor = Toolkit.randomNode(myGraph).getAttribute("color");
                for (Node node : myGraph.getEachNode()) {// check if there is consensus
                    if (consensusColor.equals(node.getAttribute("color"))) {
                        isConsensus = true;
                    } else {
                        isConsensus = false;
                        break;
                    }
                }
                HashMap<String, Integer> colorsOnTheGrid = countCollors(myGraph);
                maxColorsNumber = Integer.max(maxColorsNumber, colorsOnTheGrid.size());

                // System.out.println(colorsOnTheGrid.toString());//debug print colors in grid

                print.printLine(entity[0].iterations, colorsOnTheGrid.size());//save statistic to file
            }
            // consensus made
            print.close();//close file
            print.createPLT(entity[0].iterations, maxColorsNumber);//rewrite the gnuplot file
            System.out.println("Consensus approched after: " + entity[0].iterations + " iterations");
            int tmp = entity[0].iterations;
            entity[0].stopThread();

            for (Ex2Entity ex2Entity : entity) {
                try {
                    ex2Entity.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return tmp;
        } else {
            for (int i = 0; i < threads; i++){
                entity[i] = new Ex2Entity(myGraph, false);
                Tools.pause(10);
            }
            // Tools.pause(1000);
            for (Ex2Entity ex2Entity : entity) {
                ex2Entity.start();
            }
            while (!isConsensus) { // check if all cells have same collor
                consensusColor = Toolkit.randomNode(myGraph).getAttribute("color");
                synchronized (myGraph) {
                    for (Node node : myGraph.getEachNode()) {
                        if (consensusColor.equals(node.getAttribute("color"))) {
                            isConsensus = true;
                        } else {
                            isConsensus = false;
                            break;
                        }
                    }
                }
            }
            System.out.println("Killing Nodes");
            System.out.flush();
            for (Ex2Entity ex2Entity : entity) {
                ex2Entity.stopThread();
            }
            for (Ex2Entity ex2Entity : entity) {
                try {
                    ex2Entity.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Consensus approched after: " + iteration + " iterations");
            return iteration;
        }

    }
    
    /**
     * Runs ex2 multiple times for calculate average number of iterations
     * 
     * @param n             size of grid (N x N)
     * @param threads       how many threads should be run
     * @param _boxSize      size of the node
     * @param _sleepTime    waiting time between next threds executions
     * @param multiruns     how many runs will be done
     * @param moore         
     */
    public void ex2_multirun(int n, int _threads, int _boxSize, int _sleepTime, int multiruns, boolean moore) {
        int sum = 0;
        int executions = 0;

        //NOT WORKING, JUST ONE GRAPH WINDOW IS UPDATED :(
        // int [] iterations = new int [multiruns];
        // Thread[] threads = new Thread[_threads];
        // for (int i = 0; i < iterations.length; i++) {
        //     for (int j = 0; j < threads.length; j++) {
        //         if(i<iterations.length){
        //             threads[j]= new Thread(""+j){
        //                 public void run() {
        //                     int tmp = ex2(n,10,_boxSize,_sleepTime,true,this.getName(),this.getName());
        //                     synchronized(iterations){
        //                         iterations[Integer.parseInt(this.getName())] = tmp;
        //                     }
        //                 };
        //             };
        //             threads[j].start();
        //         }
        //         i++;
        //     }
        //     for (int j2 = 0; j2 < threads.length; j2++) {
        //         if(threads[j2] !=null){
        //             try {
        //                 threads[j2].join();
        //             } catch (InterruptedException e) {
        //                 e.printStackTrace();
        //             }
        //             threads[j2] = null;
        //         }
        //     }
        // }



        for (int i = 0; i < multiruns; i++) {
            System.out.println("Runing run: " + i);
            sum += ex2(n, _threads, _boxSize, _sleepTime,moore);
            executions++;
            Lab4.iteration =0;
        }
        
        System.out.println("Consensus reached on average in " + (sum / executions) + " iterations");

    }

    /**
     * Counts number of different colors on graph
     * 
     * @param graph
     * @return
     */
    private HashMap<String, Integer> countCollors(SingleGraph graph) {
        HashMap<String, Integer> colors = new HashMap<>();
        for (Node node : graph) {
            String color = Ex2Entity.getColorOfNode(node);
            if (colors.containsKey(color)) {
                colors.replace(color, colors.get(color) + 1);// increment amount of this color
            } else {
                colors.put(color, 1);// add new color to map;
            }
        }

        return colors;
    }

    // GAME_OF_LIFE
    public void ex1(int n, double alive) {
        boxSize = 5;

        myGraph = Tools.grid(n, true, false);
        myGraph.display(false);
        for (Node node : myGraph.getEachNode()) {
            node.addAttribute("ui.style", "fill-color: rgb(255,255,255);shape: box;size:" + boxSize
                    + "px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
            node.addAttribute("alive", 0);
            for (Edge edge : node.getEachEdge()) {
                edge.addAttribute("ui.style", "fill-color: rgb(255,255,255);");
            }
        }

        initState(myGraph, alive);
        Tools.hitakey("hit to start GOL");
        while (true) {
            Tools.pause(100);
            for (Node node : myGraph.getEachNode()) {// for each cell(node)
                int howManyAliveNeighbor = 0;
                Iterator<Node> iter = node.getNeighborNodeIterator();

                while (iter.hasNext()) {// count alive neighbor
                    Node friend = iter.next();
                    if ((int) friend.getAttribute("alive") == 1) {
                        howManyAliveNeighbor++;
                    }
                }

                if ((int) node.getAttribute("alive") == 1) {// GL logic
                    if (howManyAliveNeighbor == 2 || howManyAliveNeighbor == 3) {
                        ;
                    } else {
                        markDead(node);
                    }
                } else {
                    if (howManyAliveNeighbor == 3) {
                        markAlive(node);
                    }
                }
            }
        }

    }

    /**
     * makes init state for GL algorithm
     * 
     * @param graph
     * @param proc  % of alive cells
     */
    private void initState(SingleGraph graph, double proc) {
        int howManyNodes = graph.getNodeCount();
        int toMakeAlive = (int) (howManyNodes * proc);

        while (toMakeAlive-- != 0) {
            Node n = Toolkit.randomNode(myGraph, random);
            if ((int) n.getAttribute("alive") == 0) {
                markAlive(n);
            } else {
                toMakeAlive++;
            }
        }

    }

    private void markAlive(Node node) {
        node.removeAttribute("ui.style");
        node.addAttribute("ui.style",
                "fill-color: #000000;shape: box;size:" + boxSize
                        + "px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
        node.changeAttribute("alive", 1);
    }

    private void markDead(Node node) {
        node.removeAttribute("ui.style");
        node.addAttribute("ui.style",
                "fill-color: #FFFFFF;shape: box;size:" + boxSize
                        + "px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
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
class Ex2Entity extends Thread {
    boolean endWork = false;

    static SingleGraph graph;

    HashMap<String, Integer> colors;

    ReentrantLock lock = new ReentrantLock();

    String bestColor = "";
    int bestColorVal = 0;

    boolean useLocalIteration = false;
    int iterations = 0;

    Random rand = new Random(System.currentTimeMillis());

    Ex2Entity(SingleGraph graph2, boolean _useLocalIteration) {
        super();
        graph = graph2;
        this.useLocalIteration = _useLocalIteration;
        this.setName(""+rand.nextInt());
    }

    public void run() {
        while (!endWork) {
            proceedConsensus();
        }
        // System.out.println("End of thread: " + this.getName());
        // System.out.flush();
    }
    /**
     * finds consensus for one node and it's neighbor
     */
    public void proceedConsensus() {
        try {
            Tools.pause(Lab4.sleepTime);
            // System.out.println("!kill");
            if (useLocalIteration == true) {
                iterations++;
            }
            else{
                synchronized (Lab4.iteration) {
                    Lab4.iteration++;
                }
            }
            bestColor = "";
            bestColorVal = 0;
            colors = new HashMap<>();
            Node node = Toolkit.randomNode(graph);
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    Iterator<Node> neigh = node.getNeighborNodeIterator();
                    while (neigh.hasNext()) {// compute number of colors
                        Node currNode = neigh.next();
                        String color = getColorOfNode(currNode);
                        if (colors.containsKey(color)) {
                            colors.replace(color, colors.get(color) + 1);// increment amount of this color
                        } else {
                            colors.put(color, 1);// add new color to map;
                        }

                    }
                    // find the most popular color
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
                            try {
                                changeColorOfNode(currNode);// change color of this one
                            } catch (NoSuchElementException e) {
                                System.out.println("Error on " + this.getName());
                                System.out.flush();
                                e.printStackTrace();
                                System.exit(-1);
                            } finally {
                                lock.unlock();
                            }
                        }
                    }

                    changeColorOfNode(node);

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

    /**
     * get color from string
     * 
     * @param currNode node to get color
     * @return color as String
     */
    synchronized static String getColorOfNode(Node currNode) {
        synchronized (graph) {
            return currNode.getAttribute("color");
        }
    }

    private synchronized void changeColorOfNode(Node currNode) {
        synchronized (graph) {
            currNode.setAttribute("color", bestColor);
            currNode.setAttribute("ui.style", "fill-color: rgb(" + bestColor
                    + ");shape: box;size:" + Lab4.boxSize
                    + "px; stroke-mode: plain; stroke-width:1px; stroke-color:#000000;");
        }
    }

    /**
     * stopping thread
     */
    public void stopThread() {
        endWork = true;
    }
}
