package nl.maastrichtuniversity.dacs.ssa.g14;

public class Locations {
    public final static int HOSPITAL_LOCATION = 0;

    private final static int[][] adjacency_matrix = new int[][]{
            // Node 0 dist
            {
                0,1,1,1,1,1,1
            },
            // Node 1 dist
            {
               1, 0, 1, 2, 2, 2, 1
            },
            // Node 2
            {
                1,1,0,1,2,2,2
            },
            // Node 3
            {
                1,2,1,0,1,2,2
            },
            // Node 4
            {
                1,2,2,1,0,1,2
            },
            // Node 5
            {
                1,2,2,2,1,0,1
            },
            // Node 6
            {
                1,1,2,2,2,1,0
            }
    };

    private static int DURATION_PER_UNIT = 200;

    public static int getDistanceBetween(int i, int j){
        return adjacency_matrix[i][j];
    }

    public static double timeBetween(int i, int j){
        return getDistanceBetween(i,j) * DURATION_PER_UNIT;
    }
}
