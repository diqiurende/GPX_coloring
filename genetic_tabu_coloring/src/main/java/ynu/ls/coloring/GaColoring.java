package ynu.ls.coloring;

import ynu.ls.coloring.utils.GPXUtils;
import ynu.ls.coloring.utils.TABUUtils;

import java.util.ArrayList;
import java.util.List;

public class GaColoring {
    public static int COLORNUM = 48;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        GPXUtils gpxUtils = new GPXUtils();
        TABUUtils tabuUtils = new TABUUtils();

        int[][] graph = GraphFileReader.initGraphArray();
        List<int[]> adjList = GraphFileReader.initGraphList(graph);

        int[] graphColorGeneration1 = tabuUtils.initColoring(COLORNUM);
        int[] graphColorGeneration2 = tabuUtils.initColoring(COLORNUM);

        int[] elite1 = graphColorGeneration2.clone();
        int[] elite2 = graphColorGeneration1.clone();

        ArrayList<ArrayList<Integer>> colorVertex1 = tabuUtils.getColorVertex(graphColorGeneration1);
        ArrayList<ArrayList<Integer>> colorVertex2 = tabuUtils.getColorVertex(graphColorGeneration2);

        int[] c1;
        int[] c2;
        int[] best;

        if (tabuUtils.getConflictNum(adjList, graphColorGeneration1) > tabuUtils.getConflictNum(adjList, graphColorGeneration2)) {
            best = graphColorGeneration2.clone();
        } else {
            best = graphColorGeneration1.clone();
        }

        int generation = 1;
        int cycle = 1;

        do {
            c1 = gpxUtils.GPX(colorVertex1, colorVertex2, graphColorGeneration1, graphColorGeneration2);
            c2 = gpxUtils.GPX(colorVertex2, colorVertex1, graphColorGeneration2, graphColorGeneration1);

            graphColorGeneration1 = tabuUtils.tabuSearch(c1, adjList);
            graphColorGeneration2 = tabuUtils.tabuSearch(c2, adjList);

            colorVertex1 = tabuUtils.getColorVertex(graphColorGeneration1);
            colorVertex2 = tabuUtils.getColorVertex(graphColorGeneration2);

            int conflictGene1 = tabuUtils.getConflictNum(adjList, graphColorGeneration1);
            int conflictGene2 = tabuUtils.getConflictNum(adjList, graphColorGeneration2);

            int conflictElite = tabuUtils.getConflictNum(adjList, elite1);

            if (conflictElite < conflictGene1) {
                if (conflictElite > conflictGene2) {
                    elite1 = graphColorGeneration2.clone();
                    conflictElite = conflictGene2;
                }
            } else {
                if (conflictGene2 > conflictGene1) {
                    elite1 = graphColorGeneration1.clone();
                    conflictElite = conflictGene1;
                } else {
                    elite1 = graphColorGeneration2.clone();
                    conflictElite = conflictGene2;
                }
            }
            int conflict = tabuUtils.getConflictNum(adjList, elite1);

            if (conflictElite < tabuUtils.getConflictNum(adjList, best)) {
                best = elite1.clone();
                conflict = tabuUtils.getConflictNum(adjList, best);
            } else {
                conflict = tabuUtils.getConflictNum(adjList, best);
            }

            if (generation % 15 == 0) {
                graphColorGeneration1 = elite2.clone();
                colorVertex1 = tabuUtils.getColorVertex(graphColorGeneration1);
                elite2 = elite1.clone();
                elite1 = tabuUtils.initColoring(COLORNUM);
                cycle++;
            }

            generation++;

            if (generation % 100 == 0) {
                System.out.println("gene:" + generation +
                        ", conflict:" + tabuUtils.getConflictNum(adjList, graphColorGeneration2) +
                        ", best:" + tabuUtils.getConflictNum(adjList, best));
            }

        } while (tabuUtils.getConflictNum(adjList, best) > 0 && !graphColorGeneration1.equals(graphColorGeneration2));

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total execution time: " + totalTime + " milliseconds");
        System.out.println();
    }

}
