/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset;

import java.util.ArrayList;

/**
 *
 * @author mahsa
 */
public class MergeSort {

    private final ArrayList<EnronEmail> emails;

    public MergeSort(ArrayList<EnronEmail> emails) {
        this.emails = emails;
    }

    // Print Array
    public void printArray(int[] array) {
        for (int i : array) {
            System.out.printf("%d ", i);
        }
        System.out.printf("n");
    }

    // Bottom-up merge sort
    public void mergeSort() {
        if (emails.size() < 2) {
            // We consider the array already sorted, no change is done
            return;
        }
        // The size of the sub-arrays . Constantly changing .
        int step = 1;
        // startL - start index for left sub-array
        // startR - start index for the right sub-array
        int startL, startR;

        while (step < emails.size()) {
            startL = 0;
            startR = step;
            while (startR + step <= emails.size()) {
                mergeArrays(emails, startL, startL + step, startR, startR + step);
                // System.out.printf("startL=%d, stopL=%d, startR=%d, stopR=%dn",
                // startL, startL + step, startR, startR + step);
                startL = startR + step;
                startR = startL + step;
            }
            if (startR < emails.size()) {
                mergeArrays(emails, startL, startL + step, startR, emails.size());
                // System.out.printf("* startL=%d, stopL=%d, startR=%d, stopR=%dn",
                // startL, startL + step, startR, array.length);
            }
            step *= 2;
        }
    }

    // Merge to already sorted blocks
    public void mergeArrays(ArrayList<EnronEmail> array, int startL, int stopL,
            int startR, int stopR) {
        // Additional arrays needed for merging
        EnronEmail[] right = new EnronEmail[stopR - startR + 1];
        EnronEmail[] left = new EnronEmail[stopL - startL + 1];

        // Copy the elements to the additional arrays
        for (int i = 0, k = startR; i < (right.length - 1); ++i, ++k) {
            right[i] = array.get(k);
        }
        for (int i = 0, k = startL; i < (left.length - 1); ++i, ++k) {
            left[i] = array.get(k);
        }

        // Adding sentinel values
        right[right.length - 1] = new EnronEmail();
        left[left.length - 1] = new EnronEmail();

        // Merging the two sorted arrays into the initial one
        for (int k = startL, m = 0, n = 0; k < stopR; ++k) {
            if (right[n].getDate().isGreater(left[m].getDate())) {
                array.set(k, left[m]);
                m++;
            } else {
                array.set(k, right[n]);
                n++;
            }
        }
    }

}
