package cn.a416;

import cn.a416.utils.GPXUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * author:宋明远
 */
public class HEAD {

    public static int SIZE = 500;
    public static Random ra =new Random();
    public static int COLORNUM = 48;
    public static int [][]graph = initGraphArray();
    public static List<int[]> adjList = initGraphList(graph);
    public static int TABUNUM = 85;


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
            coloring[i] = ra.nextInt(COLORNUM);
        }
        return coloring;
    }

    /**
     * 获取当前解对应的颜色-顶点列表
     * @param graphColor
     * @return ArrayList<ArrayList<Integer>> getColorVertex
     */
    public static ArrayList<ArrayList<Integer>> getColorVertex(int graphColor[]){

        ArrayList<ArrayList<Integer>> colorVertex = new ArrayList<>();
        for (int i = 0; i < COLORNUM; i++) {
            colorVertex.add(new ArrayList<Integer>());
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
     * 对一个图-颜色数组进行指定轮数的禁忌搜索
     * @param graphColor
     * @return
     */
    public static int[] tabuSearch(int[] graphColor) {

        int []bestSolve = graphColor.clone();

        int [][]vertexNeighbourColor = getVertexColor(adjList,graphColor);

        int iter = 0;

        int [][]tabuList = new int[SIZE][COLORNUM];

        int conflictNum = getConflictNum(adjList,graphColor);

        int bestConflictNum = conflictNum;

//        System.out.println("init Conflict:" + conflictNum);
//        HashMap<Integer, ArrayList<Integer>> colorVertex = getColorVertex(graphColor);

        int vertexNum = 0; int colorNum = 0;

        int maxScore = - 9999;

        List<Integer> bestPoint = new ArrayList<Integer>();

        List<Integer> bestColor = new ArrayList<Integer>();

        do {

            bestPoint.clear();
            bestColor.clear();
            bestColor.add(0);
            bestPoint.add(0);
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
                            vertexNum = i;
                            colorNum = i1;
                            bestPoint.add(i);
                            bestColor.add(i1);
                        }
                        if (currentScore > maxScore){
                            if (conflictNum - currentScore < bestConflictNum){
                                tabuList[vertexNum][colorNum] = iter;
                                maxScore = currentScore;
                                bestPoint.clear();
                                bestColor.clear();
                                vertexNum = i;
                                colorNum = i1;
                                bestPoint.add(i);
                                bestColor.add(i1);
                            }
                            // 若没有在禁忌表中且比当前 最好值 更大
                            else if (conflictNum - currentScore >= bestConflictNum && tabuList[i][i1] < iter){
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

            // 将顶点对应的颜色更新
            conflictNum = conflictNum - maxScore;

            int idx = ra.nextInt(bestPoint.size());

            vertexNum = bestPoint.get(idx);

            colorNum = bestColor.get(idx);

            if (conflictNum < bestConflictNum) {
                tabuList[vertexNum][colorNum] = iter + conflictNum + ra.nextInt(TABUNUM);
                bestConflictNum = conflictNum;
                bestSolve = graphColor.clone();
            } else {
                tabuList[vertexNum][colorNum] = iter +conflictNum + ra.nextInt(TABUNUM);
            }

            for (int i = 0; i < adjList.get(vertexNum).length; i++) {
                // 邻居的顶点颜色数量表中，被选中顶点的旧的颜色的值-1，新的颜色的值+1
                vertexNeighbourColor[adjList.get(vertexNum)[i]][graphColor[vertexNum]]--;
                vertexNeighbourColor[adjList.get(vertexNum)[i]][colorNum]++;
            }
            // 更新顶点的颜色
            graphColor[vertexNum] = colorNum;

            iter++;

            if (conflictNum==0){
                System.out.println("iter:" + iter+ "，" + "conflict:" + conflictNum);
                bestSolve = graphColor.clone();
                return bestSolve;
            }

        } while (iter < 10000 && conflictNum > 0);

//        System.out.println("best:" + bestConflictNum);

        return graphColor;
    }


    public static void printGraphColor(int[] graphColor) {
        for (int i = 0; i < graphColor.length; i++) {
            System.out.print(graphColor[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        GPXUtils gpxUtils = new GPXUtils();

        int []graphColorGeneration1 = getRandColoring(COLORNUM);//随机的color解集

        int []graphColorGeneration2 = getRandColoring(COLORNUM);

        //HEAD2中的优化
        int []elite1 = graphColorGeneration2.clone();

        int []elite2 = graphColorGeneration1.clone();

        ArrayList<ArrayList<Integer>> colorVertex1 = getColorVertex(graphColorGeneration1);//颜色-顶点邻接矩阵

        ArrayList<ArrayList<Integer>> colorVertex2 = getColorVertex(graphColorGeneration2);

        int []c1 = new int[SIZE];

        int []c2 = new int[SIZE];

        int []best = new int[SIZE];

        if (getConflictNum(adjList,graphColorGeneration1) > getConflictNum(adjList,graphColorGeneration2)) {//设置best为最少冲突边的方案
            best = graphColorGeneration2.clone();
        } else {
            best = graphColorGeneration1.clone();
        }

        int generation = 1;
        int cycle = 1;


        do {
            c1 = gpxUtils.GPX(colorVertex1,colorVertex2,graphColorGeneration1,graphColorGeneration2);

            c2 = gpxUtils.GPX(colorVertex2,colorVertex1,graphColorGeneration2,graphColorGeneration1);

            graphColorGeneration1 = tabuSearch(c1);
            graphColorGeneration2 = tabuSearch(c2);

            colorVertex1 = getColorVertex(graphColorGeneration1);
            colorVertex2 = getColorVertex(graphColorGeneration2);

            int conflictGene1 = getConflictNum(adjList,graphColorGeneration1);
            int conflictGene2 = getConflictNum(adjList,graphColorGeneration2);

            int conflictElite = getConflictNum(adjList, elite1);

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
            int conflict = getConflictNum(adjList,elite1);

            if (conflictElite < getConflictNum(adjList,best)) {
                best = elite1.clone();
                conflict = getConflictNum(adjList,best);
            } else {
                conflict = getConflictNum(adjList, best);
            }
//
            if (generation % 15 == 0){
                graphColorGeneration1 = elite2.clone();
                colorVertex1 = getColorVertex(graphColorGeneration1);
                elite2 = elite1.clone();
                elite1 = getRandColoring(COLORNUM);
                cycle ++;
            }

            generation ++;

//            // HEAD1
//            int conflict = getConflictNum(adjList,graphColorGeneration1);
//            if (getConflictNum(adjList,graphColorGeneration2) < conflict){
//                if (getConflictNum(adjList,graphColorGeneration2) < getConflictNum(adjList,best)) {
//                    conflict = (getConflictNum(adjList,graphColorGeneration2));
//                    best = graphColorGeneration2;
//                }
//            } else {
//                if (conflict < getConflictNum(adjList,best)) {
//                    best = graphColorGeneration1;
//                }
//            }
//
            if (generation % 100 == 0) {
                System.out.println("gene:" + generation + ",conflict:" + getConflictNum(adjList,graphColorGeneration2) + ",best:" + getConflictNum(adjList,best));
            }

        }while (getConflictNum(adjList,best) > 0 && !graphColorGeneration1.equals(graphColorGeneration2));
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total execution time: " + totalTime + " milliseconds");

        System.out.println();
    }

}
