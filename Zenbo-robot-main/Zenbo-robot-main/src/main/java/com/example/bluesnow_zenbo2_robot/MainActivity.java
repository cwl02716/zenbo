package com.example.bluesnow_zenbo2_robot;

import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.WheelLights;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotCommand;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.Utility;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

public class MainActivity extends RobotActivity {
    public static final int TYPE_CAPACITY_TOUCH = Utility.SensorType.CAPACITY_TOUCH;
    public MainActivity(RobotCallback robotCallback, RobotCallback.Listen robotListenCallback) {
        super(robotCallback, robotListenCallback);
    }

    private String[] FaceCandidateArray = {"INTERESTED", "DOUBTING", "PROUD", "DEFAULT", "HAPPY", "EXPECTING", "SHOCKED", "QUESTIONING", "IMPATIENT", "ACTIVE",
            "PLEASED", "HELPLESS", "SERIOUS", "WORRIED", "PRETENDING", "LAZY", "AWARE_RIGHT", "TIRED", "SHY", "INNOCENT",
            "SINGING", "AWARE_LEFT", "DEFAULT_STILL", "HIDEFACE"};
    private TextView ServerIP;
    private TextView Status, action;

    ServerSocket serverSocket;
    BufferedReader reader;
    JSONObject object;
    String tem_work = "";
    int pointer = 0;
    // sensor manager
    private SensorManager mSensorManager;

    // sensor
    private Sensor mSensorCapacityTouch;
    private TextView mTextView_capacity_touch_value0;
    private TextView mTextView_capacity_touch_value1;
    String serverIP;

