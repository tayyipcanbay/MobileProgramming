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
    private static FrameLayout addPhoto, addLabel;
    private static Context context;
    private static String visibilityState = "default";
    public static void init(Context context, FrameLayout addPhoto, FrameLayout addLabel) {
        NavUtil.context = context;
        NavUtil.addPhoto = addPhoto;
        NavUtil.addLabel = addLabel;
    }
    public static void handleNavigationItemSelected(MenuItem menuItem, String giren) {
        int itemId = menuItem.getItemId();

        if (itemId == getResourceId("nav_item1", "id")) {
            setVisibility(View.VISIBLE, View.INVISIBLE);
        } else if (itemId == getResourceId("nav_item2", "id")) {
            setVisibility(View.INVISIBLE, View.VISIBLE);
        } else if (itemId == getResourceId("nav_item3", "id")){
            Intent intent = new Intent(context, GalleryActivity.class);
            intent.putExtra("userEmail", giren);
            context.startActivity(intent);
        } else if(itemId == getResourceId("nav_item4", "id")) {
            Intent intent = new Intent(context, AboutActivity.class);
            intent.putExtra("userEmail", giren);
            context.startActivity(intent);
            setVisibility(View.INVISIBLE, View.INVISIBLE);
        } else if (itemId == getResourceId("nav_item5", "id")) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("userEmail", giren);
            context.startActivity(intent);
            setVisibility(View.INVISIBLE, View.INVISIBLE);
        }
    }
    private static int getResourceId(String name, String type) {
        Resources resources = context.getResources();
        return resources.getIdentifier(name, type, context.getPackageName());
    }
    private static void setVisibility(int addLabelVisibility, int addPhotoVisibility) {
        if (addLabel != null) {
            addLabel.setVisibility(addLabelVisibility);
        }
        if (addPhoto != null) {
            addPhoto.setVisibility(addPhotoVisibility);
        }
    }
}

