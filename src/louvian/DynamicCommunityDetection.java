/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package louvian;

import utils.Utils;
import java.io.IOException;
import utils.DataStorage;
import utils.TypeAlgorithm;
import utils.TypeDestroyedNode;
import org.graphstream.algorithm.PageRank;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

/**
 *
 * @author Mahsa Seifikar
 */
public class DynamicCommunityDetection {

    private final LouvianClustering louvian;
    private final Utils utils;
    private final double coefficientWeight = 3.0;
    private final TypeDestroyedNode typeNode;
    private final TypeAlgorithm typeAlgorithm;

    public DynamicCommunityDetection(TypeDestroyedNode typeNode,
            TypeAlgorithm typeAlgorithm) {
        this.typeNode = typeNode;
        this.typeAlgorithm = typeAlgorithm;
        utils = new Utils();
        louvian = new LouvianClustering(typeNode);

    }

    public void run() throws IOException {
        utils.buildSnapshotGraphs();
        MultiGraph pervousDirectGraph = null;

        System.out.println("---------------------------------------------------------");
        System.out.println("itration: " + 0);

//        System.out.println("new Node Count: " + utils.getGraphs().get(0).getNodeCount());
//        System.out.println("new Edge Count: " + utils.getGraphs().get(0).getEdgeCount());
        // در گام زمانی اول روی گراف الگوریتم لووین را بیاده سازی میکند
        pervousDirectGraph = louvian.partition(utils.getGraphs().get(0), utils.getGraphs().get(0));

        for (int i = 1; i < utils.getSnapshotNumber(); i++) {

            System.out.println("---------------------------------------------------------");
            System.out.println("itration: " + i);

            if (typeAlgorithm.equals(TypeAlgorithm.Louvian)) {
                pervousDirectGraph = louvian.partition(utils.getGraphs().get(i),
                        utils.getGraphs().get(i));
            } else {
                if (typeNode.equals(TypeDestroyedNode.Degree)) {
                    pervousDirectGraph = louvian.partition(
                            createNewGraphWithDegree(utils.getGraphs().get(i), pervousDirectGraph),
                            utils.getGraphs().get(i));
                } else if (typeNode.equals(TypeDestroyedNode.PR)) {
                    pervousDirectGraph = louvian.partition(
                            createNewGraphWithPR(utils.getGraphs().get(i), pervousDirectGraph),
                            utils.getGraphs().get(i));
                }
            }
        }
    }

