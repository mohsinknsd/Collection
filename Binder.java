import android.support.v7.app.AppCompatActivity;
import java.lang.reflect.Field;

import in.appname.R;

/**
 * An annotation to bind any view at runtime using R file id.
 * @author Mohsin Khan
 * @date 7/28/2016
 */
public class Binder {
    @SuppressWarnings("TryWithIdenticalCatches")
    public static void bind(AppCompatActivity activity) {
        for(Field field : activity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Bind annotation = field.getAnnotation(Bind.class);
            if (annotation != null) {
                try {
                    Field id = R.id.class.getDeclaredField(field.getName());
                    field.set(activity, activity.findViewById(id.getInt(id)));
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
