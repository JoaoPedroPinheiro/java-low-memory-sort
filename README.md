# Java Low Memory File Sort [![Build Status](https://travis-ci.org/JoaoPedroPinheiro/java-low-memory-sort.svg?branch=master)](https://travis-ci.org/JoaoPedroPinheiro/java-low-memory-sort) 

Utility to sort large files containing integers separated by newline in a memory restricted environment.

The central class is the **IntegerInputSorter**, and the most important method is IntegerInputSorter.**sort(Reader input, Writer output)** that receives a Reader pointing to the input to be sorted, and a Writer pointing to the output. 

## Implementation details

1. The file is read in chunks. Each chunk is sorted and stored in a disk, and a reference to it is kept in memory, in the form of a **ChunkEntry**.
2. After the whole file has been consumed in this way, the chunks are consumed by reading one integer from each and then:
   1. Finding the chunk with the smallest integer
   2. Write it to the output, and update that chunk by reading the next value. 
   3. If the chunk has been completely consumed, remove it from memory, and delete the file.
   4. Repeat until all chunks have been consumed. 



### ChunkEntry

​	The ChunkEntry class works as a wrapper for a BufferedReader pointing to a chunk. The goal was to create functionality similar to a Stack of integers where the user can consume one value at a time up until it is empty. 
