package cn.a416.utils;

import javax.swing.plaf.ColorUIResource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * author:宋明远
 */
public class GPXUtils {

    public static int SIZE = 500;
    public static int COLORNUM = 48;

    public static Random ra =new Random();

    public int[] GPX(ArrayList<ArrayList<Integer>> gene1, ArrayList<ArrayList<Integer>> gene2, int[] graphColor1, int[] graphColor2){

        int idx1;
        int idx2;
        int []graphColor = new int[SIZE];

        //对gene1进行深拷贝
        ArrayList<ArrayList<Integer>> geneClone1 = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < gene1.size(); i++) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i1 = 0; i1 < gene1.get(i).size(); i1++) {
                list.add(gene1.get(i).get(i1));
            }
            geneClone1.add(list);
        }

        ArrayList<ArrayList<Integer>> geneClone2 = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < gene2.size(); i++) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i1 = 0; i1 < gene2.get(i).size(); i1++) {
                list.add(gene2.get(i).get(i1));
            }
            geneClone2.add(list);
        }

        int []gc1Clone = graphColor1.clone();
        int []gc2Clone = graphColor2.clone();


        for (int i = 0; i < COLORNUM;) {
            idx1 = 0;
            idx2 = 0;
            int maxsize1 = 0;
            int maxsize2 = 0;

            for (int i1 = 0; i1 < geneClone1.size(); i1++) {//找到geneClone1里包含最多的顶点颜色 idx1
                if (geneClone1.get(i1).size() > maxsize1){
                    idx1 = i1;
                    maxsize1 = geneClone1.get(i1).size();
                }
            }
            for (int i1 = 0; i1 < geneClone1.get(idx1).size(); i1++) {//历遍所有颜色为idex的顶点
                graphColor[geneClone1.get(idx1).get(i1)] = i; //将 geneClone1 中第 idx1 个元素（一个整数列表）中的第 i1 个顶点对应的颜色设置为 i
                // gene2获取到gene1中idx1颜色所对应的所有点，并将其删除(置为-1)。
                geneClone2.get(gc2Clone[geneClone1.get(idx1).get(i1)])
                        .remove((Integer) geneClone1.get(idx1).get(i1));
                gc1Clone[geneClone1.get(idx1).get(i1)] = -1;
                gc2Clone[geneClone1.get(idx1).get(i1)] = -1;
            }
            geneClone1.get(idx1).clear();

            i++;
            if (i >= COLORNUM) break;

            for (int i2 = 0; i2 < geneClone2.size(); i2++) {
                if (geneClone2.get(i2).size() > maxsize2){
                    idx2 = i2;
                    maxsize2 = geneClone2.get(i2).size();
                }
            }

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
