package com.tjoeun.locationuse0710;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;

public class FusedActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ImageView onOffView;

    TextView latitudeView;
    TextView longitudeView;
    TextView accuracyView;
    TextView timestampView;

    //구글 API 사용을 위한 변수
    GoogleApiClient apiClient;
    //구글 API 중에서 위치 정보를 가져오기 위한 변수
    FusedLocationProviderClient providerClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fused);

        onOffView = (ImageView)findViewById(R.id.fonoffview);
        latitudeView = (TextView)findViewById(R.id.flatitude);
        longitudeView = (TextView)findViewById(R.id.flongitude);
        accuracyView = (TextView)findViewById(R.id.faccuracy);
        timestampView = (TextView)findViewById(R.id.ftime);

        //위치 정보 사용 권한 체크
        //위치 정보 사용 권한이 없다면 권한을 요청
        if(ContextCompat.checkSelfPermission(FusedActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    FusedActivity.this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }

        //Google API 사용 객체 만들기
        apiClient = new GoogleApiClient.Builder(
                FusedActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //FusedAPI를 사용할 수 있는 객체를 생성
        providerClient = LocationServices.getFusedLocationProviderClient(FusedActivity.this);
    }

    //문자열을 매개변수로 받아서 토스트로 출력해주는 메소드
    private void showToast(String message){
        Toast.makeText(
                FusedActivity.this, message,
                Toast.LENGTH_LONG).show();
    }

    //Location을 매개변수로 받아서 위치 정보를 출력해주는 메소드
    private void setLocationInfo(Location location){
        if(location != null){
            latitudeView.setText(location.getLatitude() + "");
            longitudeView.setText(location.getLongitude() + "");
            accuracyView.setText(location.getAccuracy() + "");
            //년도-월-일 시간:분:초 출력
            java.util.Date date =
                    new java.util.Date(location.getTime());
            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
            String timestamp = sdf.format(date);
            timestampView.setText(timestamp);
            //이미지 변경 -  drawable 디렉토리에 있는 이미지 파일을 출력
            onOffView.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(),R.drawable.ic_on, null
                        ));
        }else{
            showToast("위치정보를 수집할 권한이 없습니다.");
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        apiClient.connect();
    }

    //위치 정보를 가져올때 호출되는 메소드 재정의
    @Override
    public void onConnected(Bundle bundle){
        //위치 정보권한이 있는지 확인
        if(ContextCompat.checkSelfPermission(
                FusedActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED){
            providerClient.getLastLocation().addOnSuccessListener(FusedActivity.this,
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                         setLocationInfo(location);
                        }
                    });
            apiClient.disconnect();
        }
    }

    @Override
    //연결이 지연될 때 호출되는 메소드
    public void onConnectionSuspended(int i){
        showToast("연결이 지연되고 있습니다.");
    }

    @Override
    //위치 정보를 가져오는데 실패 했을 때 호출되는 메소드
    public void onConnectionFailed(ConnectionResult result){
        showToast("연결에 실패하였습니다.");
    }

}
