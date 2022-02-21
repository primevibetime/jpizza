package lemon.jpizza;

import java.io.IOException;

public class RunTest {
    public static void main(String[] args) throws IOException {
        double start = System.currentTimeMillis();
        Shell.main(new String[]{"UnitTest.devp", "-rf", "-r"});
        double end = System.currentTimeMillis();
        Shell.logger.outln("Time: " + (end - start) + "ms");
    }
}
