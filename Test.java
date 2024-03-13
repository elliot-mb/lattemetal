import java.io.*;

public class Test {

    private static final Memory getExampleMemory() {
        return new Memory(
                0, //dont care
                new int[]{
                        40, 10, 0, 0, 0, 0, 0, 0, 0, 0,
                        78, 9, -15, 22, -4, 65, 11, 2, 10, 18,
                        58, 11, -11, 6, -4, 51, 51, 4, 51, 17,
                        48, 12, -8, 24, 20, 25, 31, 10, 19, 25,
                        148, 15, -81, 31, -4, 54, 14, 23, 41, 4,
                }
        );
    }

    private static Memory runKern(String filePath) throws FileNotFoundException {
        System.out.println(filePath);
        PrintStream silencer = new PrintStream("/dev/null");
        Assembler assembler = new Assembler(filePath);
        boolean didRead = assembler.readFile();
        if(!didRead){
            throw new RuntimeException("runKern: program assembler failed to read program '" + filePath + "'");
        }
        InstructionCache ic = new InstructionCache(assembler.assemble());
        Processor p = new Processor(ic, getExampleMemory()); //memory can be set if you like
        return p.run(silencer);
    }

    private static void bubbleSortMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{40, 10, 0, 0, 0, 0, 0, 0, 0, 0, -81, -15, -11, -8, -4, -4, -4, 2, 4, 4, 6, 9, 10, 10, 11, 11,
                        12, 14, 15, 17, 18, 19, 20, 22, 23, 24, 25, 25, 31, 31, 41, 48, 51, 51, 51, 54, 58, 65, 78, 148,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Utils.assertTrue(result.equals(runKern("./assembly/bubble_sort.latte")));
    }

    private static void mat2MulMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{2, 2, 2, 16, 32, 48, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, -1, 6, 6, 1, 58, 11, -11, 6, -4, 51,
                        51, 4, 51, 17, 48, 12, 2, 3, 4, 5, 31, 10, 19, 25, 148, 15, -81, 31, -4, 54, 14, 23, 22, 27, 16,
                        23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Utils.assertTrue(result.equals(runKern("./assembly/mat2_mul.latte")));
    }

    private static void mat3MulMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{3, 3, 3, 18, 36, 54, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, 11, 2, 1, 0, 0, 0, 1, 0, 0, 0, 1, 4,
                        51, 17, 48, 12, -8, 24, 20, 25, 2, 3, 1, 2, 8, 9, 1, 8, 9, 54, 14, 23, 41, 4, 0, 0, 0, 0, 2, 3,
                        1, 2, 8, 9, 1, 8, 9, 0});
        Utils.assertTrue(result.equals(runKern("./assembly/mat3_mul.latte")));
    }

    private static void minMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{3, 10, -5, -1, -5, 2, 4, -5, -2, -5, -1, 2, 1000, 22, -4, 65, 11, 2, 10, 18, 58, 11, -11, 6,
                        -4, 51, 51, 4, 51, 17, 48, 12, -8, 24, 20, 25, 31, 10, 19, 25, 148, 15, -81, 31, -4, 54, 14, 23,
                        41, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Utils.assertTrue(result.equals(runKern("./assembly/min.latte")));
    }

    private static void quotMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{134, 24, 5, 14, 0, 0, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, 11, 2, 10, 18, 58, 11, -11, 6, -4,
                        51, 51, 4, 51, 17, 48, 12, -8, 24, 20, 25, 31, 10, 19, 25, 148, 15, -81, 31, -4, 54, 14, 23, 41,
                        4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                });
        Utils.assertTrue(result.equals(runKern("./assembly/quot.latte")));
    }

    private static void vecAddMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{10, 20, 30, 40, 0, 0, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, 11, 2, 10, 18, 58, 11, -11, 6, -4,
                        51, 51, 4, 51, 17, 48, 12, -8, 24, 20, 25, 31, 10, 19, 25, 106, 23, -19, 30, 16, 76, 82, 14, 70,
                        42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                });
        Utils.assertTrue(result.equals(runKern("./assembly/vec_add.latte")));
    }

    private static void vecDotMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{10, 20, 30, 40, 0, 0, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, 11, 2, 10, 18, 58, 11, -11, 6, -4,
                        51, 51, 4, 51, 17, 48, 12, -8, 24, 20, 25, 31, 10, 19, 25, 7358, 15, -81, 31, -4, 54, 14, 23,
                        41, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                });
        Utils.assertTrue(result.equals(runKern("./assembly/vec_dot.latte")));
    }

    static void test() throws FileNotFoundException {
        bubbleSortMemTest();
        mat2MulMemTest();
        mat3MulMemTest();
        minMemTest();
        quotMemTest();
        vecAddMemTest();
        vecDotMemTest();
    }
}
