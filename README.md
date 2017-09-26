# A1 - Neighborhood Score: Benchmarking

This is the first assignment for PDPMR

It gives the neighborhood score for each word in the corpus

Pre-requisites:
1. Java 8 installed
2. R installed

For running the project, open the terminal (unix/linux) and run the below command:
make

Before running make sure that the input folder has .gz files. If not run the below command
before running make command
gzip input/*

Also make sure that an out folder is there in the same path as src folder.

By default, the Makefile runs the Benchmark file. It prompts for neighborhood value
as well as number of iterations. 

It first runs sequential version for the given neighborhood and number of iterations.
Then it runs the parallel version with threads 2-16 for the given neighborhood and number of iterations.

It generates 4 outputs. 1 output each for sequential and parallel. 1 output each for the execution 
times of each variant.

R takes the output with execution times and generates an R markdown report.