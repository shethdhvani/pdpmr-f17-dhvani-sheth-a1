all: gunzip build run gzip

build: 
	javac -d out src/*.java
	
gzip:
	gzip input/*
	
gunzip:
	gunzip input/*

run:
	java -cp out Benchmark
	Rscript -e 'rmarkdown::render("report.Rmd")'