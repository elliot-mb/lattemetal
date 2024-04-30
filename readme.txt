# LatteMetal

LatteMetal is my own-brewed RISC-based ISA and micro-architecture written in Java. 

It is a pipelined superscalar implementation which focuses on hardware-optimisation to achieve amiable performance.

# Startup & running guide

This requires you have javac and java versions at least 17.0.2
The below commands are of the form "$ command" to represent the shell.

- The program take the following arguments (<key>=<value> or where no value applies it is a flag, if unused a default is provided):

    prog=<assembly_file_path>                               (program to run)
                                                            (default "./benchmark_assembly/collatz.latte")

    testing                                                 (run test kernels)
                                                            (default not included)

    width=<num_columns>                                     (prints memory at end of program as a table)
                                                            (default 1)

    quiet                                                   (pipeline view is hidden)
                                                            (default not included)

    predictor=<"fixedTaken" | "fixedNotTaken" | "bckTknFwdNTkn" | "bckNTknFwdTkn" | "oneBit" | "twoBit">
                                                            (default "twoBit")

    btb_size=<num_entries>                                  (how many branches we can hold the prediction of)
                                                            (default 32)

    ss_width=<num_n_way>                                    (n way superscalar)
                                                            (default 8)

    alus=<num_alus>                                         (up to eight)
                                                            (default 4)

    lsus=<num_lsus>                                         (up to four)
                                                            (default 2)

    brus=<num_brus>                                         (up to four branch units)
                                                            (default 2)

    alu_rss=<num_n>                                         (any number n >= 1 alu reservation stations)
                                                            (default 8)

    lsu_rss=<num_n>                                         (any number n >= 1 lsu reservation stations)
                                                            (default 4)

    bru_rss=<num_n>                                         (any number n >= 1 bru reservation stations)
                                                            (default 4)

    dp_acc=<num_decimal_places>                             (stat decimal-point accuracy in trailing digits)
                                                            (default 4)

    rob_size=<num_entries>                                  (default 64)

    phys_regs=<size_prf>                                    (default 128)

    align_fetch                                             (fetch is aligned)
                                                            (default not included)

    show_commit                                             (print all instructions committed in order)
                                                            (default not included)

- For example, to run my vector dot product test kernel displaying 10 integers for each line of memory, please run

    $ javac -d ./class Main.java && java -cp ./class Main prog="./assembly/vec_dot.latte" width=10

    ...
    ...(pipeline view)
    ...
    registers (dirty):      t0:10   s0:10   s1:20   s2:30   s3:40   s4:-2400        s5:120  s6:-8550        s11:1
    memory:
    [00]  10          [01]  20          [02]  30          [03]  40          [04]  0           [05]  0           [06]  0           [07]  0           [08]  0           [09]  0
    [10]  92          [11]  84          [12]  76          [13]  68          [14]  50          [15]  42          [16]  34          [17]  26          [18]  18          [19]  0
    [20] -2           [21] -4           [22] -6           [23] -8           [24] -10          [25] -12          [26] -14          [27] -16          [28] -18          [29] -20
    [30]  12          [31]  24          [32]  36          [33]  48          [34]  51          [35]  62          [36]  74          [37]  86          [38]  98          [39]  120
    [40] -8550        [41] -1           [42] -1           [43] -1           [44] -1           [45] -1           [46] -1           [47] -1           [48] -1           [49] -1
    [50]              [51]              [52]              [53]              [54]              [55]              [56]              [57]              [58]              [59]
    [60]              [61]              [62]              [63]
    settings: CLOCK_SPEED_MHZ=500.0
    settings: PREDICTOR=twoBit
    settings: BTB_CACHE_SIZE=32
    settings: SUPERSCALAR_WIDTH=8
    settings: ALU_COUNT=4
    settings: LSU_COUNT=2
    settings: BRU_COUNT=2
    settings: ALU_RS_COUNT=8
    settings: LSU_RS_COUNT=4
    settings: BRU_RS_COUNT=4
    settings: DP_ACC=4
    settings: ROB_ENTRIES=64
    settings: PHYSICAL_REGISTER_COUNT=128
    run: program finished in 63 cycles
    run: program finished after committing 118 instructions
    run: program incorrectly speculated and thereby flushed 27 instructions
    run: instructions per cycle 1.873
    run: cpu time 0.126μs @ 500.0MHz
    run: percentage mispredicted instructions added to rob 18.6207%
    run: percentage mispredicted branches 27.2727%
    mem: [10, 20, 30, 40, 0, 0, 0, 0, 0, 0, 92, 84, 76, 68, 50, 42, 34, 26, 18, 0, -2, -4, -6, -8, -10, -12, -14, -16, -18, -20, 12, 24, 36, 48, 51, 62, 74, 86, 98, 120, -8550, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    registers (dirty):      t0:10   s0:10   s1:20   s2:30   s3:40   s4:-2400        s5:120  s6:-8550        s11:1
