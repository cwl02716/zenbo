package com.example.bluesnow_zenbo2_robot;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;


public class RobotActivity extends Activity{
    public RobotAPI robotAPI;
    RobotCallback robotCallback;
    RobotCallback.Listen robotListenCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.robotAPI = new RobotAPI(getApplicationContext(), robotCallback);
    }

    public RobotActivity (RobotCallback robotCallback, RobotCallback.Listen robotListenCallback) {
        this.robotCallback = robotCallback;
        this.robotListenCallback = robotListenCallback;
    }

    @Override
    protected void onPause() {
        super.onPause();
        robotAPI.robot.unregisterListenCallback();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        robotAPI.release();
    }
}
