package com.tjoeun.locationuse0710;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    ImageView onOffView;
    TextView allProviders, enableProvider, provider;
    TextView latituteView, longitudeView, accuracyView, timestampView;
    //위치정보 파악을 위한 변수
    LocationManager manager;
    //사용가능 공급자와 정밀도를 저장하기 위한 변수
    List<String> enableProviders;
    float bestAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allProviders = (TextView) findViewById(R.id.allproviders);
        enableProvider = (TextView) findViewById(R.id.enableprovider);
        provider = (TextView) findViewById(R.id.provider);

        latituteView = (TextView) findViewById(R.id.latitude);
        longitudeView = (TextView) findViewById(R.id.longitude);
        accuracyView = (TextView) findViewById(R.id.accuracy);
        timestampView = (TextView) findViewById(R.id.time);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //동적 권한 요청 - FINE_LOCATION
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            //권한요청하기
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            //위치 정보 제공자를 찾아오는 메소드
            getProviders();
            //위치 정보가 변경될 때 호출될 리스너를 설정하는 메소드
            getLocation();
        }
    }
    //문자열을 매개변수로 받아서 토스트로 출력해주는 메소드
    private void showToast(String message){
        Toast toast = Toast.makeText(
                MainActivity.this,
                message, Toast.LENGTH_LONG
        );
        toast.show();
    }
    //첫번째 매개변수는 권한 요청을 할 때 설정한 번호
    //두번째 매개변수는 요청한 권한
    //세번째 매개변수는 사용자가 권한에 대해서 응답한것
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String [] permissions,
            int [] grantResults){
        //상위 클래스의 메소드 호출
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults
        );
        if(requestCode == 100 && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getProviders();
                getLocation();
            }else {
                showToast("권한을 사용할 수 없습니다.");
            }
        }
    }

    //위치정보 제공자를 출력해주는 메소드
    private void getProviders(){
        String result = "위치정보 공급자:";
        //위치 정보 공급자 전체 가져오기
        List<String> providers = manager.getAllProviders();
        for(String provider : providers){
            result += provider + ",";
        }
        allProviders.setText(result);

        result = "사용가능 공급자:";
        enableProviders = manager.getProviders(true);
        for(String provider : enableProviders){
            result += provider + ",";
        }
        enableProvider.setText(result);
    }

    //공급자와 Location을 매개변수로 받아서 위치 정보를 출력해주는 메소드
    private void setLocationInfo(
            String provider, Location location) {
        if (location != null) {
            enableProvider.setText(provider);
            latituteView.setText(location.getLatitude() + "");
            longitudeView.setText(location.getLongitude() + "");
            accuracyView.setText(location.getAccuracy() + "");
            //timestampView.setText(location.getTime() + "");

            java.sql.Date date = new java.sql.Date(location.getTime());
            timestampView.setText(date.toString());
        } else {
            showToast("위치 정보 파악이 되지 않습니다.");
        }
    }

    public void getLocation(){
        //사용 가능 공급자를 순회하면서 위치 정보를 읽어서 출력
        for(String provider : enableProviders){
            Location location = null;
            if (ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                location = manager.getLastKnownLocation(provider);
            }else{
                showToast("위치 정보 파악이 되지 않습니다.");
            }
            setLocationInfo(provider, location);
        }
    }
}