    /**
     *
     * @param curGraph
     * @param preGraph
     * @return new graph
     */
    private MultiGraph createNewGraphWithPR(MultiGraph curGraph, MultiGraph preGraph) {

        MultiGraph newGraph = new MultiGraph(" ");

        // ابتدا میانگین بیج رنک را در گراف حاضر میابیم سبس بیج رنک هر گره در گراف قبلی را با این میانگین میسنجیم
        PageRank PRcurGraph = new PageRank();
        PRcurGraph.setVerbose(true);
        PRcurGraph.init(curGraph);
//        double avgPR = calculateAvrgPR(PRcurGraph, curGraph);

        String brokenCommunity = "";
        int newNodesCount, newEdgesCount, nodeInCommunityCount,
                edgeInCommunityCount, communityUsedCount,
                destroyedNode, outNode;
        newEdgesCount = newNodesCount = nodeInCommunityCount
                = edgeInCommunityCount = communityUsedCount
                = destroyedNode = outNode = 0;
        int c1, c2;

        // in this for all new nodes and their neighboure in t+1 add to newGraph
        //  تمام گره های جدید و همسایه های آم ها را اضافه میکنیم.
        for (Edge edge : curGraph.getEachEdge()) {
            Node n0 = edge.getNode0();
            Node n1 = edge.getNode1();
            // اضافه شدن گره جدید
            if (preGraph.getNode(n0.getId()) == null) {
                if (newGraph.getNode(n0.getId()) == null) {
                    newGraph.addNode(n0.getId());
                    newNodesCount++;
                }

                if (newGraph.getNode(n1.getId()) == null) {
                    if (preGraph.getNode(n1.getId()) == null) {
                        newNodesCount++;
                    } else {
                        if (PRcurGraph.getRank(n1)
                                > louvian.prAvgCommuniti[preGraph.getNode(n1.getId()).getAttribute("community")]) {
                            brokenCommunity += "-" + preGraph.getNode(n1.getId()).getAttribute("community") + "-";
                            destroyedNode++;
                            n1.addAttribute("label", "Distroyed");
                        }
                    }
                    newGraph.addNode(n1.getId());
                }
                newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                newGraph.getEdge(edge.getId()).addAttribute("weight",
                        coefficientWeight * (Double) edge.getAttribute("weight"));
                newEdgesCount++;

            } else { // if n0 was in preGraph
                //اضافه شدن گره جدید
                if (preGraph.getNode(n1.getId()) == null) {
                    if (newGraph.getNode(n1.getId()) == null) {
                        newGraph.addNode(n1.getId());
                        newNodesCount++;
                    }
                    if (newGraph.getNode(n0.getId()) == null) {
                        newGraph.addNode(n0.getId());
                    }
                    if (PRcurGraph.getRank(n0)
                            > louvian.prAvgCommuniti[preGraph.getNode(n0.getId()).getAttribute("community")]) {
                        brokenCommunity += "-" + preGraph.getNode(n0.getId()).getAttribute("community") + "-";
                        n0.addAttribute("label", "Distroyed");
                    }

                    newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                    newGraph.getEdge(edge.getId()).addAttribute("weight",
                            coefficientWeight * (Double) edge.getAttribute("weight"));
                    newEdgesCount++;
                }
            }
        }

//        System.out.println("createNewGraphWithDegree11: " + (System.currentTimeMillis() - l));
//        l = System.currentTimeMillis();
        //======================================================================
        // 
        //======================================================================
        String[] communitiesID = new String[louvian.getPartisionNumber()];
        for (int i = 0; i < communitiesID.length; i++) {
            communitiesID[i] = new String();
        }
        // add preGraph informations to new graph 
        for (Edge edge : curGraph.getEachEdge()) {
            Node n0 = edge.getNode0();
            Node n1 = edge.getNode1();
            if (newGraph.getNode(n0.getId()) != null
                    && newGraph.getNode(n1.getId()) != null) {
                // hameye etelaat ghablan add shode
                continue;
            } else if (newGraph.getNode(n0.getId()) == null
                    && newGraph.getNode(n1.getId()) != null) {

                // چک میشود آیا نود در اجتماع یک نود مخرب بوده یا نه
                if (brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n0.getId()).getAttribute("community")
                        + "-")) {
                    outNode++;
                    newGraph.addNode(n0.getId());
                    newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                    newGraph.getEdge(edge.getId()).addAttribute("weight",
                            (Double) edge.getAttribute("weight"));
                } else {
                    c1 = preGraph.getNode(n0.getId()).getAttribute("community");
                    if (newGraph.getNode(c1 + "") == null) {
                        newGraph.addNode(c1 + "");
                    }
                    communitiesID[c1] += n0.getId() + "kntu";
                    nodeInCommunityCount++;

                    if (newGraph.getEdge(c1 + "" + n1.getId()) == null) {
                        newGraph.addEdge(c1 + "" + n1.getId(), c1 + "", n1.getId() + "");
                        newGraph.getEdge(c1 + "" + n1.getId()).setAttribute("weight",
                                edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(c1 + "" + n1.getId()).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(c1 + "" + n1.getId())
                                        .getAttribute("weight"));
                    }
                }
            } else if (newGraph.getNode(n0.getId()) != null
                    && newGraph.getNode(n1.getId()) == null) {

                // چک میشود آیا نودی که در ابر گره وجود دارد مخرب است یا نه
                if (brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n1.getId()).getAttribute("community")
                        + "-")) {
                    outNode++;
                    newGraph.addNode(n1.getId());
                    newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                    newGraph.getEdge(edge.getId()).addAttribute("weight",
                            (Double) edge.getAttribute("weight"));
                } else {

                    c2 = preGraph.getNode(n1.getId()).getAttribute("community");
                    if (newGraph.getNode(c2 + "") == null) {
                        newGraph.addNode(c2 + "");
                    }
                    communitiesID[c2] += n1.getId() + "kntu";
                    nodeInCommunityCount++;
                    if (newGraph.getEdge(n0.getId() + "" + c2) == null) {
                        newGraph.addEdge(n0.getId() + "" + c2, n0.getId() + "", c2 + "");
                        newGraph.getEdge(n0.getId() + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(n0.getId() + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(n0.getId() + "" + c2).getAttribute("weight"));
                    }
                }
            } else { // هر دوتا گره تو گراف جدید نیستن

                if (!brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n1.getId()).getAttribute("community")
                        + "-")
                        && !brokenCommunity.contains(
                                "-"
                                + preGraph.getNode(n0.getId()).getAttribute("community")
                                + "-")) {

                    // it means that n0 and n1 didn't exsist in newGraph
                    c1 = preGraph.getNode(n0.getId()).getAttribute("community");
                    if (newGraph.getNode(c1 + "") == null) {
                        newGraph.addNode(c1 + "");
                    }
                    communitiesID[c1] += n0.getId() + "kntu";
                    c2 = preGraph.getNode(n1.getId()).getAttribute("community");
                    if (newGraph.getNode(c2 + "") == null) {
                        newGraph.addNode(c2 + "");
                    }
                    communitiesID[c2] += n1.getId() + "kntu";
                    nodeInCommunityCount += 2;

                    if (newGraph.getEdge(c1 + "" + c2) == null) {
                        newGraph.addEdge(c1 + "" + c2, c1 + "", c2 + "");
                        newGraph.getEdge(c1 + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(c1 + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(c1 + "" + c2).getAttribute("weight"));
                    }
                    edgeInCommunityCount++;

                } else if (!brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n1.getId()).getAttribute("community")
                        + "-")
                        && brokenCommunity.contains(
                                "-"
                                + preGraph.getNode(n0.getId()).getAttribute("community")
                                + "-")) {

                    newGraph.addNode(n0.getId());
                    outNode++;

                    c2 = preGraph.getNode(n1.getId()).getAttribute("community");
                    if (newGraph.getNode(c2 + "") == null) {
                        newGraph.addNode(c2 + "");
                    }
                    communitiesID[c2] += n1.getId() + "kntu";
                    if (newGraph.getEdge(n0.getId() + "" + c2) == null) {
                        newGraph.addEdge(n0.getId() + "" + c2, n0.getId() + "", c2 + "");
                        newGraph.getEdge(n0.getId() + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(n0.getId() + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(n0.getId() + "" + c2).getAttribute("weight"));
                    }
                } else if (brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n1.getId()).getAttribute("community")
                        + "-")
                        && !brokenCommunity.contains(
                                "-"
                                + preGraph.getNode(n0.getId()).getAttribute("community")
                                + "-")) {

                    newGraph.addNode(n1.getId());

                    c1 = preGraph.getNode(n0.getId()).getAttribute("community");
                    if (newGraph.getNode(c1 + "") == null) {
                        newGraph.addNode(c1 + "");
                    }
                    communitiesID[c1] += n0.getId() + "kntu";
                    nodeInCommunityCount++;

                    if (newGraph.getEdge(c1 + "" + n1.getId()) == null) {
                        newGraph.addEdge(c1 + "" + n1.getId(), c1 + "", n1.getId() + "");
                        newGraph.getEdge(c1 + "" + n1.getId()).setAttribute("weight",
                                edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(c1 + "" + n1.getId()).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(c1 + "" + n1.getId())
                                        .getAttribute("weight"));
                    }
                } else {

                    outNode += 2;

                    newGraph.addNode(n0.getId());
                    newGraph.addNode(n1.getId());
                    newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                    newGraph.getEdge(edge.getId()).addAttribute("weight",
                            (Double) edge.getAttribute("weight"));
                }

            }
        }