    {
        try {
            serverSocket = new ServerSocket(7100);
            serverSocket.setReuseAddress(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                /*new Thread(new GetWifiIP()).start();
                while (serverIP ==null)
                {

                }
                ServerIP.setText("server IP :"+serverIP);*/
                // ServerSocket serverSocket;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ServerIP.append(getLocalIpAddress());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                new_work(serverSocket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Thread thread = new Thread(runnable);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewElement();
        this.robotAPI = new RobotAPI(getApplicationContext(), robotCallback);
        // ui
        //CAPACITY_TOUCH
        mTextView_capacity_touch_value0 = (TextView)findViewById(R.id.id_sensor_type_capacity_touch_value0_value);
        mTextView_capacity_touch_value1 = (TextView)findViewById(R.id.id_sensor_type_capacity_touch_value1_value);

        // sensor manager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // sensors
        mSensorCapacityTouch = mSensorManager.getDefaultSensor(TYPE_CAPACITY_TOUCH);
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(listenerCapacityTouch, mSensorCapacityTouch, SensorManager.SENSOR_DELAY_UI);
        if(robotListenCallback!= null)
            robotAPI.robot.registerListenCallback(robotListenCallback);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    //listener - TYPE_CAPACITY_TOUCH
    SensorEventListener listenerCapacityTouch = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mTextView_capacity_touch_value0.setText(String.valueOf(event.values[0]));
            mTextView_capacity_touch_value1.setText(String.valueOf(event.values[1]));
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    void new_work(ServerSocket serverSocket) {
        System.out.println("In the function");

        try {
            System.out.println("Server connected");
            Socket socket = serverSocket.accept();
            System.out.println("Server connected!!!");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Status.setText("Status : Connected");
                }
            });

            Thread work = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tem_work = "";
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        //action.setText("22");
                        while ((tem_work = reader.readLine()) != null) {
                            object = new JSONObject(tem_work);
                            if (object.getString("act").equals("face")) {
                                setface();
                                Log.d("Message", object.getString("act"));

                            } else if (object.getString("act").equals("stop")) {
                                stop();
                                Log.d("Message", object.getString("act"));

                            } else if (object.getString("act").equals("front")) {
                                front();
                                Log.d("Message", object.getString("act"));

                            } else if (object.getString("act").equals("back")) {
                                back();
                                Log.d("Message", object.getString("act"));

                            } else if (object.getString("act").equals("right")) {
                                right();
                                Log.d("Message", object.getString("act"));

                            } else if (object.getString("act").equals("left")) {
                                left();
                                Log.d("Message", object.getString("act"));

                            } else if (object.getString("act").equals("lookatuser")) {
                                lookatuser();
                                Log.d("Message", object.getString("lookatuser"));

                            }
                            else if(object.getString("act").equals("temp")){
                                getData("temp");
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            work.start();
        } catch (Exception e) {
            System.out.println("Server disconnected");
            e.printStackTrace();
        }


    }
    void getData(final String param){
        HttpURLConnection connection;
        try {
            URL url = new URL("http://140.115.158.250:3000/api/devices/SJdxWm4ui/data" +
                    "channels/DHTchannelID/datapoints?limit=1");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("deviceKey", "7751782cb87de3ce22285cb5689a6e53807537d70baf391165fb1dc644157a1d");
            connection.setDoInput(true);

            InputStream inputStream = connection.getInputStream();
            BufferedReader bR = new BufferedReader(  new InputStreamReader(inputStream));
            String line = "";

            StringBuilder responseStrBuilder = new StringBuilder();
            while((line =  bR.readLine()) != null){
                responseStrBuilder.append(line);
            }
            inputStream.close();

            JSONObject jsonPost = new JSONObject(responseStrBuilder.toString());

            // men-set data ke dalam tampilan
            JSONObject object = (JSONObject) jsonPost.getJSONArray("data").get(0);
            String str = object.getJSONObject("values").getString("value");

            if(param.contains("temp")){
                robotAPI.robot.setExpression(RobotFace.HAPPY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void stop() {
        robotAPI.motion.remoteControlBody(MotionControl.Direction.Body.STOP);
        //robotAPI.motion.remoteControlHead(MotionControl.Direction.Head.STOP);
        Log.d("work", "stop");
    }

    void left() {
        robotAPI.motion.remoteControlBody(MotionControl.Direction.Body.TURN_LEFT);
        Log.d("work", "left");
    }

    void lookatuser()
    {
        robotAPI.utility.lookAtUser(0);
        Log.d("work", "look");
    }
    void right()
    {
        robotAPI.motion.remoteControlBody(MotionControl.Direction.Body.TURN_RIGHT);
        Log.d("work","right");
    }

    void front(){
        robotAPI.motion.remoteControlBody(MotionControl.Direction.Body.FORWARD);
        Log.d("work","front");
    }

    void back(){
        robotAPI.motion.remoteControlBody(MotionControl.Direction.Body.BACKWARD);
        Log.d("work","back");
    }


    void setface(){
        if(pointer % 24 == 0){
            robotAPI.robot.setExpression(RobotFace.INTERESTED);

        }
        else if(pointer % 24 == 1){
            robotAPI.robot.setExpression(RobotFace.DOUBTING);
        }
        else if(pointer % 24 == 2){
            robotAPI.robot.setExpression(RobotFace.PROUD);
        }
        else if(pointer % 24 == 3){
            robotAPI.robot.setExpression(RobotFace.DEFAULT);
        }
        else if(pointer % 24 == 4){
            robotAPI.robot.setExpression(RobotFace.HAPPY);
        }
        else if(pointer % 24 == 5){
            robotAPI.robot.setExpression(RobotFace.EXPECTING);
        }
        else if(pointer % 24 == 6){
            robotAPI.robot.setExpression(RobotFace.SHOCKED);
        }
        else if(pointer % 24 == 7){
            robotAPI.robot.setExpression(RobotFace.QUESTIONING);
        }
        else if(pointer % 24 == 8){
            robotAPI.robot.setExpression(RobotFace.IMPATIENT);
        }
        else if(pointer % 24 == 9){
            robotAPI.robot.setExpression(RobotFace.ACTIVE);
        }
        else if(pointer % 24 == 10){
            robotAPI.robot.setExpression(RobotFace.PLEASED);
        }
        else if(pointer % 24 == 11){
            robotAPI.robot.setExpression(RobotFace.HELPLESS);
        }
        else if(pointer % 24 == 12){
            robotAPI.robot.setExpression(RobotFace.SERIOUS);
        }
        else if(pointer % 24 == 13){
            robotAPI.robot.setExpression(RobotFace.WORRIED);
        }
        else if(pointer % 24 == 14){
            robotAPI.robot.setExpression(RobotFace.PRETENDING);
        }
        else if(pointer % 24 == 15){
            robotAPI.robot.setExpression(RobotFace.LAZY);
        }
        else if(pointer % 24 == 16){
            robotAPI.robot.setExpression(RobotFace.AWARE_RIGHT);
        }
        else if(pointer % 24 == 17){
            robotAPI.robot.setExpression(RobotFace.TIRED);
        }
        else if(pointer % 24 == 18){
            robotAPI.robot.setExpression(RobotFace.SHY);
        }
        else if(pointer % 24 == 19){
            robotAPI.robot.setExpression(RobotFace.INNOCENT);
        }
        else if(pointer % 24 == 20){
            robotAPI.robot.setExpression(RobotFace.SINGING);
        }
        else if(pointer % 24 == 21){
            robotAPI.robot.setExpression(RobotFace.AWARE_LEFT);
        }
        else if(pointer % 24 == 22){
            robotAPI.robot.setExpression(RobotFace.DEFAULT_STILL);
        }
        else if(pointer % 24 == 23){
            robotAPI.robot.setExpression(RobotFace.HIDEFACE);
        }
        pointer += 1;
        Log.d("work","face");
    }
    /*
    class GetWifiIP implements Runnable {

        @Override
        public void run()
        {
            WifiManager wifi_service = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifi_service.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            serverIP = String.format("%d.%d.%d.%d",(ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        }
    }*/


    public static String getLocalIpAddress(){
        try{
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ){
                NetworkInterface intf = en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()){
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }catch(SocketException ex){
            Log.e("WifiPreference Ip Address",ex.toString());
        }
        return null;
    }

    void initViewElement(){
        ServerIP = (TextView) findViewById(R.id.ServerIP);
        Status = (TextView) findViewById(R.id.Status);
        action =(TextView) findViewById(R.id.Action);
    }
    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {

        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);

            Log.d("RobotDevSample", "onResult:"
                    + RobotCommand.getRobotCommand(cmd).name()
                    + ", serial:" + serial + ", err_code:" + err_code
                    + ", result:" + result.getString("RESULT"));
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }

        @Override
        public void initComplete() {
            super.initComplete();

        }
    };


    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {

        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    public MainActivity(){
        super(robotCallback, robotListenCallback);
    }
}