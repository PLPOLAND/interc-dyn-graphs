package graph;

import java.io.IOException;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;

public class Tools {

	// =============== GENERATORS ==================


	public static SingleGraph grid(int n, boolean moore, boolean torus) {
		return grid(n, moore, torus,"", "") ;
	}


	/**
	 * Grid generator with VonNeumann neighborhood
	 * --> every vertex non located on the border will have exactly
	 * 4 neighbors (north, south, east and west)
	 * The algorithm consists first in creating the vertices and
	 * then to add edges between them.
	 * The position of the vertices will be fixed.
	 * 
	 * @param n
	 */
	public static SingleGraph grid(int n, boolean moore, boolean torus,String postfix, String graphName) {
		SingleGraph myGraph = new SingleGraph("grid von Neumann or Moore "+graphName);
		// creation of nodes
		for (int line = 0; line < n; line++) {
			for (int col = 0; col < n; col++) {
				Node v = myGraph.addNode(line + "-" + col+postfix);
				v.addAttribute("x", col);
				v.addAttribute("y", line);
			}
		}
		// creation of edges for von Neumann neighborhood
		int val = 1;
		if (torus)
			val = 0;
		for (int line = 0; line < n; line++) {
			for (int col = 0; col < n - val; col++) {
				int colplusone = (col + 1) % n;
				Node v = myGraph.getNode(line + "-" + col+postfix);
				Node w = myGraph.getNode(line + "-" + colplusone+postfix);
				Edge e = myGraph.addEdge(v.getId()+ "_" + w.getId()+postfix, v.getId(), w.getId());
			}
		}

		for (int col = 0; col < n; col++) {
			for (int line = 0; line < n - val; line++) {
				int lineplusone = (line + 1) % n;
				Node v = myGraph.getNode(line + "-" + col+postfix);
				Node w = myGraph.getNode(lineplusone + "-" + col+postfix);
				Edge e = myGraph.addEdge(v.getId() + "_" + w.getId()+postfix, v.getId(), w.getId());
			}
		}
		if (moore) {
			// creation of additional edges corresponding to Moore neighborhood
			// addition of north-east, north-west, south-east and south-west neighbors
			for (int line = 0; line < n - val; line++) {
				for (int col = 0; col < n - val; col++) {
					int colplusone = (col + 1) % n;
					int lineplusone = (line + 1) % n;
					Node v = myGraph.getNode(line + "-" + col+postfix);
					Node w = myGraph.getNode(lineplusone + "-" + colplusone+postfix);
					Edge e = myGraph.addEdge(v.getId() + "_" + w.getId()+postfix, v.getId(), w.getId());
				}
			}
			for (int line = val; line < n; line++) {
				for (int col = 0; col < n - val; col++) {
					int colplusone = (col + 1) % n;
					int lineminusone = (n + line - 1) % n;
					Node v = myGraph.getNode(line + "-" + col+postfix);
					Node w = myGraph.getNode(lineminusone + "-" + colplusone + postfix);
					Edge e = myGraph.addEdge(v.getId() + "_" + w.getId()+postfix, v.getId(), w.getId());
				}
			}
		}
		return myGraph;
	}

	/**
	 * Full-connected (also known as complete) graphs generator
	 * any vertex is connected to all the other vertices
	 * 
	 * @param n the number of nodes
	 */

	public static SingleGraph fullConnectedGraph(int n) {
		SingleGraph myGraph = new SingleGraph("full-connected");
		// creation of nodes
		Node no = myGraph.addNode("n_0");
		no.addAttribute("ui.style", "fill-color:red;size:10px;");
		for (int i = 1; i < n; i++) {
			myGraph.addNode("n_" + i);
		}
		// creation of edges
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				Edge e = myGraph.addEdge("E:" + i + "-" + j, "n_" + i, "n_" + j);
				if (i == 0)
					e.addAttribute("ui.style", "fill-color:red;size:3px;");
			}
		}
		return myGraph;
	}

	/**
	 * Erdos-Renyi model of random graphs
	 * Nota: if p=1 then the graph is full-connected
	 * 
	 * @param n number of nodes
	 * @param p probability of creation of any edge
	 */
	public static SingleGraph randomGraph(int n, double p) {
		SingleGraph myGraph = new SingleGraph("erdos-renyi");
		Random alea = new Random(System.currentTimeMillis());
		// creation of vertices
		for (int i = 0; i < n - 1; i++) {
			myGraph.addNode("n_" + i);
		}
		// creation of edges according to the probability
		for (int i = 0; i < n - 2; i++) {
			for (int j = i + 1; j < n - 1; j++) {
				if (alea.nextDouble() < p) {
					myGraph.addEdge("E:" + i + "-" + j, "n_" + i, "n_" + j);
				}
			}
		}
		return myGraph;
	}

	// ============== VARIOUS TOOLS ================

	/**
	 * take a screenshot of the current graph
	 * 
	 * @param graph
	 * @param pathToImage
	 */
	public static void screenshot(SingleGraph graph, String pathToImage) {
		if (graph != null)
			if (graph.getNodeCount() > 0) {
				FileSinkImages fsi = new FileSinkImages(FileSinkImages.OutputType.PNG, FileSinkImages.Resolutions.SVGA);
				fsi.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
				try {
					fsi.writeAll(graph, pathToImage);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}

	/**
	 * generate a graph from a dgs file
	 * 
	 * @param pathtodgs
	 * @return
	 */
	public static SingleGraph read(String pathtodgs) {
		SingleGraph graph = new SingleGraph("");
		try {
			graph.read(pathtodgs);
		} catch (Exception e) {
		}
		return graph;
	}

	/**
	 * pause the current execution
	 * 
	 * @param delay
	 */
	public static void pause(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException ie) {
		}
	}

	public static void hitakey(String msg) {
		System.err.println("\n-----------");
		System.err.println("\t" + msg);
		System.err.println("-----------");
		try {
			System.in.read();
		} catch (IOException ioe) {
		}
	}
}