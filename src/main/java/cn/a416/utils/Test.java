package cn.a416.utils;



import java.util.ArrayList;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        GPXUtils gpxUtils = new GPXUtils();

        // 使用提供的数据初始化 gene1 和 gene2
        ArrayList<ArrayList<Integer>> gene1 = new ArrayList<>();
        gene1.add(new ArrayList<>(Arrays.asList(0, 1, 3)));
        gene1.add(new ArrayList<>(Arrays.asList(2, 4)));
        gene1.add(new ArrayList<>(Arrays.asList(5)));

        ArrayList<ArrayList<Integer>> gene2 = new ArrayList<>();
        gene2.add(new ArrayList<>(Arrays.asList(0, 2)));
        gene2.add(new ArrayList<>(Arrays.asList(1, 4, 5)));
        gene2.add(new ArrayList<>(Arrays.asList(3)));

        int[] graphColor1 = {0, 0, 1, 0, 1, 2};
        int[] graphColor2 = {0, 1, 2, 1, 0, 2};

        // 打印GPX前的解集
        System.out.println("GPX前的解集:");
        printGraphColor(graphColor1);
        printGraphColor(graphColor2);

        // 调用GPX方法
        int[] result = gpxUtils.GPX(gene1, gene2, graphColor1, graphColor2);

        // 打印GPX后的解集
        System.out.println("GPX后的解集:");
        printGraphColor(result);
    }

    public static void printGraphColor(int[] graphColor) {
        for (int i = 0; i < graphColor.length; i++) {
            System.out.print(graphColor[i] + " ");
        }
        System.out.println();
    }
}
