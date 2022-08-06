/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package louvian;

import java.io.File;
import java.io.IOException;
import utils.DataStorage;
import utils.TypeAlgorithm;
import utils.TypeDestroyedNode;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.plugin.PartitionElementColorTransformer;
import org.gephi.appearance.plugin.palette.Palette;
import org.gephi.appearance.plugin.palette.PaletteManager;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.gephi.statistics.plugin.PageRank;
import org.graphstream.graph.implementations.MultiGraph;
import org.openide.util.Lookup;

/**
 *
 * @author mahsa
 */
public class LouvianClustering {

    private static int snapShotNum = 0;
    private DirectedGraph directedGraph;
    Column modColumn;
    private int partisionNumber;
    private double modularity;
    protected double[] degAvgCommuniti;
    protected double[] prAvgCommuniti;
    private MultiGraph multiGraph;
    private final TypeDestroyedNode destroyedNode;

    public LouvianClustering(TypeDestroyedNode destroyedNode) {
        this.destroyedNode = destroyedNode;
    }

    /**
     *
     * @param convertedGraph
     * @param baseGraph
     * @return
     */
    public MultiGraph partition(MultiGraph convertedGraph, MultiGraph baseGraph) {

        //Run modularity algorithm - community detection
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get a graph model - it exists because we have a workspace
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
        AppearanceController appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
        AppearanceModel appearanceModel = appearanceController.getModel();

        directedGraph = convertMultiGraphToDirectedGraph(convertedGraph, graphModel);
        DataStorage.allNodecount.add(directedGraph.getNodeCount());

        System.out.println("Louvian Nodes Count: " + directedGraph.getNodeCount());
        System.out.println("Louvain Edges Count: " + directedGraph.getEdgeCount());
        long time1 = System.currentTimeMillis(); // ابتدای بازه ی زمانی

        Modularity modularity = new Modularity();
        modularity.execute(graphModel);

        //Partition with 'modularity_class', just created by Modularity algorithm
        modColumn = graphModel.getNodeTable().getColumn(Modularity.MODULARITY_CLASS);
        Function func = appearanceModel.getNodeFunction(directedGraph, modColumn, PartitionElementColorTransformer.class);
        Partition partition = ((PartitionFunction) func).getPartition();
        DataStorage.time.add((System.currentTimeMillis() - time1));
        this.modularity = modularity.getModularity();
        partisionNumber = partition.size();
        DataStorage.modularity.add(this.modularity);
        DataStorage.partitionSize.add(partisionNumber);

        System.out.println("Time: " + DataStorage.time.get(DataStorage.time.size() - 1));
        System.out.println(partisionNumber + " partitions found");
        System.out.println("Modularity is : " + this.modularity);

        //Export
        export(partition, func, appearanceController);

        multiGraph = convertDirectGraphToMultiGraph(baseGraph);
        if (destroyedNode != null) {

            if (destroyedNode.equals(TypeDestroyedNode.PR)) {
                prAvgCommuniti = new double[partisionNumber];
                PageRank pageRank = new PageRank();
                pageRank.setUseEdgeWeight(true);
                pageRank.execute(graphModel);
                calculateAvragePRCommunity();
            } else {
                degAvgCommuniti = new double[partisionNumber];
                calculateAvrageDegCommunity();
            }
        }
        return multiGraph;
    }

    /**
     *
     */
    private void calculateAvrageDegCommunity() {

        int[] countNodeinCommunity = new int[degAvgCommuniti.length];
        int[] sumDeginCommunity = new int[degAvgCommuniti.length];

        int temp;
        for (Node n : directedGraph.getNodes()) {
            temp = (Integer) n.getAttribute(modColumn);
            countNodeinCommunity[temp]++;
            sumDeginCommunity[temp] += directedGraph.getDegree(n);
        }
        for (int i = 0; i < degAvgCommuniti.length; i++) {
            degAvgCommuniti[i] = (double) sumDeginCommunity[i] / countNodeinCommunity[i];
        }
    }

