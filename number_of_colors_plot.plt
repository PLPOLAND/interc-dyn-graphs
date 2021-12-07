set style data lines 
set xrange [0:256015]
set yrange [0:892]
set xlabel "iteration"
set ylabel "colors on grid"
set label "Consensus made in 256015 iterations" at 300,100
plot 'iterations.txt' using 1:2 with lines lc 2 lw 2 title "colors on grid"

