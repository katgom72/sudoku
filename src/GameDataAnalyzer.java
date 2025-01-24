import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GameDataAnalyzer {
    private int time;        
    private int hints;      
    private int errors;      
    private int initialFilled;
    private String username; 

    
    private static final double WEIGHT_TIME = 0.4;
    private static final double WEIGHT_HINTS = 0.4;
    private static final double WEIGHT_ERRORS = 0.01;
    private static final double WEIGHT_FILLED = 0.19;

    private static final Set<Integer> validGameIds = new HashSet<>(Arrays.asList(5,6,7,8,16,22,23,30,34,37,41,44,45,46,47,48,50,51,58,59,61,63,65,66,68,69,70,71,72,75,76));
    private static final Set<Integer> validGameIdsToHard = new HashSet<>(Arrays.asList(7,59,66,72,75,76));



    public GameDataAnalyzer(int time, int hints, int errors, int initialFilled, String username) {
        this.time = time;
        this.hints = hints;
        this.errors = errors;
        this.initialFilled = initialFilled;
        this.username = username;
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
    public static double calculateAverageDToHard(String jsonFilePath) {
        List<Double> dValues = new ArrayList<>();

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray gameData = new JSONArray(jsonContent);

            List<JSONObject> filteredGames = gameData.toList().stream()
                    .map(obj -> new JSONObject((java.util.Map<?, ?>) obj))
                    .filter(game -> validGameIdsToHard.contains(game.getInt("data_game_id")))
                    .collect(Collectors.toList());

            if (!filteredGames.isEmpty()) {

                for (JSONObject game : filteredGames) {
                    int solveTime = game.getInt("solveTime");
                    int hintsUsed = game.getInt("hintCount");
                    int errorCount = game.getInt("errorCount");
                    int initialFilledCount = game.getInt("initialFilledCount");

                    GameDataAnalyzer analyzer = new GameDataAnalyzer(solveTime, hintsUsed, errorCount, initialFilledCount, "username");

                    double d = analyzer.calculateD(jsonFilePath, solveTime, hintsUsed, errorCount, initialFilledCount);
                    dValues.add(d);
                }

                double averageD = dValues.stream()
                                        .mapToDouble(Double::doubleValue)
                                        .average()
                                        .orElse(0.0);

                System.out.printf("Średnia z wartości d dla za trudne: %.8f%n", averageD);

                return averageD; 
            } else {
                return 0.0; 
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
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

                    GameDataAnalyzer analyzer = new GameDataAnalyzer(solveTime, hintsUsed, errorCount, initialFilledCount,"username");

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

                    GameDataAnalyzer analyzer = new GameDataAnalyzer(solveTime, hintsUsed, errorCount, initialFilledCount,"username");

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


    public int analyzeDForNextLevel(double d, int difficulty) {
        try {
            int difficultyStreak = getNextLevelForUser("game_data.json",username);
            double minD = calculateMinD("game_data.json");
            double maxD = calculateAverageDToHard("game_data.json");
            int new_difficulty=difficulty;
            if (difficultyStreak>1){
                return difficulty;
            }else{
                if (d<minD){
                    if(difficulty<4){
                        new_difficulty = difficulty+1;
                        return new_difficulty;
                    }else{
                        return difficulty;
                    }
                }
                if(d> minD && d<maxD){
                    new_difficulty= difficulty;
                    return new_difficulty;
                }
                if(d>maxD){
                    if(difficulty>1){
                        new_difficulty = difficulty-1;
                        return new_difficulty;
                    }else{
                        return difficulty;
                    }
                }    
            }
            return new_difficulty; 
        } catch (Exception e) {
            e.printStackTrace(); // Obsługa wyjątku - możesz dodać własną logikę
        }
        return difficulty;       
    }

    public int determineDifficultyStreak(double d,String username) {
        try {
            int difficultyStreak = getNextLevelForUser("game_data.json",username);
            double averageD = calculateAverageD("game_data.json");
            double minD = calculateMinD("game_data.json");
            double averageOfAverageDAndMinD = (minD+averageD)/2;
            if(difficultyStreak>1){
                return difficultyStreak-1;
            }else{
                if (d<averageOfAverageDAndMinD){
                    return 2;
                }
                if(d>averageOfAverageDAndMinD && d<averageD){
                    return 5;
                }
                if(d> averageD ){
                    return 2;
                }
            }
            return 1;     
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return 1;
           
    }

    public static int getNextLevelForUser(String jsonFilePath, String username) {
        try {
            // Odczyt zawartości pliku JSON
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray gameData = new JSONArray(jsonContent);

            JSONObject maxGameData = null;
            int maxGameId = Integer.MIN_VALUE;

            // Szukanie rekordu z maksymalnym `data_game_id` dla danego użytkownika
            for (int i = 0; i < gameData.length(); i++) {
                JSONObject record = gameData.getJSONObject(i);
                if (record.has("username") && record.getString("username").equals(username)) {
                    int currentGameId = record.optInt("data_game_id", Integer.MIN_VALUE); // Bezpieczne pobranie wartości
                    if (currentGameId > maxGameId) {
                        maxGameId = currentGameId;
                        maxGameData = record;
                    }
                }
            }

            // Jeśli brak rekordu dla danego użytkownika, zwracamy 1
            if (maxGameData == null) {
                return 1;
            }

            // Sprawdzenie, czy istnieje klucz `difficultyStreak`
            if (maxGameData.has("difficultyStreak")) {
                return maxGameData.getInt("difficultyStreak");
            } else {
                return 1;
            }
        } catch (IOException ex) {
            System.err.println("Błąd odczytu pliku JSON: " + ex.getMessage());
            return 1;
        } catch (JSONException ex) {
            System.err.println("Błąd przetwarzania danych JSON: " + ex.getMessage());
            return 1;
        } catch (Exception ex) {
            System.err.println("Nieoczekiwany błąd: " + ex.getMessage());
            return 1;
        }
    }

    

    public static void main(String[] args) {
        int solveTime = 386812; 
        int hintsUsed = 0;        
        int errorCount = 0;       
        int initialFilledCount = 46;
        String username = "username";

        String jsonFilePath = "game_data.json";

        GameDataAnalyzer analyzer = new GameDataAnalyzer(solveTime, hintsUsed, errorCount, initialFilledCount,username);

        System.out.println(analyzer.calculateD(jsonFilePath, solveTime, hintsUsed, errorCount, initialFilledCount));
        calculateAverageD(jsonFilePath);
        calculateMinD(jsonFilePath);
        calculateAverageDToHard(jsonFilePath);



    }
}
