# LatteMetal

LatteMetal is my own-brewed RISC-based ISA and micro-architecture written in Java. 

It is a pipelined superscalar implementation which focuses on hardware-optimisation to achieve amiable performance.

# Development and running guide

This requires you have javac and java versions at least ``17.0.2``

- ``$ ./run_noargs`` in this directory will run the processor with the default program (2x2 matrix multiplication) and some preset memory.
- ``$ javac -d ./class Main.java && java -cp ./class Main [ASSEMBLY FILE PATH : string].latte [MEMORY ROW LENGTH : integer] `` in this directory will run the processor on a specified assembly program (all located in ./assembly) and will show the resulting memory with a row length MEMORY ROW LENGTH.

For example, to

### Lab 1 tips 

- Simulator can get complex quickly 
- OOP Languages are good for this 
- Like a state machine, one cycle of the core is a change in state
  - A function call tick will be a cycle that changes the state; dont feel you need to go super low-level
  - 'add' can be an add rather than binary logic
- Start wide, create a skeleton which has the full width of the implementation
  - Think ahead as you implement your simulator, like if you want five execution units create a skeleton for this
- Don't overcomplicate your instruction set 
- Experiments with your simulator are as important as the simulator itself 
  - Spend ~60% time on sim, ~%40 time on experiments 
  - Plan experiments early so you know what to implement in your simulator 
- Debugging; log output of architectural state
  - E.g. outputting register values 
  - Track instructions flowing through the pipeline 
  - Stepping modes e.g. keypress-activated clock cycle 
  - Don't spend all your time on this 
- Set of workloads with outputs are suitable for testing programs 
  - Test that your sim is functionally correct as you develop it
- OoO memory operations theres a queue called a load-store queue 
  - Tracks in-flight memory requests, and checks their order
  - Worth looking into when we get there 
- Assembly code can use the assembly language strings, where each line is the address of the PC
  - It is an extension though; compiling, decoding etc. 
- Memory; somewhere to store the memory youre interacting with (L1 datacache accesses are ~4-5 cycles where addresses are like 0-1000)
- Register; some vector of values 
- Instructions that take n cycles can have an internal counter to abstract the different stages they go through
  - The same is true for memory accesses or anything that takes non-zero time
- What does the decode stage look like if our instructions are just strings?
  - it could let the branch predictor know if this is a branch; early (mis)prediction

# Devlog

@ 01/02/24

Instruction cache will be stored as integers! 
an instruction may be 
``big end << [opcode 0-11 (5b)] [register_s 0-31 (5b)] [register_s 0-31 (5b)] [register_t 0-31 (5b)] [immediate 0-8192 (12b)] small end >>``

# Interim

Plan benchmarks for the interim etc.
