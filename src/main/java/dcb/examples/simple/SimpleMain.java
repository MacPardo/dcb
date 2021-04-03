package dcb.examples.simple;

import dcb.components.ComponentType;
import dcb.core.models.ComponentInfo;
import dcb.core.models.ComponentPort;
import dcb.core.models.NetworkAddress;
import dcb.core.runner.Runner;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleMain {
    private static final int A = 1;
    private static final int B = 2;
    private static NetworkAddress address = new NetworkAddress("localhost", 8080);
    private static NetworkAddress address2 = new NetworkAddress("localhost", 8081);

    public static void main(String[] args) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String outputDirName = "outputs" + File.separator + dateTimeFormatter.format(now);
        File outputDir = new File(outputDirName);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        String filenameA = outputDirName + File.separator + "A.csv";
        String filenameB = outputDirName + File.separator + "B.csv";
        File fileA = new File(filenameA);
        File fileB = new File(filenameB);
        fileA.createNewFile();
        fileB.createNewFile();

        List<ComponentInfo> componentInfoList = new ArrayList<>();
        componentInfoList.add(new ComponentInfo(A, address, new SimpleBehavior(), ComponentType.RDT_LGC, filenameA));
        componentInfoList.add(new ComponentInfo(B, address, new SimpleBehavior(), ComponentType.RDT_LGC, filenameB));

        Map<ComponentPort, ComponentPort> connections = new HashMap<>();
        connections.put(
                new ComponentPort(A, SimpleBehavior.OUTPUT_PORT),
                new ComponentPort(B, SimpleBehavior.INPUT_PORT)
        );
        connections.put(
                new ComponentPort(B, SimpleBehavior.OUTPUT_PORT),
                new ComponentPort(A, SimpleBehavior.INPUT_PORT)
        );
        connections.put(
                new ComponentPort(A, SimpleBehavior.MYSELF_PORT),
                new ComponentPort(A, SimpleBehavior.MYSELF_PORT)
        );
        connections.put(
                new ComponentPort(B, SimpleBehavior.MYSELF_PORT),
                new ComponentPort(B, SimpleBehavior.MYSELF_PORT)
        );

        Runner runner = new Runner(componentInfoList, connections);
        Thread thread = new Thread(runner);
        thread.start();


        long time = 4_000L;
        try {
            Thread.sleep(time);
            System.out.println("Forcing execution to stop after " + time + " milliseconds");
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now));
//
//        filePrefix = "outputs" + File.separator + dtf.format(now) + File.separator;

//        String filename = "outputs" + File.separator + "dcb_test_file.txt";
//        File file = new File(filename);
//        file.createNewFile();
//        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8));
//        writer.write("hello this is a test");
//        writer.close();
//
//        System.out.println(System.getProperty("user.dir"));
//        System.out.println(File.separator);
    }
}
