/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import utils.TypeAlgorithm;
import utils.TypeDestroyedNode;
import utils.DataStorage;
import java.io.IOException;
import javax.swing.JOptionPane;
import louvian.DynamicCommunityDetection;

/**
 *
 * @author mahsa
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        String typeAlgorithm = JOptionPane.showInputDialog("Enter type of algorithm:"
                + "\n1.Louvian\n2.Proposed");

        String typeDestroyed = "";
        
        if (typeAlgorithm.equals("Proposed")) {
            typeDestroyed = JOptionPane.showInputDialog("Enter type of destroyed node:"
                    + "\n1.Degree\n2.PR");
            new DynamicCommunityDetection(TypeDestroyedNode.valueOf(typeDestroyed),
                    TypeAlgorithm.valueOf(typeAlgorithm)).run();

        } else {
            new DynamicCommunityDetection(null,
                    TypeAlgorithm.valueOf(typeAlgorithm)).run();
        }
        DataStorage.writeDataInExcel();
    }

}
