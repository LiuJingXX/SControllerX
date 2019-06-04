package com.decawave.argomanager.scontroller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.constant.Constant;
import com.decawave.argomanager.scontroller.constant.UrlConstant;
import com.decawave.argomanager.scontroller.constant.UserConstant;
import com.decawave.argomanager.scontroller.http.HttpUtil;
import com.decawave.argomanager.scontroller.util.Global;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "zhangsan@zs:zhangsan", "fdse:fdse"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    Button mEmailSignInButton;
    ProgressDialog progressDialog;
//    private View mProgressView;
//    private View mLoginFormView;

    //判断是否有登录信息，实现自动登录
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private String email, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
//        mLoginFormView = findViewById(R.id.login_form);
//        mProgressView = findViewById(R.id.login_progress);


        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");

        preferences = getSharedPreferences(Constant.PREFERENCES_USER_INFO, Activity.MODE_PRIVATE);
        editor = preferences.edit();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //判断是否有登录信息，实现自动登录
        if (judgeLogin()) {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
            login();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //调用mqtt
//        EventBus.getDefault().register(this);
        progressDialog.dismiss();
    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void getMqttMessage3(MQTTMessage mqttMessage){
//        Log.i(MQTTService.TAG,"Login结束收到消息:"+mqttMessage.getMessage());
//        Toast.makeText(this,"Login结束收到消息:"+mqttMessage.getMessage(), Toast.LENGTH_SHORT).show();
//    }

//    private void populateAutoComplete() {
//        if (!mayRequestContacts()) {
//            return;
//        }
//
//        getLoaderManager().initLoader(0, null, this);
//    }

//    private boolean mayRequestContacts() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
//            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
//                    .setAction(android.R.string.ok, new View.OnClickListener() {
//                        @Override
//                        @TargetApi(Build.VERSION_CODES.M)
//                        public void onClick(View v) {
//                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//                        }
//                    });
//        } else {
//            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
//        }
//        return false;
//    }

    /**
     * Callback received when a permissions request has been completed.
     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_CONTACTS) {
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                populateAutoComplete();
//            }
//        }
//    }

    /**
     * judge whether can find login information in the SharedPreferences
     *
     * @return whether need to show the login page
     */
    private boolean judgeLogin() {
        email = preferences.getString("email", null);
        password = preferences.getString("password", null);
        if (null != email && null != password) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressDialog.show();
            //更换成我自己的登录逻辑
//            mAuthTask = new UserLoginTask(name, password);
//            mAuthTask.execute((Void) null);
            login();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 3;
    }


    private void login() {

        /*发送post登录请求*/
        final HashMap<String, String> postData = new HashMap<String, String>();

        try {
            //todo 暂时就这么做
//            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);

            //数据加密
//            final String encryptedEmail = RSAUtils.encryptByPublicKey(email);
//            final String encryptedPassword = RSAUtils.encryptByPublicKey(password);

            postData.put("encryptedEmail", email);
            postData.put("encryptedPassword", password);

            String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_USER_LOGIN_SERVICE);
            HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {

                        //获取session的操作，session放在cookie头，且取出后含有“；”，取出后为下面的 s （也就是jsesseionid）
                        Headers headers = response.headers();
                        List<String> cookies = headers.values("Set-Cookie");
                        if (cookies.size() > 0) {
                            String session = cookies.get(0);
                            Global.sessionId = session.substring(0, session.indexOf(";"));
                        }

                        String responceData = response.body().string();
                        JSONObject jsonObject = new JSONObject(responceData);
                        int result = (int) jsonObject.get("result");
                        JSONObject userInfo = (JSONObject) jsonObject.get("userInfo");
                        if (result == UserConstant.RESULT_SUCCESS) {
                            editor.putInt("userId", (Integer) userInfo.get("id"));
                            editor.putString("email", (String) userInfo.get("email"));
                            editor.putString("password", (String) userInfo.get("password"));
                            editor.putString("userName", (String) userInfo.get("userName"));
                            editor.commit();
                            Intent intent = new Intent(LoginActivity.this, ScMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (result == UserConstant.RESULT_ACCOUNT_NOT_EXIST) {
                            showLoginFailed(UserConstant.RESULT_ACCOUNT_NOT_EXIST);
                        } else if (result == UserConstant.RESULT_WRONG_PASSWORD) {
                            showLoginFailed(UserConstant.RESULT_WRONG_PASSWORD);
                        } else if (result == UserConstant.RESULT_SERVER_ERROR) {
                            showLoginFailed(UserConstant.RESULT_SERVER_ERROR);
                        }
//                        ServletResponseData responseData = JSONUtils.toBean(response.body().string(), ServletResponseData.class);
//                        //判断是否登录成功，如果成功将返回的userId存入application
//                        int result = responseData.getResult();
//                        if (result == 1) {
//                            LoginServlet.ResponseBO responseBO = JSONUtils.toBean(responseData.getData(), LoginServlet.ResponseBO.class);
//                            editor.putString("username",userName);
//                            editor.putString("password",password);
//                            editor.commit();
//
//                            app.setUserId(responseBO.getUserId());
//                            app.setUserName(userName);
//                            app.setCreditPublish(responseBO.getCreditPublish());
//                            app.setCreditWithdraw(responseBO.getCreditWithdraw());
//
//                            Intent i = new Intent(Login.this, EntryActivity.class);
//                            startActivity(i);
//                            finish();
//                        } else {
//                            showLoginFailed(result);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showLoginFailed(UserConstant.RESULT_SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    showLoginFailed(UserConstant.RESULT_SERVER_ERROR);
                }

            });
        } catch (Exception e) {

        }
    }


    //show the login failed message
    private void showLoginFailed(final int result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                progressDialog.dismiss();
//                showProgress(false);//回到登录界面
                if (result == -1) { //用户名不存在
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                } else if (result == -2) { //密码错误
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                } else if (result == -3) {
                    Toast.makeText(LoginActivity.this, getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this,
                            String.valueOf(result), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//    /**
//     * Shows the progress UI and hides the login form.
//     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }

//
//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return new CursorLoader(this,
//                // Retrieve data rows for the device user's 'profile' contact.
//                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
//                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
//
//                // Select only email addresses.
//                ContactsContract.Contacts.Data.MIMETYPE +
//                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
//                .CONTENT_ITEM_TYPE},
//
//                // Show primary email addresses first. Note that there won't be
//                // a primary email address if the user hasn't specified one.
//                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        List<String> emails = new ArrayList<>();
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            emails.add(cursor.getString(ProfileQuery.ADDRESS));
//            cursor.moveToNext();
//        }
//
//        addEmailsToAutoComplete(emails);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//
//    }
//
//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(LoginActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
//        mEmailView.setAdapter(adapter);
//    }
//
//
//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//
//        int ADDRESS = 0;
//        int IS_PRIMARY = 1;
//    }

//    /**
//     * Represents an asynchronous login/registration task used to authenticate
//     * the user.
//     */
//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mName;
//        private final String mPassword;
//
//        UserLoginTask(String name, String password) {
//            mName = name;
//            mPassword = password;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            // TODO: attempt authentication against a network service.
//
//            try {
//                // Simulate network access.
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                return false;
//            }
//
//            /*for (String credential : DUMMY_CREDENTIALS) {
//                String[] pieces = credential.split(":");
//                if (pieces[0].equals(mName)) {
//                    // Account exists, return true if the password matches.
//                    //Context context = getApplicationContext();
//                    //TokenMessage.getInstance().saveUserToken(context,mName + mPassword);
//                    return pieces[1].equals(mPassword);
//                }
//            }*/
//            String param = "param=" + mName + ":" + mPassword;
//            System.out.println(param);
//            String token = WebService.login(param,"login");
//            System.out.println("token:" + token);
//            if(token != null){
//                Context context = getApplicationContext();
//                TokenMessage.getInstance().saveUserToken(context,token);
//                return true;
//            }else {
//                /// TODO: 2018/7/24  暂时设置为true方便调试
//                return true;
//            }
//           /* String message = WebService.executeHttpGet(mEmail,mPassword);
//            if(message == null){
//                return false;
//            }*/
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
//            showProgress(false);
//
//            if (success) {
//                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTask = null;
//            showProgress(false);
//        }
//    }
}

