import java.util.AbstractMap;
import java.util.Map;

public class Lookup {
    //for assembling
    public static Map<String, OpCode> op = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, OpCode>("add", OpCode.add),
            new AbstractMap.SimpleEntry<String, OpCode>("addi", OpCode.addi),
            new AbstractMap.SimpleEntry<String, OpCode>("mul", OpCode.mul),
            new AbstractMap.SimpleEntry<String, OpCode>("muli", OpCode.muli),
            new AbstractMap.SimpleEntry<String, OpCode>("cmp", OpCode.cmp),
            new AbstractMap.SimpleEntry<String, OpCode>("ld", OpCode.ld),
            new AbstractMap.SimpleEntry<String, OpCode>("ldc", OpCode.ldc),
            new AbstractMap.SimpleEntry<String, OpCode>("st", OpCode.st),
            new AbstractMap.SimpleEntry<String, OpCode>("brlz", OpCode.brlz),
            new AbstractMap.SimpleEntry<String, OpCode>("jplz", OpCode.jplz),
            new AbstractMap.SimpleEntry<String, OpCode>("br", OpCode.br),
            new AbstractMap.SimpleEntry<String, OpCode>("jp", OpCode.jp)
    );

    public static Map<String, RegisterName> reg = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, RegisterName>("zero", RegisterName.zero),
            new AbstractMap.SimpleEntry<String, RegisterName>("ra", RegisterName.ra),
            new AbstractMap.SimpleEntry<String, RegisterName>("sp", RegisterName.sp),
            new AbstractMap.SimpleEntry<String, RegisterName>("gp", RegisterName.gp),
            new AbstractMap.SimpleEntry<String, RegisterName>("tp", RegisterName.tp),
            new AbstractMap.SimpleEntry<String, RegisterName>("t0", RegisterName.t0),
            new AbstractMap.SimpleEntry<String, RegisterName>("t1", RegisterName.t1),
            new AbstractMap.SimpleEntry<String, RegisterName>("t2", RegisterName.t2),
            new AbstractMap.SimpleEntry<String, RegisterName>("t3", RegisterName.t3),
            new AbstractMap.SimpleEntry<String, RegisterName>("t4", RegisterName.t4),
            new AbstractMap.SimpleEntry<String, RegisterName>("t5", RegisterName.t5),
            new AbstractMap.SimpleEntry<String, RegisterName>("t6", RegisterName.t6),
            new AbstractMap.SimpleEntry<String, RegisterName>("s0", RegisterName.s0),
            new AbstractMap.SimpleEntry<String, RegisterName>("s1", RegisterName.s1),
            new AbstractMap.SimpleEntry<String, RegisterName>("s2", RegisterName.s2),
            new AbstractMap.SimpleEntry<String, RegisterName>("s3", RegisterName.s3),
            new AbstractMap.SimpleEntry<String, RegisterName>("s4", RegisterName.s4),
            new AbstractMap.SimpleEntry<String, RegisterName>("s5", RegisterName.s5),
            new AbstractMap.SimpleEntry<String, RegisterName>("s6", RegisterName.s6),
            new AbstractMap.SimpleEntry<String, RegisterName>("s7", RegisterName.s7),
            new AbstractMap.SimpleEntry<String, RegisterName>("s8", RegisterName.s8),
            new AbstractMap.SimpleEntry<String, RegisterName>("s9", RegisterName.s9),
            new AbstractMap.SimpleEntry<String, RegisterName>("s10", RegisterName.s10),
            new AbstractMap.SimpleEntry<String, RegisterName>("s11", RegisterName.s11),
            new AbstractMap.SimpleEntry<String, RegisterName>("a0", RegisterName.a0),
            new AbstractMap.SimpleEntry<String, RegisterName>("a1", RegisterName.a1),
            new AbstractMap.SimpleEntry<String, RegisterName>("a2", RegisterName.a2),
            new AbstractMap.SimpleEntry<String, RegisterName>("a3", RegisterName.a3),
            new AbstractMap.SimpleEntry<String, RegisterName>("a4", RegisterName.a4),
            new AbstractMap.SimpleEntry<String, RegisterName>("a5", RegisterName.a5),
            new AbstractMap.SimpleEntry<String, RegisterName>("a6", RegisterName.a6),
            new AbstractMap.SimpleEntry<String, RegisterName>("a7", RegisterName.a7)
    );
}
