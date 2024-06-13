# 图染色算法-HEAD' + MACol

#### 项目说明： 使用java编写的图染色问题算法
#### 项目环境： jdk8
#### 项目参数： 自行调整相关算法类中static变量

#### 启动说明： 原始图文件路径为/resource/DSJC500.5.col,如有需要可自行替换。在/src目录下的三个类可以直接启动main函数

## 问题简介：
   图着色问题（Graph Coloring Problem, GCP） 又称着色问题，是最著名的NP-完全问题之一。道路着色问题（Road Coloring Problem）是图论中最著名的猜想之一。
图的m-着色判定问题——给定无向连通图G和m种不同的颜色。用这些颜色为图G的各顶点着色，每个顶点着一种颜色，是否有一种着色法使G中任意相邻的2个顶点着不同颜色?
图的m-着色优化问题——若一个图最少需要m种颜色才能使图中任意相邻的2个顶点着不同颜色，则称这个数m为该图的色数。求一个图的最小色数m的问题称为m-着色优化问题。


HEAD算法：
该算法的基本思想如下：
生成两个初始解，对这两个解进行正反两次GPX操作。GPX交叉之后生成的解我们用来进行禁忌搜索，将本轮最优的解存进精英解中，再将精英解与最优解进行比较，确定最优解，之后在满足某些情况的条件下，将解1替换成上一轮的精英解，执行这个过程直到满足退出条件（轮数达到或者冲突为0）。


# 参考文献：
[1] Hertz, A., de Werra, D.: Using Tabu search techniques for graph coloring.Computing 39(4), 345–351(1987)
[2] Zhipeng Lü , Jin-Kao Hao: A memetic algorithm for graph coloring 
European Journal of Operational Research 203 (2010) 241–250
[3] Laurent Moalic , Alexandre Gondran: Variations on memetic algorithms for graph coloring problems J Heuristics (2018) 24:1–24
