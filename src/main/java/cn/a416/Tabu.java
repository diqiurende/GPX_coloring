package cn.a416;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * author:宋明远
 */
public class Tabu {

    public static int SIZE = 500;
    public static Random ra = new Random();
    public static int COLORNUM = 50;
    /**
     * 从文件中初始化图
     * @return int[500][500]
     */
    public static int[][] initGraphArray() {
        int[][] graph = new int[SIZE][SIZE];
        try {
            BufferedReader br = new BufferedReader(new FileReader("src\\main\\resources\\DSJC500.5.col"));
            String contentLine;
            int i = 0;
            while ((contentLine = br.readLine()) != null) {
                String[] strs = contentLine.split(" ");
                if (strs[0].equals("e")) {
                    int col = Integer.parseInt(strs[1]);
                    int row = Integer.parseInt(strs[2]);
                    graph[col-1][row-1] = 1;
                    graph[row-1][col-1] = 1;
                    i++;
                }
            }
            System.out.println("i:"+i);
            return graph;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将int[500][500]变为邻接矩阵
     * @param graph int[500][500]
     * @return
     */
    public static List<int[]> initGraphList(int[][] graph) {
        int size;
        List<int[]> adjList = new ArrayList<int[]>();
        for (int i = 0; i < graph.length; i++) {
            size = 0;
            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j] == 1) {
                    size++;
                }
            }
            int[] array = new int[size];
            int flag = 0;

            for (int j = 0; j < graph[i].length; j++) {
                if (graph[i][j] == 1) {
                    array[flag] = j;
                    flag++;
                }
            }
            adjList.add(array);

        }
        return adjList;
    }

    /**
     * 初始化每个点的随机颜色
     * @param colorNum
     * @return int coloring[500]
     */
    public static int[] getRandColoring(int colorNum){
        int[] coloring = new int[SIZE];
        for (int i=0;i<SIZE;i++) {
            coloring[i] = ra.nextInt(colorNum);
        }
        return coloring;
    }

    /**
     * 初始化顶点对应的邻居的各种颜色数量分布
     * @param adjList
     * @param graphColor
     * @return int[][]
     */
    public static int[][] getVertexColor(List<int[]> adjList, int[] graphColor){
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
     * 获取初始化冲突数量
     * @param adjList
     * @param graphColor
     * @return
     */
    public static int getConflictNum(List<int[]> adjList, int[] graphColor){
        int conflict = 0;
        for (int i = 0; i < adjList.size(); i++) {
            for (int i1 = 0; i1 < adjList.get(i).length; i1++) {
                if (graphColor[i] == graphColor[adjList.get(i)[i1]]) conflict++;
            }
        }
        return conflict/2;
    };

    /**
     * 获取该顶点的movescore表
     * @param vertexNeighbourColor
     * @param graphColor
     * @return int[][]
     */
//    public static int[][] initMoveScore(int [][]vertexNeighbourColor, int []graphColor){
//        int [][] moveScore = new int[SIZE][COLORNUM];
//        for (int i = 0; i < SIZE; i++) {
//            for (int i1 = 0; i1 < vertexNeighbourColor[i].length; i1++) {
//                if (i1 == graphColor[i]) {
//                    moveScore[i][i1] = 0;
//                } else {
//                    // 一个点想要变成别的颜色，分数表上的值为它原来颜色的邻居数减去新颜色的邻居数
//                    moveScore[i][i1] = vertexNeighbourColor[i][graphColor[i]] - vertexNeighbourColor[i][i1];
//                }
//            }
//        }
//        return moveScore;
//    }
//
//    /**
//     * 获取movescore表中的最大值，点以及对应颜色的序号
//     * @param moveScore
//     * @return
//     */
//    public static int[] getMax(int [][]moveScore){
//        int x = 0;
//        int y = 0;
//        int maxScore = -999999;
//        for (int i = 0; i < moveScore.length; i++) {
//            for (int i1 = 0; i1 < moveScore[i].length; i1++) {
//                if (moveScore[i][i1] > maxScore) {
//                    maxScore = moveScore[i][i1];
//                    x = i;
//                    y = i1;
//                }
//            }
//        }
//        int []arr = new int[3];
//        arr[0] = x;arr[1] = y;arr[2] = maxScore;
//        return arr;
//    }

    public static HashMap<Integer, ArrayList<Integer>> getColorVertex(int graphColor[]){

        HashMap<Integer, ArrayList<Integer>> colorVertex = new HashMap<Integer, ArrayList<Integer>>();
        for (int i = 0; i < graphColor.length; i++) {
            if (colorVertex.containsKey(graphColor[i])){
                colorVertex.get(graphColor[i]).add(i);
            } else {
                colorVertex.put(graphColor[i],new ArrayList<Integer>());
                colorVertex.get(graphColor[i]).add(i);
            }
        }
        return colorVertex;
    }


    public static void main(String[] args) {

        int [][]graph = initGraphArray();

        List<int[]> adjList = initGraphList(graph);

        int []graphColor = getRandColoring(COLORNUM);

        int []bestSolve = graphColor.clone();

        int [][]vertexNeighbourColor = getVertexColor(adjList,graphColor);

        int iter = 0;

        int [][]tabuList = new int[SIZE][COLORNUM];

        int conflictNum = getConflictNum(adjList,graphColor);

        int bestConflictNum = conflictNum;

        System.out.println("init Conflict:" + conflictNum);
//        HashMap<Integer, ArrayList<Integer>> colorVertex = getColorVertex(graphColor);

        int vertexNum = 0; int colorNum = 0;

        int maxScore = - 9999;

        List<Integer> bestPoint = new ArrayList<Integer>();

        List<Integer> bestColor = new ArrayList<Integer>();
        do {
            bestPoint.clear();
            bestColor.clear();

            vertexNum = 0; colorNum = 0;

            maxScore = - 9999;

//            bestPoint.add(vertexNum);
//            bestColor.add(colorNum);

            for (int i = 0; i < vertexNeighbourColor.length; i++) {
                // 若当前点已经无冲突
                if (vertexNeighbourColor[i][graphColor[i]]==0){
                    continue;
                }

                for (int i1 = 0; i1 < vertexNeighbourColor[i].length; i1++) {
                    // 若当前点的颜色和表中颜色相等
                    if (graphColor[i] == i1){
                        continue;
                    } else {
                        int currentScore = vertexNeighbourColor[i][graphColor[i]] - vertexNeighbourColor[i][i1];

                        if (currentScore == maxScore  && tabuList[i][i1] < iter) {
                            bestPoint.add(i);
                            bestColor.add(i1);
                        }

                        if (currentScore > maxScore){

                            if (conflictNum - currentScore < bestConflictNum){
                                tabuList[vertexNum][colorNum] = iter;
                                maxScore = currentScore;

                                bestPoint.clear();
                                bestColor.clear();

                                bestPoint.add(i);
                                bestColor.add(i1);

                            }
                            // 若没有在禁忌表中且比当前 最好值 更大
                            else if (conflictNum - currentScore >= bestConflictNum && tabuList[i][i1] < iter){
                                maxScore = currentScore;

                                bestPoint.clear();
                                bestColor.clear();

                                bestPoint.add(i);
                                bestColor.add(i1);
                            }

                        }
                    }
                }
            }

            // 将顶点对应的颜色更新
            conflictNum = conflictNum - maxScore;

            int idx = ra.nextInt(bestPoint.size());

            vertexNum = bestPoint.get(idx);

            colorNum = bestColor.get(idx);

            if (conflictNum < bestConflictNum) {
                tabuList[vertexNum][colorNum] = iter + conflictNum + ra.nextInt(87);
                bestConflictNum = conflictNum;
                bestSolve = graphColor.clone();
            } else {
                tabuList[vertexNum][colorNum] = iter +conflictNum + ra.nextInt(87);
            }

            for (int i = 0; i < adjList.get(vertexNum).length; i++) {
                // 邻居的顶点颜色数量表中，被选中顶点的旧的颜色的值-1，新的颜色的值+1
                vertexNeighbourColor[adjList.get(vertexNum)[i]][graphColor[vertexNum]]--;
                vertexNeighbourColor[adjList.get(vertexNum)[i]][colorNum]++;
            }
            // 更新顶点的颜色
            graphColor[vertexNum] = colorNum;

            iter++;

            if (iter % 1000000 == 0){
                System.out.println("iter:"+iter+",conflict:"+conflictNum+","+"best:"+bestConflictNum);
            }

            if (conflictNum==0){
                System.out.println("iter:" + iter+ "，" + "conflict:" + conflictNum);
                break;
            }

        } while (iter < 250000000 && conflictNum > 0);

        System.out.println("best:" + bestConflictNum);

    }

}
