import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Random;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
/**
 * #INSTRUCTIONS:
 * 1. run in terminal: javac *.java
 * 2. run in terminal: java main increasingRule30CA_16by16_22.txt
 * 3. run in terminal: java main pureRule30CA_16by16_63.txt
 * 
 * Naive brute force takes a little over a minute on my machine. It might take more htan that on yours.
 * Other test files are included by their size for the users convenience in other folders.
 * 
 * @author Jake McKenzie
 * @version August 12, 2018
 * 
 * Problem Statement: There are n trading posts numbered from n, as you travel
 * down stream. At any trading post i, you can rent a canoe to be returned at 
 * any of the downstream trading posts j > i. You are given a cost array R(i,j)
 * given the cost of the rentals for 1 <= i <= j <= n. We will have to assume that
 * R(i,j) = 0 and R(i,j) = infinity if i > j. For example, with n = 4, the cost array
 * may look as follows:
 * 
 *          0    2   3   7
 *          -    0   2   4
 *          -    -   0   2
 *          -    -   -   0
 * 
 * The problem is to find a solution that computes the cheapest sequence of rentals 
 * taking you from post 1 all the way down to post n. In this example, the cheapest 
 * sequence is to rent from post 1 to post 3 (cost 3), then from post 3 to post 4 
 * (cost 2), with a total cost of 5 (less than the direct rental from post 1 to post 
 * 7, which would cost 7).
 * 
 * As I was watching Eric Demaine from MIT to review for my course in Algorithms he
 * stated that any minimizing and maximizing over a sequence in infact a graph
 * problem. As I dug deeper I found he continued in this vein saying "it turns out
 * that most dynamic programs can be converted to solving single-source shortest paths
 * typically in a DAG - not all but a lot of them" ~ Eric Demaine
 * 
 * https://youtu.be/NzgFUwOaoIw?t=15m20s
 * 
 * This is what I set out to do and I accomplished. 
 */
public class main {
    
    public static void main(String[] args) throws IOException{

        /**
         * @param testGiven this was the array given to us for the problem.
         */
        final int[][] testGiven  = { {0, 2, 3, 7},
                                     {0, 0, 2, 4},
                                     {0, 0, 0, 2},
                                     {0, 0, 0, 0}};
        /**
         * So I generated many random matrices for testing in mathematica. I decided to generate them there because the tools
         * for generating random numbers is much more mature in mathematica and I could easily generate better unpredictability
         * using their tools.
         * 
         * Mathematica uses Rule30CA which passes the big crush suite, which is a very high bar statistical test for 
         * randomness. The documentation I used in researching this is below.
         * 
         * https://mathematica.stackexchange.com/questions/92666/how-to-zero-or-replace-the-diagonal-of-a-square-matrix
         * https://mathematica.stackexchange.com/questions/27408/have-the-random-functions-changed
         * https://link.springer.com/chapter/10.1007%2F3-540-46416-6_17
         * 
         * The first set of test files was produced with the following definition:
         * 
         * f[n_] = UpperTriangularize[Table[RandomInteger[{1, n}], {i, 1, n}, {j, 1, n}], 1]
         * and
         * Table[Export["pureRule30CA" <> ToString[i] <> ".txt", Grid[f[i]], "Table"], {i, 4, 10}]
         * Table[Export["pureRule30CA" <> ToString[i] <> ".txt", Grid[f[i]], "Table"], {i, {25, 50, 100, 200, 400, 800}}]
         * 
         * The second set of test files was produced with the following definition:
         * 
         * g[n_] = UpperTriangularize[Table[RandomInteger[{1, n}] + RandomInteger[{1, i}], {i, 1, n}, {j, 1, n}], 1]
         * and
         * Table[Export["increasingRule30CA" <> ToString[i] <> ".txt", Grid[g[i]], "Table"], {i, 4, 10}]
         * Table[Export["increasingRule30CA" <> ToString[i] <> ".txt", Grid[g[i]], "Table"], {i, {25, 50, 100, 200, 400, 800}}]
         * 
         * I hope it is clear to the reader what is going on with these definitions. The first set is pure random numbers in the range of 1 to n
         * where n is the row and column size of the matrix I want to produce. The choice of a good random number range was difficult. If more time
         * were given I think 1 to n^2 would be a better range. The second set is the definition prior plus a random number that range increases with
         * the row length of the entry it is in. 
         * 
         * @param testCommandLine the command line text file asked of us that reads in a random file that is tab delineated.
         * I didn't like the use of NA so I made sure that it could read in a file of zeros instead. I assumed this was okay
         * to do because you told us we didn't have to worry about even reading in from a file.
         * 
         * https://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/
         */
        final int[][] testCommandLine = Files.lines(FileSystems.getDefault().getPath(args[0]))
                                    .map((l)->l.trim().split("\\s+"))
                                    .map((sa)->Stream.of(sa).mapToInt(Integer::parseInt).toArray())
                                    .toArray(int[][]::new);
        
        System.out.println(); 
        for (int i = 0; i < testCommandLine[0].length; i++) System.out.print("-----\t");
        System.out.println(); 
        for (int i = 0; i < testCommandLine[0].length; i++) System.out.print("| "+(i)+" |\t");
        System.out.println(); 
        for (int i = 0; i < testCommandLine[0].length; i++) System.out.print("-----\t");
        System.out.println(); 
        for (int i = 0; i < testCommandLine[0].length; i++){
            for (int j = 0; j < testCommandLine.length; j++) {
                System.out.print(testCommandLine[i][j]+"\t");
            }
            System.out.print("\n");
        }
        
        
        Long startTime = System.currentTimeMillis();
        Graph g = new Graph(testCommandLine.length);
        g.drawGraph(testCommandLine);
        Long runTime = System.currentTimeMillis() - startTime;
        System.out.println("Preprocessing into Graph Runtime: " + runTime + " ns");
        
        System.out.println("Naive Brute Force Approach: ");
        startTime = System.currentTimeMillis();
        Dipath brute = g.minimumNaiveBruteForce();
        runTime = System.currentTimeMillis() - startTime;
        System.out.println("Minimum path: "+brute.toString()+"\nCost: "+brute.cost);
        System.out.println("Runtime: " + runTime + " ms");

        System.out.println();

        startTime = System.nanoTime();
        Dipath BFS = g.BreadthFirstSearch();
        runTime = System.nanoTime() - startTime;
        System.out.println("BFS using Decrease and Conquer: ");
        System.out.println("Minimum path: "+BFS.toString()+"\nCost: "+ BFS.cost);
        System.out.println("Runtime: " + runTime + " ns");

        System.out.println();

        startTime = System.nanoTime();
        Dipath Dijksta = g.Dijkstra();
        runTime = System.nanoTime() - startTime;
        System.out.println("Dijkstra using Dynamic Programming: ");
        System.out.println("Minimum path: "+ Dijksta.toString()+"\nCost: "+ Dijksta.cost);
        System.out.println("Runtime: " + runTime + " ns");
        
    }

}