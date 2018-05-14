package app.shruthi.com.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    CallbackManager manager;
    LoginButton loginButton;
    TextView textViewName;
    TextView textViewEmail;
    AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createKeyHash();
        initControls();
        manageLogin();


        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (!isLoggedIn){
            LoginManager.getInstance().logOut();
        }


    }

    private void createKeyHash() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo("app.shruthi.com.login",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {

                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                Log.d("key hash", Base64.encodeToString((messageDigest.digest())
                        , Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void initControls() {
        manager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.button_login);
        textViewName = findViewById(R.id.tv_name);
        textViewEmail = findViewById(R.id.tv_email);
        accessToken = AccessToken.getCurrentAccessToken();
    }

    private void manageLogin() {
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        loginButton.registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d("response list", response.toString());

                                getUserData(object);

                            }
                        });
                Bundle bundle = new Bundle();
                bundle.putString("fields", "name,email");
                request.setParameters(bundle);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        /*if (accessToken != null) {
            textViewEmail.setText(accessToken.getUserId());
        }
        else {
            LoginManager.getInstance().logOut();
        }*/

    }

    private void getUserData(JSONObject object) {
        try {
                textViewName.setText(object.getString("name"));
                textViewEmail.setText(object.getString("email"));
//                logout();
                /*textViewName.setVisibility(View.GONE);
                textViewEmail.setVisibility(View.GONE)*/;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void logout() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        manager.onActivityResult(requestCode, resultCode, data);
    }
}