//        System.out.println("createNewGraphWithDegree22: " + (System.currentTimeMillis() - l));
        for (int i = 0;
                i < louvian.getPartisionNumber();
                i++) {
            if (newGraph.getNode(i + "") != null) {
                newGraph.getNode(i + "").addAttribute("label", communitiesID[i]);
                communityUsedCount++;
            }
        }

        System.out.println("Community used Count: " + communityUsedCount);
        System.out.println("new Node Count: " + newNodesCount);
        System.out.println("new Edge Count: " + newEdgesCount);
        System.out.println("Node in Community Count: " + nodeInCommunityCount);
        System.out.println("Edge in Community Count: " + edgeInCommunityCount);
        System.out.println("Node out Community Count: " + outNode);

        System.out.println("destyousNode: " + destroyedNode);

        DataStorage.communityUsedCount.add(communityUsedCount);
        DataStorage.newNodesCount.add(newNodesCount);
        DataStorage.newEdgesCount.add(newEdgesCount);
        DataStorage.nodeInCommunityCount.add(nodeInCommunityCount);
        DataStorage.edgeInCommunityCount.add(edgeInCommunityCount);
        DataStorage.destroyedNode.add(destroyedNode);
        DataStorage.outNodesCount.add(outNode);

        return newGraph;
    }

    /**
     *
     * @param curGraph
     * @param preGraph
     * @return new graph
     */
    private MultiGraph createNewGraphWithDegree(MultiGraph curGraph, MultiGraph preGraph) {

        long l = System.currentTimeMillis();
        MultiGraph newGraph = new MultiGraph("g");

        String brokenCommunity = ""; // community haye shamel node mohareb ro mizarim in to
        int newNodesCount, newEdgesCount, nodeInCommunityCount,
                edgeInCommunityCount, communityUsedCount,
                destroyedNode, outNode;
        newEdgesCount = newNodesCount = nodeInCommunityCount
                = edgeInCommunityCount = communityUsedCount
                = destroyedNode = outNode = 0;
        int c1, c2;

        // in this for all new nodes and their neighboure in t+1 add to newGraph
        //  تمام گره های جدید و همسایه های آم ها را اضافه میکنیم.
        for (Edge edge : curGraph.getEachEdge()) {
            Node n0 = edge.getNode0();
            Node n1 = edge.getNode1();
            // اضافه شدن گره جدید
            if (preGraph.getNode(n0.getId()) == null) {
                if (newGraph.getNode(n0.getId()) == null) {
                    newGraph.addNode(n0.getId());
                    newNodesCount++;
                }

                if (newGraph.getNode(n1.getId()) == null) {
                    if (preGraph.getNode(n1.getId()) == null) {
                        newNodesCount++;
                    } else {

                        if (n1.getDegree()
                                > louvian.degAvgCommuniti[preGraph.getNode(n1.getId()).getAttribute("community")]) {
                            brokenCommunity += "-" + preGraph.getNode(n1.getId()).getAttribute("community") + "-";
                            destroyedNode++;
                            newGraph.getNode(n1.getId()).addAttribute("label", "Distroyed");
                        } else {
                            outNode++;
                        }
                    }
                    newGraph.addNode(n1.getId());
                }
                newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                newGraph.getEdge(edge.getId()).addAttribute("weight",
                        coefficientWeight * (Double) edge.getAttribute("weight"));
                newEdgesCount++;

            } else { // if n0 was in preGraph
                //اضافه شدن گره جدید
                if (preGraph.getNode(n1.getId()) == null) {
                    if (newGraph.getNode(n1.getId()) == null) {
                        newGraph.addNode(n1.getId());
                        newNodesCount++;
                    }
                    if (newGraph.getNode(n0.getId()) == null) {
                        newGraph.addNode(n0.getId());
                    }
                    if (n0.getDegree()
                            > louvian.degAvgCommuniti[preGraph.getNode(n0.getId()).getAttribute("community")]) {
                        brokenCommunity += "-" + preGraph.getNode(n0.getId()).getAttribute("community") + "-";
                        newGraph.getNode(n0.getId()).addAttribute("label", "Distroyed");
                        destroyedNode++;
                    } else {
                        outNode++;
                    }

                    newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                    newGraph.getEdge(edge.getId()).addAttribute("weight",
                            coefficientWeight * (Double) edge.getAttribute("weight"));
                    newEdgesCount++;
                }
            }
        }

        System.out.println("createNewGraphWithDegree11: " + (System.currentTimeMillis() - l));
        l = System.currentTimeMillis();
        //======================================================================
        // 
        //======================================================================
        String[] communitiesID = new String[louvian.getPartisionNumber()];
        for (int i = 0; i < communitiesID.length; i++) {
            communitiesID[i] = new String();
        }
        // add preGraph informations to new graph 
        for (Edge edge : curGraph.getEachEdge()) {
            Node n0 = edge.getNode0();
            Node n1 = edge.getNode1();
            if (newGraph.getNode(n0.getId()) != null
                    && newGraph.getNode(n1.getId()) != null) {
                // hameye etelaat ghablan add shode
                continue;
            } else if (newGraph.getNode(n0.getId()) == null
                    && newGraph.getNode(n1.getId()) != null) {

                // چک میشود آیا نود در اجتماع یک نود مخرب بوده یا نه
                if (brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n0.getId()).getAttribute("community")
                        + "-")) {
                    outNode++;
                    newGraph.addNode(n0.getId());
                    newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                    newGraph.getEdge(edge.getId()).addAttribute("weight",
                            (Double) edge.getAttribute("weight"));
                } else {
                    c1 = preGraph.getNode(n0.getId()).getAttribute("community");
                    if (newGraph.getNode(c1 + "") == null) {
                        newGraph.addNode(c1 + "");
                    }
                    communitiesID[c1] += n0.getId() + "kntu";
                    nodeInCommunityCount++;

                    if (newGraph.getEdge(c1 + "" + n1.getId()) == null) {
                        newGraph.addEdge(c1 + "" + n1.getId(), c1 + "", n1.getId() + "");
                        newGraph.getEdge(c1 + "" + n1.getId()).setAttribute("weight",
                                edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(c1 + "" + n1.getId()).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(c1 + "" + n1.getId())
                                        .getAttribute("weight"));
                    }
                }
            } else if (newGraph.getNode(n0.getId()) != null
                    && newGraph.getNode(n1.getId()) == null) {

                // چک میشود آیا نودی که در ابر گره وجود دارد مخرب است یا نه
                if (brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n1.getId()).getAttribute("community")
                        + "-")) {
                    outNode++;
                    newGraph.addNode(n1.getId());
                    newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                    newGraph.getEdge(edge.getId()).addAttribute("weight",
                            (Double) edge.getAttribute("weight"));
                } else {

                    c2 = preGraph.getNode(n1.getId()).getAttribute("community");
                    if (newGraph.getNode(c2 + "") == null) {
                        newGraph.addNode(c2 + "");
                    }
                    communitiesID[c2] += n1.getId() + "kntu";
                    nodeInCommunityCount++;
                    if (newGraph.getEdge(n0.getId() + "" + c2) == null) {
                        newGraph.addEdge(n0.getId() + "" + c2, n0.getId() + "", c2 + "");
                        newGraph.getEdge(n0.getId() + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(n0.getId() + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(n0.getId() + "" + c2).getAttribute("weight"));
                    }
                }
            } else { // هر دوتا گره تو گراف جدید نیستن

                if (!brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n1.getId()).getAttribute("community")
                        + "-")
                        && !brokenCommunity.contains(
                                "-"
                                + preGraph.getNode(n0.getId()).getAttribute("community")
                                + "-")) {

                    // it means that n0 and n1 didn't exsist in newGraph
                    c1 = preGraph.getNode(n0.getId()).getAttribute("community");
                    if (newGraph.getNode(c1 + "") == null) {
                        newGraph.addNode(c1 + "");
                    }
                    communitiesID[c1] += n0.getId() + "kntu";
                    c2 = preGraph.getNode(n1.getId()).getAttribute("community");
                    if (newGraph.getNode(c2 + "") == null) {
                        newGraph.addNode(c2 + "");
                    }
                    communitiesID[c2] += n1.getId() + "kntu";
                    nodeInCommunityCount += 2;

                    if (newGraph.getEdge(c1 + "" + c2) == null) {
                        newGraph.addEdge(c1 + "" + c2, c1 + "", c2 + "");
                        newGraph.getEdge(c1 + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(c1 + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(c1 + "" + c2).getAttribute("weight"));
                    }
                    edgeInCommunityCount++;

                } else if (!brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n1.getId()).getAttribute("community")
                        + "-")
                        && brokenCommunity.contains(
                                "-"
                                + preGraph.getNode(n0.getId()).getAttribute("community")
                                + "-")) {

                    newGraph.addNode(n0.getId());
                    outNode++;

                    c2 = preGraph.getNode(n1.getId()).getAttribute("community");
                    if (newGraph.getNode(c2 + "") == null) {
                        newGraph.addNode(c2 + "");
                    }
                    communitiesID[c2] += n1.getId() + "kntu";
                    if (newGraph.getEdge(n0.getId() + "" + c2) == null) {
                        newGraph.addEdge(n0.getId() + "" + c2, n0.getId() + "", c2 + "");
                        newGraph.getEdge(n0.getId() + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(n0.getId() + "" + c2).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(n0.getId() + "" + c2).getAttribute("weight"));
                    }
                } else if (brokenCommunity.contains(
                        "-"
                        + preGraph.getNode(n1.getId()).getAttribute("community")
                        + "-")
                        && !brokenCommunity.contains(
                                "-"
                                + preGraph.getNode(n0.getId()).getAttribute("community")
                                + "-")) {

                    newGraph.addNode(n1.getId());

                    c1 = preGraph.getNode(n0.getId()).getAttribute("community");
                    if (newGraph.getNode(c1 + "") == null) {
                        newGraph.addNode(c1 + "");
                    }
                    communitiesID[c1] += n0.getId() + "kntu";
                    nodeInCommunityCount++;

                    if (newGraph.getEdge(c1 + "" + n1.getId()) == null) {
                        newGraph.addEdge(c1 + "" + n1.getId(), c1 + "", n1.getId() + "");
                        newGraph.getEdge(c1 + "" + n1.getId()).setAttribute("weight",
                                edge.getAttribute("weight"));
                    } else {
                        newGraph.getEdge(c1 + "" + n1.getId()).setAttribute("weight",
                                (Double) edge.getAttribute("weight")
                                + (Double) newGraph.getEdge(c1 + "" + n1.getId())
                                        .getAttribute("weight"));
                    }
                } else {

                    outNode += 2;
                    newGraph.addNode(n0.getId());
                    newGraph.addNode(n1.getId());
                    newGraph.addEdge(edge.getId(), n0.getId(), n1.getId());
                    newGraph.getEdge(edge.getId()).addAttribute("weight",
                            (Double) edge.getAttribute("weight"));
                }

            }
        }
        System.out.println("createNewGraphWithDegree22: " + (System.currentTimeMillis() - l));
        for (int i = 0;
                i < louvian.getPartisionNumber();
                i++) {
            if (newGraph.getNode(i + "") != null) {
                newGraph.getNode(i + "").addAttribute("label", communitiesID[i]);
                communityUsedCount++;
            }
        }

        System.out.println("Community used Count: " + communityUsedCount);
        System.out.println("new Node Count: " + newNodesCount);
        System.out.println("new Edge Count: " + newEdgesCount);
        System.out.println("Node in Community Count: " + nodeInCommunityCount);
        System.out.println("Edge in Community Count: " + edgeInCommunityCount);
        System.out.println("Node out Community Count: " + outNode);
        System.out.println("destyousNode: " + destroyedNode);

        DataStorage.communityUsedCount.add(communityUsedCount);
        DataStorage.newNodesCount.add(newNodesCount);
        DataStorage.newEdgesCount.add(newEdgesCount);
        DataStorage.nodeInCommunityCount.add(nodeInCommunityCount);
        DataStorage.edgeInCommunityCount.add(edgeInCommunityCount);
        DataStorage.destroyedNode.add(destroyedNode);
        DataStorage.outNodesCount.add(outNode);
        DataStorage.sumAllNodecount.add(outNode
                + nodeInCommunityCount
                + newNodesCount
                + destroyedNode);

        return newGraph;
    }

    /**
     *
     * @param pr
     * @param graph
     * @returnPropo *
     */
    private double calculateAvrgPR(PageRank pr, MultiGraph graph) {
        double sum = 0;
        for (Node node : graph) {
            sum += pr.getRank(node);
        }
        return sum / graph.getNodeCount();
    }

    /**
     *
     * @param curGraph
     * @return
     */
    private double calculateAvrgDegree(MultiGraph curGraph) {
//        long l = System.currentTimeMillis();
        double sum = 0;
        for (Node node : curGraph) {
            sum += node.getDegree();
        }
//        System.out.println("calculateAvrgDegree:  " + (System.currentTimeMillis()-l));
        return sum / curGraph.getNodeCount();
    }
}
