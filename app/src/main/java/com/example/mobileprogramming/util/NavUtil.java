package com.example.mobileprogramming.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.mobileprogramming.AboutActivity;
import com.example.mobileprogramming.GalleryActivity;
import com.example.mobileprogramming.MainActivity;

/**
 * Bu sınıf, NavigationView içindeki öğelerin seçilmesini ve bu seçimlere göre işlemler yapmayı sağlar.
 */
public class NavUtil {
    private static FrameLayout addphoto, addlabel;
    private static Context context;
    private static String visibilityState = "default";
    /**
     * Bu metod, NavUtil sınıfını başlatır. Bu metodun çağrılması, kullanılacak olan FrameLayout ve Context'in belirlenmesini sağlar.     *
     * @param context   Uygulamanın bağlamı (context) - Aktivite sınıfından elde edilebilir.
     * @param addphoto  Fotoğraf eklemek için kullanılacak FrameLayout.
     * @param addlabel  Etiket eklemek için kullanılacak FrameLayout.
     */
    public static void init(Context context, FrameLayout addphoto, FrameLayout addlabel) {
        NavUtil.context = context;
        NavUtil.addphoto = addphoto;
        NavUtil.addlabel = addlabel;
    }
    /**
     * Bu metod, NavigationView'de bir öğe seçildiğinde çağrılır ve seçilen öğeye göre işlemler yapar.
     * @param menuItem Seçilen menü öğesi.
     */
    public static void handleNavigationItemSelected(MenuItem menuItem, String giren) {
        int itemId = menuItem.getItemId();

        if (itemId == getResourceId("nav_item1", "id")) {
            // "Add Label" seçildiğinde yapılacak işlemler
            setVisibility(View.VISIBLE, View.INVISIBLE);
        } else if (itemId == getResourceId("nav_item2", "id")) {
            // "Add Photo" seçildiğinde yapılacak işlemler
            setVisibility(View.INVISIBLE, View.VISIBLE);
        } else if (itemId == getResourceId("nav_item3", "id")){
            Intent intent1 = new Intent(context, GalleryActivity.class);
            intent1.putExtra("userEmail", giren);
            context.startActivity(intent1);
        } else if(itemId == getResourceId("nav_item4", "id")) {
            Intent intent3 = new Intent(context, AboutActivity.class);
            intent3.putExtra("userEmail", giren);
            context.startActivity(intent3);
            setVisibility(View.INVISIBLE, View.INVISIBLE);
        } else if (itemId == getResourceId("nav_item5", "id")) {
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.putExtra("userEmail", giren);
            context.startActivity(intent2);
            // "Some Item" seçildiğinde yapılacak işlemler
            setVisibility(View.INVISIBLE, View.INVISIBLE);
        }
    }
    /**
     * Bu metod, adını ve tipini belirtilen bir öğenin kimliğini alır.
     * @param name Öğe adı.
     * @param type Öğe tipi (örneğin, "id").
     * @return Belirtilen öğenin kimliği.
     */
    private static int getResourceId(String name, String type) {
        Resources resources = context.getResources();
        return resources.getIdentifier(name, type, context.getPackageName());
    }
    /**
     * Bu metod, addlabel ve addphoto FrameLayout'larının görünürlüğünü ayarlar.
     * @param addLabelVisibility addlabel'ın görünürlüğü.
     * @param addPhotoVisibility addphoto'nun görünürlüğü.
     */
    private static void setVisibility(int addLabelVisibility, int addPhotoVisibility) {
        if (addlabel != null) {
            addlabel.setVisibility(addLabelVisibility);
        }
        if (addphoto != null) {
            addphoto.setVisibility(addPhotoVisibility);
        }
    }
}

