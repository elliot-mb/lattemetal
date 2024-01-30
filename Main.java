import java.util.function.BiFunction;

public class Main {

    /**
     * ISA
     * this is a load store architecture
     *
     * operator destination operand [operand]
     *
     * add rd rs rt         --addition of two registers
     * addi rd rs #n        --addition of a register and an immediate
     * cmp rd rs rt         --compare rs and rt, returning -1 less, 0 eq, 1 greater
     * ld rd n(rs)[rt]      --scaled addressing (? not totally sure how this works because how is d decided)
     *                      --since we have regs.rd <- mem[n + regs.rs + (regs.rt * d)]
     * ld rd n(rs)          --if we have a constant zero register (or load a constant like below)
     *                      --we can use this for direct and indirect addressing
     * ldc rd #n            --load a constant
     * b #n                 --branch; sets the pc to absolute address (immediate/const)
     * j #n                 --branch; jumps n instructions down (relative branch with immediate)
     * blez rs #n           --branch; if rs is less than or equal to zero move to absolute instruction # n
     * jlez rs #n           --branch; if rs is less than or equal to zero, relative branch by immediate operand
     */
    public static void main(String[] args){
        if(args.length >= 1){
            System.out.println(args[0]);
        }

        Assembler assembler = new Assembler("./assembly/text2.latte");
        boolean didRead = assembler.readFile();
        System.out.println("didRead is " + didRead);
        System.out.println("readFile read: " + Utils.writeList(assembler.getRawLines()));
    }

}
