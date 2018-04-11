package ru.ya.rrmstu.mywallet.utils;

import android.app.Application;

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        IconUtils.fillIcons(this); // один раз при загрузке приложения загружаем иконки
    }

}
