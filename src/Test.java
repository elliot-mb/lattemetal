import java.io.*;

public class Test {

    static final int ONE_ABOVE = 2;

    static final int GLOBAL_DIVERGE_LIM = (int) Math.pow(10, 5);

    static final int btbSize = 32;
    static final int superscalarWidth = 8;
    static final int aluCount = 4;
    static final int lsuCount = 4;
    static final int bruCount = 2;
    static final int aluRsCount = 4;
    static final int lsuRsCount = 4;
    static final int bruRsCount = 2;
    static final int dpAcc = 4;
    static final int robEntries = 64;
    static final boolean alignedFetch = false;
    static final boolean showCommit = false;
    static final int physicalRegisters = 128;

    private static Memory getExampleMemory() {
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

    private static Memory getExampleMemory2() {
        return new Memory(
                0,
                new int[]{
                        40, 10, 0, 0, 0, 0, 0, 0, 0, 0,
                        92, 84, 76, 68, 50, 42, 34, 26, 18, 0,
                        -2, -4, -6, -8, -10, -12, -14, -16, -18, -20,
                        12, 24, 36, 48, 51, 62, 74, 86, 98, 120,
                        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                }
        );
    }

    private static void showAmInside(){
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        System.out.println(ste[ONE_ABOVE].getClassName()+"#"+ste[ONE_ABOVE].getMethodName());
    }

    private static void bubbleSortMemTest() throws FileNotFoundException {
        //showAmInside();
        final Memory result = new Memory(0,
                new int[]{40, 10, 0, 0, 0, 0, 0, 0, 0, 0, -81, -15, -11, -8, -4, -4, -4, 2, 4, 4, 6, 9, 10, 10, 11, 11,
                        12, 14, 15, 17, 18, 19, 20, 22, 23, 24, 25, 25, 31, 31, 41, 48, 51, 51, 51, 54, 58, 65, 78, 148,
                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/bubble_sort.latte", getExampleMemory(),true, GLOBAL_DIVERGE_LIM, true,
            Processor.predictor.twoBit,
            btbSize,
            superscalarWidth,
            aluCount,
            lsuCount,
            bruCount,
            aluRsCount,
            lsuRsCount,
            bruRsCount,
            dpAcc,
            robEntries,
            alignedFetch,
            showCommit,
            physicalRegisters
        )));
    }

    private static void mat2MulMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{2, 2, 2, 16, 32, 48, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, -1, 6, 6, 1, 58, 11, -11, 6, -4, 51,
                        51, 4, 51, 17, 48, 12, 2, 3, 4, 5, 31, 10, 19, 25, 148, 15, -81, 31, -4, 54, 14, 23, 22, 27, 16,
                        23, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/mat2_mul.latte", getExampleMemory(), true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }

    private static void mat3MulMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{3, 3, 3, 18, 36, 54, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, 11, 2, 1, 0, 0, 0, 1, 0, 0, 0, 1, 4,
                        51, 17, 48, 12, -8, 24, 20, 25, 2, 3, 1, 2, 8, 9, 1, 8, 9, 54, 14, 23, 41, 4, 0, 0, 0, 0, 2, 3,
                        1, 2, 8, 9, 1, 8, 9, 0});
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/mat3_mul.latte", getExampleMemory(),true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }

    private static void minMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{3, 10, -5, -1, -5, 2, 4, -5, -2, -5, -1, 2, 1000, 22, -4, 65, 11, 2, 10, 18, 58, 11, -11, 6,
                        -4, 51, 51, 4, 51, 17, 48, 12, -8, 24, 20, 25, 31, 10, 19, 25, 148, 15, -81, 31, -4, 54, 14, 23,
                        41, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/min.latte", getExampleMemory(),true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }

    private static void quotMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{134, 24, 5, 14, 0, 0, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, 11, 2, 10, 18, 58, 11, -11, 6, -4,
                        51, 51, 4, 51, 17, 48, 12, -8, 24, 20, 25, 31, 10, 19, 25, 148, 15, -81, 31, -4, 54, 14, 23, 41,
                        4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                });
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/quot.latte", getExampleMemory(),true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }

    private static void vecAddMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{10, 20, 30, 40, 0, 0, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, 11, 2, 10, 18, 58, 11, -11, 6, -4,
                        51, 51, 4, 51, 17, 48, 12, -8, 24, 20, 25, 31, 10, 19, 25, 106, 23, -19, 30, 16, 76, 82, 14, 70,
                        42, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                });
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/vec_add.latte", getExampleMemory(),true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }

    private static void vecDotMemTest() throws FileNotFoundException {
        final Memory result = new Memory(0,
                new int[]{10, 20, 30, 40, 0, 0, 0, 0, 0, 0, 78, 9, -15, 22, -4, 65, 11, 2, 10, 18, 58, 11, -11, 6, -4,
                        51, 51, 4, 51, 17, 48, 12, -8, 24, 20, 25, 31, 10, 19, 25, 7358, 15, -81, 31, -4, 54, 14, 23,
                        41, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
                });
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/vec_dot.latte", getExampleMemory(),true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }
    
    private static void blockTest() throws FileNotFoundException {
        final Memory result = new Memory(0, new int[]{40,10,0,120,371,371,0,0,0,0,78,9,40,10,-4,65,11,2,10,18,58,11,-11,6,-4,51,51,4,51,17,48,12,-8,24,20,25,31,10,19,25,148,15,-81,31,-4,54,14,23,41,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/block_testing.latte", getExampleMemory(), true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }

    private static void branchTakenTest() throws FileNotFoundException{
        final Memory result = new Memory(0, new int[]{1, 11, 0, 0, 0, 0, 0, 0, 0, 0, 92, 84, 76, 68, 50, 42, 34, 26, 18, 0, -2, -4, -6, -8, -10, -12, -14, -16, -18, -20, 12, 24, 36, 48, 51, 62, 74, 86, 98, 120, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/simple_branch_taken.latte", getExampleMemory2(), true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }

    private static void movTest() throws FileNotFoundException{
        final Memory result = new Memory(0, new int[]{40, 10, 0, 0, 0, 0, 0, 0, 0, 0, 92, 84, 76, 68, 50, 42, 34, 26, 18, 0, 92, 84, 76, 68, 50, 42, 34, 26, 18, 0, 12, 24, 36, 48, 51, 62, 74, 86, 98, 120, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Utils.assertTrue(result.equals(Utils.runKern("./kernels_test/move_ten_elements.latte", getExampleMemory2(), true, GLOBAL_DIVERGE_LIM, true,
                Processor.predictor.twoBit,
                btbSize,
                superscalarWidth,
                aluCount,
                lsuCount,
                bruCount,
                aluRsCount,
                lsuRsCount,
                bruRsCount,
                dpAcc,
                robEntries,
                alignedFetch,
                showCommit,
                physicalRegisters
        )));
    }

    static void test() throws FileNotFoundException {
        try{branchTakenTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{blockTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{mat2MulMemTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{mat3MulMemTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{minMemTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{quotMemTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{vecAddMemTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{vecDotMemTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{bubbleSortMemTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
        try{movTest();}catch(RuntimeException err){System.out.println(err.getMessage());}
    }
}
