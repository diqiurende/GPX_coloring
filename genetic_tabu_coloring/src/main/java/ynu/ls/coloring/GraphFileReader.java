package ynu.ls.coloring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class GraphFileReader {
    public static int SIZE = 500;

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
                    graph[col - 1][row - 1] = 1;
                    graph[row - 1][col - 1] = 1;
                    i++;
                }
            }
            System.out.println("i:" + i);
            return graph;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将int[500][500]变为邻接矩阵
     * @param graph int[500][500]
     * @return List<int[]>
     */
    public static List<int[]> initGraphList(int[][] graph) {
        List<int[]> adjList = new ArrayList<>();
        for (int i = 0; i < graph.length; i++) {
            int size = 0;
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
}

