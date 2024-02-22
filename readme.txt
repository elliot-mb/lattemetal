# LatteMetal

LatteMetal is my own-brewed RISC-based ISA and micro-architecture written in Java. 

It is a pipelined superscalar implementation which focuses on hardware-optimisation to achieve amiable performance.

# Development and running guide

This requires you have javac and java versions at least ``17.0.2``
The below commands are of the form ``$ command`` to represent the shell.

- ``$ ./run_noargs`` in this directory will run the processor with the default program (2x2 matrix multiplication) and some preset memory.
- ``$ javac -d ./class Main.java && java -cp ./class Main [ASSEMBLY FILE PATH : string].latte [MEMORY ROW LENGTH : integer] `` in this directory will run the processor on a specified assembly program (all located in ./assembly) and will show the resulting memory with a row length MEMORY ROW LENGTH.
  - For example, to run my vector dot product benchmark displaying 10 integers for each line of memory, please run ``$ javac -d ./class Main.java && java -cp ./class Main ./assembly/vec_dot.latte 10``
    If all goes well an output like the following should be observed:
    ````
      ./assembly/vec_dot.latte
      'addi    t0 zero #10'
      ...program
      'st      s6 s3 #0'

      run: program finished in 887 cycles
      registers (dirty):
      t0     10
      s0     10
      s1     20
      s2     30
      s3     40
      s4     425
      s5     25
      s6     7358
      s11    1
      memory:
      [00]  10          [01]  20          [02]  30          [03]  40          [04]  0           [05]  0           [06]  0           [07]  0           [08]  0           [09]  0           
      [10]  78          [11]  9           [12] -15          [13]  22          [14] -4           [15]  65          [16]  11          [17]  2           [18]  10          [19]  18          
      [20]  58          [21]  11          [22] -11          [23]  6           [24] -4           [25]  51          [26]  51          [27]  4           [28]  51          [29]  17          
      [30]  48          [31]  12          [32] -8           [33]  24          [34]  20          [35]  25          [36]  31          [37]  10          [38]  19          [39]  25          
      [40]  7358        [41]  15          [42] -81          [43]  31          [44] -4           [45]  54          [46]  14          [47]  23          [48]  41          [49]  4           
      [50]              [51]              [52]              [53]              [54]              [55]              [56]              [57]              [58]              [59]              
      [60]              [61]              [62]              [63]   
    ````


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
