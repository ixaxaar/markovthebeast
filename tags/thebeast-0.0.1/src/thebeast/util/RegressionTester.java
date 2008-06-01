package thebeast.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Sebastian Riedel
 */
public class RegressionTester {

    private String regressionDir;


    public RegressionTester(String regressionDir) {
        this.regressionDir = regressionDir;
    }

    public void test(OutputStream os) throws IOException {
        PrintWriter out = new PrintWriter(os);
        File dir = new File(regressionDir);
        if (!dir.isDirectory()) throw new IOException(dir + " is not a directory");
        File sub = new File(regressionDir);
        int totalCount = 0;
        int correctCount = 0;
        int testProblemCount = 0;
        if (sub.isDirectory()) {
            out.println("Testing Regression Tests in " + regressionDir);
            out.printf("%-20s%-10s%-10s\n", "Test", "Status", "time(in s)");
            for (int i = 0; i < 40; ++i) out.print("-");
            out.println();
            HashMap<String, String> testFiles = new HashMap<String, String>();
            HashMap<String, String> expectedFiles = new HashMap<String, String>();
            HashMap<String, String> actualFiles = new HashMap<String, String>();
            for (String filename : sub.list()) {
                if (filename.endsWith(".test")) {
                    testFiles.put(filename.substring(0, filename.indexOf('.')), filename);
                } else if (filename.endsWith(".expected")) {
                    expectedFiles.put(filename.substring(0, filename.indexOf('.')), filename);
                } else if (filename.endsWith(".actual")) {
                    actualFiles.put(filename.substring(0, filename.indexOf('.')), filename);
                }
            }
            ArrayList<String> tests = new ArrayList<String>(testFiles.size());
            tests.addAll(testFiles.keySet());
            Collections.sort(tests);
            for (String test : tests) {
                long time = System.currentTimeMillis();
                //String[] env = new String[]{"PATH=.:" + sub.getAbsolutePath()};
                File file = new File(regressionDir + "/" + testFiles.get(test));

                //
                // Process process =
                Runtime.getRuntime().exec(file.getAbsolutePath());
//                BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//                String line;
//                while ((line = error.readLine())!=null)
//                    System.out.println(line);

                long delta = System.currentTimeMillis() - time;
                double inSeconds = (double) delta / 1000.0;
                String expectedRelative = expectedFiles.get(test);
                String expected = regressionDir + "/" + expectedRelative;
                String actualRelative = actualFiles.get(test);
                String actual = regressionDir + "/" + actualRelative;
                if (expectedRelative != null && actualRelative != null) {
                    boolean match = compare(expected, actual);
                    out.printf("%-20s%-10s%-10.2f\n", test, match ? "OK" : "FAILED", inSeconds);
                    ++totalCount;
                    if (match) ++correctCount;
                } else if (expectedRelative == null) {
                    out.println(test + " needs a .expected file");
                    ++testProblemCount;
                } else {
                    out.println(test + " needs a .actual file");
                    ++testProblemCount;
                }
            }
            out.println("Tests in " + sub.getAbsolutePath() + " completed.");
            out.printf("%-17s%-4d\n", "Total Count:", totalCount);
            out.printf("%-17s%-4d\n", "Correct:", correctCount);
            out.printf("%-17s%-4d\n", "Missing files:", testProblemCount);
            out.close();
        }

    }

    private boolean compare(String expected, String actual) throws IOException {
        StringBuilder builderExpected = new StringBuilder();
        BufferedReader readerExpected = new BufferedReader(new FileReader(expected));
        String lineExpected;
        while ((lineExpected = readerExpected.readLine()) != null) {
            if (!lineExpected.trim().equals("")) {
                builderExpected.append(lineExpected);
            }
        }
        StringBuilder builderActual = new StringBuilder();
        BufferedReader readerActual = new BufferedReader(new FileReader(actual));
        String lineActual;
        while ((lineActual = readerActual.readLine()) != null) {
            if (!lineActual.trim().equals("")) {
                builderActual.append(lineActual);
            }
        }
        return builderExpected.toString().equals(builderActual.toString());
    }

    public static void main(String[] args) throws IOException {
        RegressionTester tester = new RegressionTester(args[0]);
        tester.test(System.out);
    }

}
