package cn.a416;

import cn.a416.utils.HungarianAlgorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * author:宋明远
 */
public class MACol {

    public static int SIZE = 500;
    public static Random ra =new Random();
    public static int COLORNUM = 49;
    public static int [][]graph = initGraphArray();
    public static List<int[]> adjList = initGraphList(graph);
    public static int TABUITERNUM = 100000;
    public static int PARENTSNUM = 8;
    public static double LAMBDA = 0.08;
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
     * @param
     * @return int coloring[500]
     */
    public static int[] getRandColoring(){
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

        } while (iter < TABUITERNUM && conflictNum > 0);

//        System.out.println("best:" + bestConflictNum);

        return graphColor;
    }

    /**
     * 通过colorvertex生成原始的解
     * @param colorVertex
     * @return
     */
    public static int[] getSolveByColorVertex(ArrayList<ArrayList<Integer>> colorVertex){
        int[] res = new int[SIZE];

        for (int i = 0; i < colorVertex.size(); i++) {
            for (int i1 = 0; i1 < colorVertex.get(i).size(); i1++) {
                res[colorVertex.get(i).get(i1)] = i;
            }
        }

        return res;
    }

    /**
     * 对一堆parent进行AMPAX操作
     * @param parents
     * @return int[] 一个交叉后的解
     */
    public static int[] AMPAX(ArrayList<ArrayList<ArrayList<Integer>>> parents){
        ArrayList<ArrayList<ArrayList<Integer>>> parentsCP = new ArrayList<ArrayList<ArrayList<Integer>>>();
        int []res = new int[SIZE];
        for (int i = 0; i < res.length; i++) {
            res[i] = -1;
        }

        // 深度拷贝parents列表，因为在这里引用类型会改变其中的值
        for (int i = 0; i < parents.size(); i++) {
            ArrayList<ArrayList<Integer>> listOuter = new ArrayList<>();
            for (int i1 = 0; i1 < parents.get(i).size(); i1++) {
                ArrayList<Integer> listInner = new ArrayList<>();
                for (int i2 = 0; i2 < parents.get(i).get(i1).size(); i2++) {
                    listInner.add(parents.get(i).get(i1).get(i2));
                }
                listOuter.add(listInner);
            }
            parentsCP.add(listOuter);
        }

        // 初始化禁忌表
        int[] tabuParList = new int[parentsCP.size()];

        // 挑选最大集合
        for (int i = 0; i < COLORNUM; i++) {
            // 寻找颜色对应的点最多的父代的颜色以及里面的点
            int maxPar = 0;
            int maxCol = 0;
            int maxPointNum = 0;
            for (int i1 = 0; i1 < parentsCP.size(); i1++) {
                if (tabuParList[i1] < i) {
                    for (int i2 = 0; i2 < parentsCP.get(i1).size(); i2++) {
                        if (parentsCP.get(i1).get(i2).size() > maxPointNum) {
                            maxPar = i1;
                            maxCol = i2;
                            maxPointNum = parentsCP.get(i1).get(i2).size();
                        }
                    }
                }
            }

            // 找到了颜色对应点最多的父代以及其对应的颜色
            // 接下来需要将这个父类对应的颜色的所有点加入解中
            for (int i1 = 0; i1 < parentsCP.get(maxPar).get(maxCol).size(); i1++) {
                res[parentsCP.get(maxPar).get(maxCol).get(i1)] = i;
            }

            // 这一步需要将所有父类中，这些已经被选出的点进行删除
            // 先将非该父类的其它父类中的这些点删除
            for (int i1 = 0; i1 < parentsCP.size(); i1++) {
                if (i1 != maxPar) {
                    for (int i2 = 0; i2 < parentsCP.get(i1).size(); i2++) {
                        for (int i3 = 0; i3 < parentsCP.get(i1).get(i2).size(); i3++) {
                            for (int i4 = 0; i4 < parentsCP.get(maxPar).get(maxCol).size(); i4++) {
                                if (parentsCP.get(maxPar).get(maxCol).get(i4) == parentsCP.get(i1).get(i2).get(i3)){
                                    parentsCP.get(i1).get(i2).remove((Integer) i3);
                                }
                            }
                        }
                    }
                }
            }

            // 将找到的最大父类中的该颜色下的所有颜色对应的点删除
            for (int i1 = 0; i1 < parentsCP.get(maxPar).get(maxCol).size(); i1++) {
                parentsCP.get(maxPar).get(maxCol).remove((Integer) i1);
            }

            // 将该父类加入禁忌表中
            tabuParList[maxPar] = i + parentsCP.size()/2;
        }

        // 将没有颜色的点随机附上颜色
        for (int i = 0; i < res.length; i++) {
            if (res[i] == -1) {
                res[i] = ra.nextInt(COLORNUM);
            }
        }
        return res;
    }

    /**
     * 对两个解对应的颜色-点集合进行构造二部图的操作
     * @param colorVertex1
     * @param colorVertex2
     * @status
     * @return int[][] bipartiteGraph
     */
    public static int[][] getBipartiteInput(ArrayList<ArrayList<Integer>> colorVertex1, ArrayList<ArrayList<Integer>> colorVertex2){

        int[][] bipartiteGraph = new int[COLORNUM][COLORNUM];

        for (int i = 0; i < colorVertex1.size(); i++) {
            for (int i1 = 0; i1 < colorVertex2.size(); i1++) {
                for (int i2 = 0; i2 < colorVertex1.get(i).size(); i2++) {
                    for (int i3 = 0; i3 < colorVertex2.get(i1).size(); i3++) {
                        if (colorVertex1.get(i).get(i2) .equals(colorVertex2.get(i1).get(i3)) ) {
                            bipartiteGraph[i][i1] ++;
                            bipartiteGraph[i1][i] ++;
                        }
                    }
                }
            }
        }

        return bipartiteGraph;
    }

    /**
     * 获取两个解对应颜色-点集合的二部图的距离
     * @status 借助了github上的匈牙利算法
     * @param bipartiteGraph
     * @return int
     */
    public static int getDistance(int [][] bipartiteGraph){
        int [][]graphCp = new int[COLORNUM][COLORNUM];
        for (int i = 0; i < bipartiteGraph.length; i++) {
            for (int i1 = 0; i1 < bipartiteGraph[i].length; i1++) {
                graphCp[i][i1] = - bipartiteGraph[i][i1];
            }
        }

        // 通过全部取反获取它的最小花费
        HungarianAlgorithm ha = new HungarianAlgorithm(graphCp);
        int[][] assignment = ha.findOptimalAssignment();

        // 将最小花费和求出
        int sum = 0;
        for (int i = 0; i < assignment.length; i++) {
            sum += bipartiteGraph[assignment[i][0]][assignment[i][1]];
        }

        return SIZE - sum;
    }

    /**
     * 根据搜索到的解和原来的种群进行更新。。。
     * @param population
     * @param solve
     * @return 一个三维的ArrayList population
     */
    public static ArrayList<ArrayList<ArrayList<Integer>>> poolUpdating(ArrayList<ArrayList<ArrayList<Integer>>> population, ArrayList<ArrayList<Integer>> solve){

        ArrayList<ArrayList<ArrayList<Integer>>> populationBefore = new ArrayList<ArrayList<ArrayList<Integer>>>();
        // 将旧的种群copy下来
        for (int i = 0; i < population.size(); i++) {
            ArrayList<ArrayList<Integer>> outer = new ArrayList<>();
            for (int i1 = 0; i1 < population.get(i).size(); i1++) {
                ArrayList<Integer> inner = new ArrayList<>();
                for (int i2 = 0; i2 < population.get(i).get(i1).size(); i2++) {
                    inner.add(population.get(i).get(i1).get(i2));
                }
                outer.add(inner);
            }
            populationBefore.add(outer);
        }

        // poplation与solve做并集
        population.add(0,solve);

        int[] distance = new int[population.size()];
        double[] goodness = new double[population.size()];

        int []solves = new int[SIZE];
        int maxDis = -999999;

        for (int i = 0; i < population.size(); i++) {
            int minDis = 9999999;
            for (int i1 = 0; i1 < populationBefore.size(); i1++) {
                int curDis = getDistance(getBipartiteInput(population.get(i),populationBefore.get(i1)));
                if (curDis< minDis ) {
                    minDis = curDis;
                }
            }
            distance[i] = minDis;
            if(distance[i] > maxDis) maxDis = distance[i];

            solves = getSolveByColorVertex(population.get(i));
            goodness[i] = getConflictNum(adjList,solves) + Math.pow(2.7183,LAMBDA*maxDis/minDis);
        }

        int maxGoodnessIdx = 0;
        int secondIdx = 0;
        double maxValue = -99999;
        for (int i = 0; i < goodness.length; i++) {
            if (goodness[i] > maxValue) {
                secondIdx = maxGoodnessIdx;
                maxGoodnessIdx = i;
            }
        }

        if (maxGoodnessIdx != 0) {
            population.remove(maxGoodnessIdx);
        } else {
            if (ra.nextInt(10) < 2) {
                population.remove(secondIdx);
            }
        }
        return population;
    }


    public static void main(String[] args) {

        ArrayList<int[]> populations = new ArrayList<int[]>();
        ArrayList<ArrayList<ArrayList<Integer>>> popColor1 = new ArrayList<ArrayList<ArrayList<Integer>>>();

        // 初始化种群
        for (int i = 0; i < PARENTSNUM; i++) {
            populations.add(getRandColoring());
        }

        // 进行一轮禁忌搜索，更新种群
        int minIdx = 0;
        int minConflict = 99999999;

        for (int i = 0; i < populations.size(); i++) {
            int[] population = tabuSearch(populations.get(i));
            if (getConflictNum(adjList,population) < minConflict){
                minConflict = getConflictNum(adjList,population);
                minIdx = i;
            }
            populations.set(i,population);
        }

        for (int i = 0; i < populations.size(); i++) {
            ArrayList<ArrayList<Integer>> popTmp = getColorVertex(populations.get(i));
            popColor1.add(popTmp);
        }

        int iter = 0;

        do {

            ArrayList<ArrayList<ArrayList<Integer>>> randomPop = new ArrayList<>();

            for (int i = 0; i < populations.size(); i++) {
                if (i > 0){
                    randomPop.add(getColorVertex(populations.get(i)));
                }
            }

            int[] ampax = AMPAX(randomPop);

            ampax = tabuSearch(ampax);

            if (getConflictNum(adjList,ampax) < minConflict){
                minConflict = getConflictNum(adjList,ampax);
            }

            ArrayList<ArrayList<Integer>> solve0 = getColorVertex(ampax);

            popColor1 = poolUpdating(popColor1,solve0);

            iter++;

            if (iter%10 == 0) {
                System.out.println("conflict:" + minConflict+",iter:" + iter);
            }
        } while (minConflict > 0);
    }

}