    /**
     *
     */
    private void calculateAvragePRCommunity() {

        int[] countNodeinCommunity = new int[prAvgCommuniti.length];
        double[] sumPRinCommunity = new double[prAvgCommuniti.length];

        int temp;
        for (Node n : directedGraph.getNodes()) {
            temp = (Integer) n.getAttribute(modColumn);
            countNodeinCommunity[temp]++;
            sumPRinCommunity[temp] += (Double) n.getAttribute("pageranks");
        }
        for (int i = 0; i < prAvgCommuniti.length; i++) {
            prAvgCommuniti[i] = (double) sumPRinCommunity[i] / countNodeinCommunity[i];
        }
    }

    /**
     *
     * @param graph
     * @param graphModel
     * @return
     */
    public DirectedGraph convertMultiGraphToDirectedGraph(MultiGraph graph,
            GraphModel graphModel) {
//        long l = System.currentTimeMillis();
        directedGraph = graphModel.getDirectedGraph();
        Node n0;
        for (org.graphstream.graph.Node n : graph.getNodeSet()) {
            n0 = graphModel.factory().newNode(n.getId());
            n0.setAttribute("label", n.getAttribute("label"));
            directedGraph.addNode(n0);
            if (((String) n.getAttribute("label")) != null
                    && ((String) n.getAttribute("label")).contains("#")) {
//                System.out.println(((String) n.getAttribute("label")).split("#").length);
            }
        }
        org.gephi.graph.api.Edge e1;
        for (org.graphstream.graph.Edge e : graph.getEdgeSet()) {
            e1 = graphModel.factory().newEdge(directedGraph.getNode(e.getNode0().getId()),
                    directedGraph.getNode(e.getNode1().getId()),
                    0,
                    (Double) e.getAttribute("weight"),
                    true);
            if (!directedGraph.contains(e1)) {
                directedGraph.addEdge(e1);
            }
        }
//        System.out.println("convertMultiGraphToDirectedGraph: " + (System.currentTimeMillis() - l));
        return directedGraph;
    }

    /**
     * @return the directedGraph
     */
    public DirectedGraph getDirectedGraph() {
        return directedGraph;
    }

    public Integer getCommunityNumber(String nodeID) {

        if (directedGraph.getNode(nodeID) == null) {
            return -1;
        }
        return (Integer) directedGraph.getNode(nodeID).getAttribute(modColumn);

    }

    /**
     * @return the partisionNumber
     */
    public int getPartisionNumber() {
        return partisionNumber;
    }

    /**
     * @return the modularity
     */
    public double getModularity() {
        return modularity;
    }

    /**
     *
     * @param baseGraph
     * @return
     */
    private MultiGraph convertDirectGraphToMultiGraph(MultiGraph baseGraph) {
//        long l = System.currentTimeMillis();

        // kntu نقش جدا کننده را برای ما ایفا میکند
        for (Node n : directedGraph.getNodes()) {
            if (n.getAttribute("label") != null
                    && ((String) n.getAttribute("label")).contains("kntu")) {
                for (String c : ((String) n.getAttribute("label")).split("kntu")) {
                    baseGraph.getNode(c).addAttribute("community",
                            (Integer) directedGraph.getNode(n.getId()).getAttribute(modColumn));
                }
            } else {
                baseGraph.getNode((String) n.getId()).addAttribute("community",
                        (Integer) directedGraph.getNode(n.getId()).getAttribute(modColumn));
            }
        }
        return baseGraph;
    }

    private void export(Partition partition,
            Function func,
            AppearanceController appearanceController) {
        Palette palette2 = PaletteManager.getInstance().randomPalette(partition.size());
        partition.setColors(palette2.getColors());
        appearanceController.transform(func);
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File("partition" + snapShotNum + ".gexf"));
            snapShotNum++;
        } catch (IOException ex) {
            System.out.println("error");
        }
    }
}
