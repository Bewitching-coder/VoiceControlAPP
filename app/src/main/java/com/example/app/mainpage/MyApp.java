package com.example.app.mainpage;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;  // 记得导入这个包

public class MyApp extends Application {
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {
                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                    // 应用程序从后台回到前台
                    // 这里可以考虑是否也要重置UI
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity instanceof FragmentActivity) { // 检查是否为FragmentActivity实例
                    BottomFragment bottomFragment = (BottomFragment) ((FragmentActivity) activity).getSupportFragmentManager().findFragmentByTag("YourFragmentTag");
                    if (bottomFragment != null && bottomFragment.isAdded()) {
                        bottomFragment.resetUIFromExternal();
                    }
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                    // 应用程序进入到了后台
                    if (activity instanceof FragmentActivity) { // 检查是否为FragmentActivity实例
                        BottomFragment bottomFragment = (BottomFragment) ((FragmentActivity) activity).getSupportFragmentManager().findFragmentByTag("YourFragmentTag");
                        if (bottomFragment != null && bottomFragment.isAdded()) {
                            bottomFragment.resetUIFromExternal();
                        }
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });
    }
}
