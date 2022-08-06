/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.graphstream.graph.implementations.MultiGraph;

/**
 *
 * @author mahsa
 */
public class Utils {

    private final List<MultiGraph> graphs;
    private int evolvingSpeed;
    private String evolvingUnit;
    private int snapshotNumber;

    public Utils() {
        graphs = new ArrayList<>();
    }

    public Utils(int evolvingSpeed, String evolvingUnit) {
        this.evolvingSpeed = evolvingSpeed;
        this.evolvingUnit = evolvingUnit;
        graphs = new ArrayList<>();
    }

    public void buildSnapshotGraphs() throws IOException {
        //For build Graph
//        EnronDataSet dataSet = new EnronDataSet();
//        try {
//            snapshotNumber = dataSet.builsSnapshotLists("year", 10);
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//            return;
//        }

        /* read from file */
        setSnapshotNumber(new File("SnapShot_25day").listFiles().length);
        for (int i = 1; i <= getSnapshotNumber(); i++) {
//            graphs.add(convertSnapshotFileToDirectedGraph("SnapShot_25day\\Snapshot" + i + ".csv"));
            graphs.add(convertSnapshotFileToDirectedGraph(i));
        }
        System.out.println("build SnapshotGraph");
    }

    /**
     *
     * @param csvFileNum
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public MultiGraph convertSnapshotFileToDirectedGraph(int csvFileNum)
            throws FileNotFoundException, IOException {

        String csvFile = "SnapShot_25day\\Snapshot";
        String source, target;
        MultiGraph graph = new MultiGraph("Tutorial 1");
        double sumWeight = 0;

        for (int i = 1; i <= csvFileNum; i++) {
            CsvReader reader = new CsvReader(new FileReader(csvFile + i + ".csv"), ',');
            reader.setSafetySwitch(false);

            reader.readHeaders();

            while (reader.readRecord()) {

                source = reader.get(0);
                target = reader.get(1);
                if (graph.getNode(source) == null) {
                    graph.addNode(source);
                    graph.getNode(source).addAttribute("snapshot", i);
                }
                if (graph.getNode(target) == null) {
                    graph.addNode(target);
                    graph.getNode(target).addAttribute("snapshot", i);
                }
                if (graph.getEdge(source + target) == null) {
                    graph.addEdge(source + target, source, target);
                    graph.getEdge(source + target).addAttribute("weight", 1.0);
                    graph.getEdge(source + target).addAttribute("snapshot", i);
                } else {
                    graph.getEdge(source + target).addAttribute("weight",
                            ((Double) graph.getEdge(source + target).getAttribute("weight"))
                            + 1.0);
                }
                sumWeight++;
            }
            reader.close();
        }
        graph.setAttribute("GraphWeight", sumWeight);
        return graph;
    }

    /**
     * @return the graphs
     */
    public List<MultiGraph> getGraphs() {
        return graphs;
    }

    /**
     * @return the snapshotNumber
     */
    public int getSnapshotNumber() {
        return snapshotNumber;
    }

    /**
     * @param snapshotNumber the snapshotNumber to set
     */
    public void setSnapshotNumber(int snapshotNumber) {
        this.snapshotNumber = snapshotNumber;
    }
}
