set style data lines 
set xrange [0:1428]
set yrange [0:220]
set xlabel "iteration"
set ylabel "colors on grid"
set label "Consensus made in 1428 iterations" at 300,100
plot 'iterations.txt' using 1:2 with lines lc 2 lw 2 title "colors on grid"

