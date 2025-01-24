import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameDataAnalyzer {
    private int time;        
    private int hints;      
    private int errors;      
    private int initialFilled; 

    
    private static final double WEIGHT_TIME = 0.4;
    private static final double WEIGHT_HINTS = 0.4;
    private static final double WEIGHT_ERRORS = 0.01;
    private static final double WEIGHT_FILLED = 0.19;

    private static final Set<Integer> validGameIds = new HashSet<>(Arrays.asList(5,6,7,8,10,12,16,22,23,30,34,37,41,44,45,46,47,48,50,51,58,59,61,63,65,66,68,69,70,71,72));


    public GameDataAnalyzer(int time, int hints, int errors, int initialFilled) {
        this.time = time;
        this.hints = hints;
        this.errors = errors;
        this.initialFilled = initialFilled;
    }

    public enum InitialFilledRange {
        RANGE_46_36(46, 36),
        RANGE_35_32(35, 32),
        RANGE_31_28(31, 28),
        RANGE_27_17(27, 17);

        private final int max;
        private final int min;

        InitialFilledRange(int max, int min) {
            this.max = max;
            this.min = min;
        }

        public static InitialFilledRange getRange(int value) {
            for (InitialFilledRange range : values()) {
                if (value <= range.max && value >= range.min) {
                    return range;
                }
            }
            return null;  
        }
    }
    public static double calculateAverageD(String jsonFilePath) {
        List<Double> dValues = new ArrayList<>();

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray gameData = new JSONArray(jsonContent);

            List<JSONObject> filteredGames = gameData.toList().stream()
                    .map(obj -> new JSONObject((java.util.Map<?, ?>) obj))
                    .filter(game -> validGameIds.contains(game.getInt("data_game_id")))
                    .collect(Collectors.toList());

            if (!filteredGames.isEmpty()) {

                for (JSONObject game : filteredGames) {
                    int solveTime = game.getInt("solveTime");
                    int hintsUsed = game.getInt("hintCount");
                    int errorCount = game.getInt("errorCount");
                    int initialFilledCount = game.getInt("initialFilledCount");

                    GameDataAnalyzer analyzer = new GameDataAnalyzer(solveTime, hintsUsed, errorCount, initialFilledCount);

                    double d = analyzer.calculateD(jsonFilePath, solveTime, hintsUsed, errorCount, initialFilledCount);

                    dValues.add(d);
                }

                double averageD = dValues.stream()
                                        .mapToDouble(Double::doubleValue)
                                        .average()
                                        .orElse(0.0);

                System.out.printf("Średnia z wartości d: %.8f%n", averageD);

                return averageD; 
            } else {
                System.out.println("Brak gier z podanymi identyfikatorami.");
                return 0.0; 
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }
    public static double calculateMinD(String jsonFilePath) {
        List<Double> dValues = new ArrayList<>(); 

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray gameData = new JSONArray(jsonContent);

            List<JSONObject> filteredGames = gameData.toList().stream()
                    .map(obj -> new JSONObject((java.util.Map<?, ?>) obj))
                    .filter(game -> validGameIds.contains(game.getInt("data_game_id")))
                    .collect(Collectors.toList());

            if (!filteredGames.isEmpty()) {

                for (JSONObject game : filteredGames) {
                    int solveTime = game.getInt("solveTime");
                    int hintsUsed = game.getInt("hintCount");
                    int errorCount = game.getInt("errorCount");
                    int initialFilledCount = game.getInt("initialFilledCount");

                    GameDataAnalyzer analyzer = new GameDataAnalyzer(solveTime, hintsUsed, errorCount, initialFilledCount);

                    double d = analyzer.calculateD(jsonFilePath, solveTime, hintsUsed, errorCount, initialFilledCount);
                    

                    dValues.add(d);
                }

                double averageD = dValues.stream()
                                        .mapToDouble(Double::doubleValue)
                                        .min()
                                        .orElse(0.0);

                System.out.printf("Minimalna wartość d: %.8f%n", averageD);

                return averageD;  
            } else {
                System.out.println("Brak gier z podanymi identyfikatorami.");
                return 0.0; 
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0; 
        }
    }

    public double calculateD(String jsonFilePath, int t, int h, int e, int i_f) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray gameData = new JSONArray(jsonContent);

            List<JSONObject> games = gameData.toList().stream()
                    .map(obj -> new JSONObject((java.util.Map<?, ?>) obj))
                    .collect(Collectors.toList());

            InitialFilledRange currentRange = InitialFilledRange.getRange(i_f);

            if (currentRange == null) {
                System.out.println("Initial filled count is out of defined ranges.");
                return 0.0;
            }

            List<JSONObject> filteredGames = games.stream()
                    .filter(game -> {
                        int gameInitialFilled = game.getInt("initialFilledCount");
                        return gameInitialFilled <= currentRange.max && gameInitialFilled >= currentRange.min;
                    })
                    .collect(Collectors.toList());

            if (!filteredGames.isEmpty()) {
                int minSolveTime = filteredGames.stream()
                        .mapToInt(game -> game.getInt("solveTime"))
                        .min()
                        .orElse(Integer.MAX_VALUE);

                int maxSolveTime = filteredGames.stream()
                        .mapToInt(game -> game.getInt("solveTime"))
                        .max()
                        .orElse(Integer.MIN_VALUE);

                minSolveTime = Math.min(minSolveTime, t);
                maxSolveTime = Math.max(maxSolveTime, t);

                double timeNorm = (maxSolveTime > minSolveTime)
                        ? (double) (t - minSolveTime) / (maxSolveTime - minSolveTime)
                        : 0.0;

                double hintsNorm = (double) h / 3;

                int maxErrorCount = filteredGames.stream()
                        .mapToInt(game -> game.getInt("errorCount"))
                        .max()
                        .orElse(0);

                maxErrorCount = Math.max(maxErrorCount, e);

                double errorsNorm = (maxErrorCount > 0) ? (double) e / maxErrorCount : 0.0;

                double initialFilledNorm = 1.0 - ((double) (i_f - 17) / (46 - 17));

                return 100 * ((WEIGHT_TIME * timeNorm) +
                            (WEIGHT_HINTS * hintsNorm) +
                            (WEIGHT_ERRORS * errorsNorm) +
                            (WEIGHT_FILLED * initialFilledNorm));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0.0; 
    }




    public static void main(String[] args) {
        int solveTime = 386812; 
        int hintsUsed = 0;        
        int errorCount = 0;       
        int initialFilledCount = 32; 

        String jsonFilePath = "game_data.json";

        GameDataAnalyzer analyzer = new GameDataAnalyzer(solveTime, hintsUsed, errorCount, initialFilledCount);

        System.out.println(analyzer.calculateD(jsonFilePath, solveTime, hintsUsed, errorCount, initialFilledCount));
        calculateAverageD(jsonFilePath);
        calculateMinD(jsonFilePath);


    }
}
