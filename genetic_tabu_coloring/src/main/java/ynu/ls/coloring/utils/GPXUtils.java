package ynu.ls.coloring.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 *GPX算法通过贪婪选择和传递包含最多顶点的颜色类，将父代个体中的优质特性保留到后代中，从而提高解决方案的质量。
 * 该算法适用于遗传算法中的交叉操作，特别是在解决图着色问题时表现出色。
 * 通过简化问题规模和高效保留优质解，GPX算法能够加快遗传算法的收敛速度，生成高质量的解。
 */
public class GPXUtils {
    public static int SIZE = 500;
    public static int COLORNUM = 48;
    public static Random ra =new Random();

    /**
     * 深拷贝一个二维ArrayList
     * @param original 原始的二维ArrayList
     * @return 深拷贝的二维ArrayList
     */
    public static ArrayList<ArrayList<Integer>> deepCopy(ArrayList<ArrayList<Integer>> original) {
        ArrayList<ArrayList<Integer>> copy = new ArrayList<>();
        for (ArrayList<Integer> innerList : original) {
            ArrayList<Integer> innerCopy = new ArrayList<>(innerList);
            copy.add(innerCopy);
        }
        return copy;
    }

    /**
     * 获取包含最多顶点的颜色类的索引
     * @param geneClone 二维ArrayList表示的颜色类集合
     * @return 包含最多顶点的颜色类的索引
     */
    public static int getMaxColorClassIndex(ArrayList<ArrayList<Integer>> geneClone) {
        int maxIndex = 0;
        int maxSize = 0;

        for (int i = 0; i < geneClone.size(); i++) {
            if (geneClone.get(i).size() > maxSize) {
                maxIndex = i;
                maxSize = geneClone.get(i).size();
            }
        }
        return maxIndex;
    }

    public int[] GPX(ArrayList<ArrayList<Integer>> gene1, ArrayList<ArrayList<Integer>> gene2,
                     int[] graphColor1, int[] graphColor2){

        int []graphColor = new int[SIZE];

        // 深拷贝基因和颜色数组
        ArrayList<ArrayList<Integer>> geneClone1 = deepCopy(gene1);
        ArrayList<ArrayList<Integer>> geneClone2 = deepCopy(gene2);
        int []gc1Clone = graphColor1.clone();
        int []gc2Clone = graphColor2.clone();

        for (int i = 0; i < COLORNUM;) {
            // 贪婪选择颜色类
            // 使用贪婪策略从geneClone1和geneClone2中选择包含最多顶点的颜色类，并将这些顶点的颜色赋值给后代个体
            int idx1 = getMaxColorClassIndex(geneClone1);
            int idx2 = getMaxColorClassIndex(geneClone2);

            int maxsize1 = geneClone1.get(idx1).size();

            //历遍所有颜色为idx1的顶点
            for (int i1 = 0; i1 < maxsize1; i1++) {

                //将 geneClone1 中第 idx1 个元素（一个整数列表）中的第 i1 个顶点对应的颜色设置为 i
                int vertex = geneClone1.get(idx1).get(i1);
                graphColor[vertex] = i;

                // gene2获取到gene1中idx1颜色所对应的所有点，并将其删除
                geneClone2.get(gc2Clone[vertex]).remove((Integer) vertex);

                // 将geneClone1中idx1颜色对应的顶点的颜色设置为-1，表示该顶点已处理
                gc1Clone[vertex] = -1;

                // 将geneClone2中对应的顶点的颜色设置为-1，表示该顶点已处理
                gc2Clone[vertex] = -1;
            }

            // 清空geneClone1中idx1的颜色类,表示该颜色类的所有顶点已被处理
            geneClone1.get(idx1).clear();

            i++;
            if (i >= COLORNUM) break;

            // 将geneClone2中选择的颜色类传递到后代
            for (int i2 = 0; i2 < geneClone2.get(idx2).size(); i2++) {
                graphColor[geneClone2.get(idx2).get(i2)] = i;
                geneClone1.get(gc1Clone[geneClone2.get(idx2).get(i2)])
                        .remove((Integer) geneClone2.get(idx2).get(i2));
                gc1Clone[geneClone2.get(idx2).get(i2)] = -1;
                gc2Clone[geneClone2.get(idx2).get(i2)] = -1;
            }
            geneClone2.get(idx2).clear();

            i++;
            if (i >= COLORNUM) break;
        }

        for (int i = 0; i < gc1Clone.length; i++) {
            if (gc1Clone[i] != -1) {
                graphColor[i] = ra.nextInt(COLORNUM);
            }
        }

        return graphColor;
    }
}

