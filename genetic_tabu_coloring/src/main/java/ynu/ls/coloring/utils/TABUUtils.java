package ynu.ls.coloring.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TABUUtils {
    public static int SIZE = 500;
    public static int COLORNUM = 48;
    public static int TABUNUM = 85;
    public static Random ra = new Random();

    /**
     * 初始化每个点的随机颜色
     * @param colorNum
     * @return int[]
     */
    public int[] initColoring(int colorNum) {
        int[] coloring = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            coloring[i] = ra.nextInt(colorNum);
        }
        return coloring;
    }

    /**
     * 获取当前解对应的颜色-顶点列表
     * @param graphColor
     * @return ArrayList<ArrayList<Integer>>
     */
    public ArrayList<ArrayList<Integer>> getColorVertex(int[] graphColor) {
        ArrayList<ArrayList<Integer>> colorVertex = new ArrayList<>();
        for (int i = 0; i < COLORNUM; i++) {
            colorVertex.add(new ArrayList<>());
        }
        for (int i = 0; i < graphColor.length; i++) {
            colorVertex.get(graphColor[i]).add(i);
        }
        return colorVertex;
    }

    /**
     * 获取初始化冲突数量
     * @param adjList
     * @param graphColor
     * @return int
     */
    public int getConflictNum(List<int[]> adjList, int[] graphColor) {
        int conflict = 0;
        for (int i = 0; i < adjList.size(); i++) {
            for (int j = 0; j < adjList.get(i).length; j++) {
                if (graphColor[i] == graphColor[adjList.get(i)[j]]) conflict++;
            }
        }
        return conflict / 2;
    }

    /**
     * 初始化顶点对应的邻居的各种颜色数量分布
     * @param adjList
     * @param graphColor
     * @return int[][]
     */
    public int[][] getVertexColor(List<int[]> adjList, int[] graphColor) {
        int[][] vertexColor = new int[SIZE][COLORNUM];
        for (int i = 0; i < adjList.size(); i++) {
            for (int i1 = 0; i1 < adjList.get(i).length; i1++) {
                int color = graphColor[adjList.get(i)[i1]];
                vertexColor[i][color]++;
            }
        }
        return vertexColor;
    }

    /**
     * 对一个图-颜色数组进行指定轮数的禁忌搜索
     * @param graphColor
     * @param adjList
     * @return int[]
     */
    public int[] tabuSearch(int[] graphColor, List<int[]> adjList) {
        int[] bestSolve = graphColor.clone();
        int[][] vertexNeighbourColor = getVertexColor(adjList, graphColor);
        int iter = 0;
        int[][] tabuList = new int[SIZE][COLORNUM];
        int conflictNum = getConflictNum(adjList, graphColor);
        int bestConflictNum = conflictNum;

        int vertexNum;
        int colorNum;
        int maxScore;

        List<Integer> bestPoint = new ArrayList<>();
        List<Integer> bestColor = new ArrayList<>();

        do {
            bestPoint.clear();
            bestColor.clear();
            bestColor.add(0);
            bestPoint.add(0);
            vertexNum = 0;
            colorNum = 0;
            maxScore = -9999;

            for (int i = 0; i < vertexNeighbourColor.length; i++) {
                if (vertexNeighbourColor[i][graphColor[i]] == 0) {
                    continue;
                }
                for (int i1 = 0; i1 < vertexNeighbourColor[i].length; i1++) {
                    if (graphColor[i] == i1) {
                        continue;
                    } else {
                        int currentScore = vertexNeighbourColor[i][graphColor[i]] - vertexNeighbourColor[i][i1];
                        if (currentScore == maxScore && tabuList[i][i1] < iter) {
                            vertexNum = i;
                            colorNum = i1;
                            bestPoint.add(i);
                            bestColor.add(i1);
                        }
                        if (currentScore > maxScore) {
                            if (conflictNum - currentScore < bestConflictNum) {
                                tabuList[vertexNum][colorNum] = iter;
                                maxScore = currentScore;
                                bestPoint.clear();
                                bestColor.clear();
                                vertexNum = i;
                                colorNum = i1;
                                bestPoint.add(i);
                                bestColor.add(i1);
                            } else if (conflictNum - currentScore >= bestConflictNum && tabuList[i][i1] < iter) {
                                maxScore = currentScore;
                                bestPoint.clear();
                                bestColor.clear();
                                vertexNum = i;
                                colorNum = i1;
                                bestPoint.add(i);
                                bestColor.add(i1);
                            }
                        }
                    }
                }
            }

            conflictNum = conflictNum - maxScore;

            int idx = ra.nextInt(bestPoint.size());

            vertexNum = bestPoint.get(idx);
            colorNum = bestColor.get(idx);

            if (conflictNum < bestConflictNum) {
                tabuList[vertexNum][colorNum] = iter + conflictNum + ra.nextInt(TABUNUM);
                bestConflictNum = conflictNum;
                bestSolve = graphColor.clone();
            } else {
                tabuList[vertexNum][colorNum] = iter + conflictNum + ra.nextInt(TABUNUM);
            }

            for (int i = 0; i < adjList.get(vertexNum).length; i++) {
                vertexNeighbourColor[adjList.get(vertexNum)[i]][graphColor[vertexNum]]--;
                vertexNeighbourColor[adjList.get(vertexNum)[i]][colorNum]++;
            }
            graphColor[vertexNum] = colorNum;
            iter++;

            if (conflictNum == 0) {
                System.out.println("iter:" + iter + "，" + "conflict:" + conflictNum);
                bestSolve = graphColor.clone();
                return bestSolve;
            }

        } while (iter < 10000 && conflictNum > 0);

        return graphColor;
    }

}
