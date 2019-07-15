package arndt.com.workoutapp.activites.workoutlist;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import arndt.com.workoutapp.R;
import arndt.com.workoutapp.activites.MainActivity;
import arndt.com.workoutapp.activites.workoutscheduler.WorkoutSchedulerActivity;

public class MenuCreator {

    public static Drawer getDrawer(AppCompatActivity context, String title){
        // Handle Toolbar
        androidx.appcompat.widget.Toolbar toolbar = context.findViewById(R.id.toolbar);
        context.setSupportActionBar(toolbar);
        context.getSupportActionBar().setTitle(title);
        Drawer result = new DrawerBuilder()
                .withActivity(context)
                .withToolbar(toolbar)
                .inflateMenu(R.menu.example_menu)
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if(view==null) return false;
                    if (drawerItem instanceof Nameable) {
                        Class<?> clz = MainActivity.class;
                        switch (view.getId()) {
                            case R.id.menu_1:
                                break;
                            case R.id.menu_2:
                                clz = WorkoutListActivity.class;
                                break;
                            case R.id.menu_3:
                                clz = WorkoutSchedulerActivity.class;
                                break;
                        }
                        if (clz.getName().equals(String.format("%s.%s", context.getPackageName(), context.getLocalClassName())))
                            return false;

//                            Toast.makeText(MainActivity.this, ((Nameable) drawerItem).getName().getText(MainActivity.this), Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(context, clz);
//                            myIntent.putExtra("key", value); //Optional parameters
                        context.startActivity(myIntent);
                    }
                    return false;
                }).build();
        return result;
    }
}
