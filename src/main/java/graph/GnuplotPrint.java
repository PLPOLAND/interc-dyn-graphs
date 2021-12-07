package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class GnuplotPrint {

    PrintWriter writer;

    GnuplotPrint(File f){
        try {
            writer = new PrintWriter(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                writer =new PrintWriter(new File("gnuplot.txt"));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.exit(-1);
            }
        }
    }

    GnuplotPrint(String s){
        try {
            writer = new PrintWriter(new File(s));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                writer = new PrintWriter(new File("gnuplot.txt"));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public void printHeader(String s) {
        if (s.equals("")) {
            writer.println("#iteration number_of_colors");
        }
        else{
            writer.println(s);
        }
    }
    public void printLine(int iteration, int number_of_colors) {
        writer.println(iteration +" " + number_of_colors);
    }
    public void close() {
        writer.flush();
        writer.close();
    }
    /**
     * @warning call after close() method!
     * @param iterations
     */
    public void createPLT(int iterations, int maxColorsNumber){
        try {
            writer = new PrintWriter(new File("number_of_colors_plot.plt"));

            writer.println("set style data lines \n"
                +"set xrange [0:"+iterations+"]\n"
                +"set yrange [0:"+maxColorsNumber+"]\n"
                +"set xlabel \"iteration\"\n"
                +"set ylabel \"colors on grid\"\n"
                +"set label \"Consensus made in "+iterations+" iterations\" at 300,100\n"
                +"plot 'iterations.txt' using 1:2 with lines lc 2 lw 2 title \"colors on grid\"\n");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
