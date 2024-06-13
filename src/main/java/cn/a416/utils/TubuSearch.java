package cn.a416.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cn.a416.HEAD.*;

public class TubuSearch {
    public static int SIZE ;
    public static Random ra =new Random();
    public static int COLORNUM ;
    public TubuSearch(int size,int colorum){
        this.SIZE=size;
        this.COLORNUM=colorum;
    }



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
}
