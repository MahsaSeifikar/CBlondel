/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import com.csvreader.CsvWriter;
import dataset.EnronEmail;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author mahsa
 */
public class DataStorage {

    public static int snapshotNum;
    public static ArrayList<Integer> partitionSize = new ArrayList<>();
    public static ArrayList<Double> modularity = new ArrayList<>();
    public static ArrayList<Long> time = new ArrayList<>();
    public static ArrayList<Integer> allNodecount = new ArrayList<>();
    public static ArrayList<Integer> sumAllNodecount = new ArrayList<>();

    public static ArrayList<Integer> outNodesCount = new ArrayList<>();
    public static ArrayList<Integer> newNodesCount = new ArrayList<>();
    public static ArrayList<Integer> newEdgesCount = new ArrayList<>();
    public static ArrayList<Integer> nodeInCommunityCount = new ArrayList<>();
    public static ArrayList<Integer> edgeInCommunityCount = new ArrayList<>();
    public static ArrayList<Integer> communityUsedCount = new ArrayList<>();
    public static ArrayList<Integer> destroyedNode = new ArrayList<>();

    public static boolean writeDataInExcel() {

        try {
            CsvWriter sb = new CsvWriter(new FileWriter("information.csv", true), ',');
            System.out.println(partitionSize.size());
            for (int i = 0; i < partitionSize.size(); i++) {

                sb.write(partitionSize.get(i) + "");
                sb.write(modularity.get(i) + "");
                sb.write(time.get(i) + "");
                sb.write(allNodecount.get(i) + "");
                if (i == 0) {
                    sb.endRecord();
                    continue;
                }
                if (!sumAllNodecount.isEmpty()) {
                    sb.write(sumAllNodecount.get(i-1) + "");
                }

//                sb.write(DataStorage.newNodesCount.get(i - 1) + "");
//                sb.write(DataStorage.newEdgesCount.get(i - 1) + "");
//                sb.write(DataStorage.nodeInCommunityCount.get(i - 1) + "");
//                sb.write(DataStorage.edgeInCommunityCount.get(i - 1) + "");
//                sb.write(DataStorage.communityUsedCount.get(i - 1) + "");
//                sb.write(DataStorage.destroyedNode.get(i - 1) + "");
//                sb.write(DataStorage.outNodesCount.get(i - 1) + "");
//                System.out.println("111 : " + DataStorage.newNodesCount.get(i) + "");
//                System.out.println("222 : " + DataStorage.newEdgesCount.get(i) + "");
//                System.out.println("333 : " + DataStorage.nodeInCommunityCount.get(i) + "");
//                System.out.println("444 : " + DataStorage.edgeInCommunityCount.get(i) + "");
//                System.out.println("555 : " + DataStorage.communityUsedCount.get(i) + "");
////                sb.write(DataStorage.nodeOuterCount.get(i) + "");
//                System.out.println("666 : " + DataStorage.destroyedNode.get(i) + "");
//                System.out.println("777 : " + DataStorage.partitionSize.get(i) + "");
//                System.out.println("888 : " + DataStorage.modularity.get(i) + "");
//                System.out.println("999 : " + DataStorage.time.get(i) + "");
                sb.endRecord();
            }
            sb.close();
            System.out.println("data is written");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage() + "\n" + e.getLocalizedMessage());
            return false;
        }
    }
}
