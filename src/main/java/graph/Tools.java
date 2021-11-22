package graph;

import java.io.IOException;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;

public class Tools {


	public static void screenshot(SingleGraph graph, String pathToImage) { 
		if(graph != null) if(graph.getNodeCount() > 0) {
			FileSinkImages fsi = new FileSinkImages( FileSinkImages.OutputType.PNG, FileSinkImages.Resolutions.SVGA);
			fsi.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
			try {
				fsi.writeAll(graph, pathToImage);
			} catch (IOException e) { e.printStackTrace(); } 
		}
	}
	
	
	public static SingleGraph read(String pathtodgs) {
		SingleGraph graph = new SingleGraph("");
		try {
			graph.read(pathtodgs);
		} catch(Exception e) { }
		return graph;
	}
	

	public static void pause(long delay) {
		try {
			Thread.sleep(delay);
		} catch(InterruptedException ie) {}
	}

}
