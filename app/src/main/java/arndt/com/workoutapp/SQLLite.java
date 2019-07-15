package arndt.com.workoutapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arndt.com.workoutapp.activites.objects.Pair;

public class SQLLite extends SQLiteAssetHelper {
    private static SQLLite instance;
    public static SQLLite getInstance(Context...context){
        if(instance == null)
            instance = new SQLLite(context[0]);
        return instance;
    }
    // please call get instance first or risk null pointer exception
    public static List<DataModel> getAllWorkouts() {
        return instance.allWorkouts;
    }


    private static final String DATABASE_NAME = "workoutapp.sqllite";
    private static final int DATABASE_VERSION = 1;

    private SQLLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // eager load workouts in background thread
        AsyncTask.execute(() -> _getAllWorkouts());
        AsyncTask.execute(() -> getAllCategories());
    }

    private List<DataModel> allWorkouts;
    private boolean loadingAllWorkouts = false, loadingCategories = false;

    public static List<Pair<String,String>> getCategories(String category) {
        return getInstance().categories.get(category);
    }

    public static List<DataModel> getTodaysWorkout() {
        return new ArrayList<>();
    }

    public void getAllCategories(){
        loadingCategories=true;
        for(String s : Arrays.asList("type","group_name"))
            parseCategory("exercise",String.format("select distinct %s from exercise;", s));
        for(String s : Arrays.asList("utility","mechanics","force","function","intensity"))
            parseCategory("classifications",String.format("select distinct %s from classifications;", s));
        for(String s : Arrays.asList("name"))
            parseCategory("muscles",String.format("select distinct %s from muscles;", s));
        loadingCategories=false;
    }

    private HashMap<String,List<Pair<String,String>>> categories = new HashMap<>();

    private void parseCategory(String category, String query){
        List<HashMap<String, String>> resultSet = query(query);
        for(HashMap<String, String> s : resultSet)
            for(Map.Entry<String, String> r : s.entrySet()) {
                List<Pair<String,String>> p = new ArrayList<>();
                if (categories.containsKey(category))
                    p = categories.get(category);
                else
                    categories.put(category, p);
                p.add(new Pair<>(r.getKey(), r.getValue()));
            }
    }

    public List<DataModel> _getAllWorkouts(){
        loadingAllWorkouts = true;
        List<HashMap<String, String>> resultSetEC = query(
                "select e.name as name,e.id as eid,type,group_name,videourl,gifurl,utility,mechanics,force,function,intensity,c.id as cid " +
                        "from exercise e, classifications c, exercise_classifications ec where e.id = ec.exercise_id and c.id = ec.classification_id;"
        );
        List<HashMap<String, String>> resultSetEI = query(
                "select e.name as name,e.id as eid,preparation,execution,easier,comments,i.id as iid " +
                        "from exercise e, instructions i, exercise_instructions ec where e.id = ec.exercise_id and i.id = ec.instructions_id;"
        );
        List<HashMap<String, String>> resultSetEM = query(
                "select e.name as name,e.id as eid,m.name as muscle_name,link,muscles_type,m.id as mid " +
                        "from exercise e, muscles m, exercise_muscles ec where e.id = ec.exercise_id and m.id = ec.muscle_Id;"
        );
        HashMap<String,ExerciseObj> results = new HashMap<>();
        for(HashMap<String, String> e : resultSetEC)
            if(results.containsKey(e.get("name")))
                results.get(e.get("name")).addProperties(e);
            else results.put(e.get("name"),new ExerciseObj(e.get("name"),e.get("eid")).addProperties(e));
        for(HashMap<String, String> e : resultSetEI)
            if(results.containsKey(e.get("name")))
                results.get(e.get("name")).addProperties(e);
            else results.put(e.get("name"),new ExerciseObj(e.get("name"),e.get("eid")).addProperties(e));
        for(HashMap<String, String> e : resultSetEM)
            if(results.containsKey(e.get("name")))
                results.get(e.get("name")).addProperties(e);
            else results.put(e.get("name"),new ExerciseObj(e.get("name"),e.get("eid")).addProperties(e));

        List<DataModel> dataModels = new ArrayList<>();
        for(ExerciseObj row : results.values()) {
            try {
                String n = row.name, v = row.properties.get("videourl").get(0);
                if(v.contains("http"))
                    continue;
                DataModel dm = new DataModel(row.properties, v, n,
                        row.properties.get("type").get(0) + " / " + row.properties.get("group_name").get(0));
                dataModels.add(dm);
            }catch (NullPointerException npe){
//                npe.printStackTrace(); /*Do nothing, no videourl or gorup name or type*/
            }
        }
        Collections.sort(dataModels,(a,b)->a.getTitle().compareTo(b.getTitle()));
        allWorkouts = dataModels;
        loadingAllWorkouts = false;
        return dataModels;
    }

    /**
     * Example
     *
     * @return
     */
    public Cursor getEmployees() {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {"0 _id", "FirstName", "LastName"};
        String sqlTables = "Employees";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null,
                null, null, null);

        c.moveToFirst();
        return c;

    }
    public List<HashMap<String,String>> query(String query) {
        return query(query,null);
    }
    public List<HashMap<String,String>> query(String query, String[] selectionArgs){
        SQLiteDatabase db = getReadableDatabase();
        Cursor e = db.rawQuery(query,selectionArgs);
        List<HashMap<String,String>> resultSet = new ArrayList<>();
        e.moveToFirst();
        while (!e.isLast()){
            HashMap<String,String> row = new HashMap<>();
            for (int i = 0; i < e.getColumnCount(); i++)
                row.put(e.getColumnName(i),e.getString(i));
            resultSet.add(row);
            e.moveToNext();
        }
        return resultSet;
    }


    // ******STANDARD LIBRARY USE BELOW



    //This should be same as which you used package section in your manifest
    private static String DB_PATH = "/data/data/arndt.com.workoutapp/databases/";


    /**
     * Connect to the test.db database
     * @return the Connection object
     */
    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:C://sqlite/db/test.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    /**
     * select all rows in the warehouses table
     */
    public void selectAll(){
        String sql = "SELECT id, name, capacity FROM warehouses";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("name") + "\t" +
                        rs.getDouble("capacity"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Get the warehouse whose capacity greater than a specified capacity
     * @param capacity
     */
    public void getCapacityGreaterThan(double capacity){
        String sql = "SELECT id, name, capacity "
                + "FROM warehouses WHERE capacity > ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){

            // set the value
            pstmt.setDouble(1,capacity);
            //
            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("name") + "\t" +
                        rs.getDouble("capacity"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
