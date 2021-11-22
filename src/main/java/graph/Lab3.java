package graph;

import java.util.Random;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * Class containg methods to solve lab 3
 */
public class Lab3 {

	
	SingleGraph myGraph;
	Random random;
	
	public Lab3(String[] args) {
		random = new Random(System.currentTimeMillis());
		// randomGraph(50,0.05);

		generateTreeGraph(1000,10);
	}
	
	/**
	 * Generates an Tree Graph with given amout of vertices.
	 * @param vertices num of vertices to generate
	 * @param timeSpace how many millis to wait between adding new vertice.
	 */
	private void generateTreeGraph(int vertices, int timeSpace) {
			myGraph = new SingleGraph("RandomGenratedTree");
			myGraph.display();

			myGraph.addNode("0").addAttribute("ui.style", "fill-color: rgb(255," + (0) + ",0);size:10px;");//add a starting Node
			
		for (int i = 1; i < vertices; i++) {
			Node nodeToAttach = Toolkit.randomNode(myGraph); // rand node to attach new node 
			Node newNode = myGraph.addNode(i+""); // init new node
			int g = (int)((i/(double)vertices)*255); // calculate color gradient 
			newNode.addAttribute("ui.style", "fill-color: rgb(" + (255-g) + ","+g+",0);size:10px;");//set new color
			Edge edge = myGraph.addEdge(nodeToAttach.getId()+"_"+newNode.getId(), nodeToAttach, newNode, false);//attach new node to alreadyexisting node 
			Tools.pause(timeSpace);//make some some pause
		}

	}


	/**
	 * Erdos-Renyi model of random graphs
	 * @param n
	 * @param p
	 */
	public void randomGraph(int n, double p) {
		myGraph = new SingleGraph("erdos-renyi");
		myGraph.display();
		// creation of vertices
		for(int i=0;i<n-1;i++) {
			myGraph.addNode("n_"+i);
		}
		// creation of edges according to the probability
		for(int i=0;i<n-2;i++) {
			for(int j=i+1;j<n-1;j++) {
				if(random.nextDouble() < p) {
					myGraph.addEdge("E:"+i+"-"+j, "n_"+i,"n_"+j);
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        new Lab3(args);
	}

}
