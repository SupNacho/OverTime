package ru.supernacho.overtime.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.supernacho.overtime.App;
import ru.supernacho.overtime.R;
import ru.supernacho.overtime.presenter.LoginPresenter;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends MvpAppCompatActivity implements LoginView {

    private static final int REQUEST_READ_CONTACTS = 0;

    // UI references.
    @BindView(R.id.email)
    AutoCompleteTextView emailView;
    @BindView(R.id.password)
    EditText passwordView;
    @BindView(R.id.login_progress)
    View progressView;
    @BindView(R.id.login_form)
    View loginFormView;
    @BindView(R.id.et_user_name)
    EditText editTextUserName;
    @BindView(R.id.et_confirm_password)
    EditText editTextConfirmPassword;
    @BindView(R.id.btn_confirm_registration)
    Button btnConfirm;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.email_sign_in_button)
    Button btnSignIn;
    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @Inject
    App app;

    @InjectPresenter
    LoginPresenter presenter;

    private boolean isRegistering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        App.getInstance().getAppComponent().inject(this);
        init();
    }

    private void init() {
        populateAutoComplete();
        passwordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    @OnClick({R.id.email_sign_in_button, R.id.btn_cancel,
            R.id.btn_confirm_registration, R.id.btn_register})
    public void onClickSignIn(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                attemptLogin();
                break;
            case R.id.btn_register:
                showHideRegistrationUI();
                break;
            case R.id.btn_confirm_registration:
                presenter.registerUser(editTextUserName.getText().toString(),
                        emailView.getText().toString(), passwordView.getText().toString());
                break;
            case R.id.btn_cancel:
                showHideRegistrationUI();
                break;

        }
    }

    @Override
    public void loginError(String msg) {
        Snackbar.make(editTextUserName, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess() {
        Intent tabsIntent = new Intent(this, TabsActivity.class);
        startActivity(tabsIntent);
        this.finish();
    }

    @Override
    public void registrationSuccess() {
        presenter.checkLoginStatus();
    }

    @Override
    public void registrationFailed() {
        Snackbar.make(editTextUserName, "Registration FAILED", Snackbar.LENGTH_SHORT).show();
    }

    private void showHideRegistrationUI() {
        isRegistering = !isRegistering;
        if (isRegistering) {
            editTextConfirmPassword.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            emailView.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
        } else {
            editTextConfirmPassword.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            emailView.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
            btnRegister.setVisibility(View.VISIBLE);
            btnSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
//
//        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(emailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS));
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = editTextUserName.getText().toString();
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        // Check for a valid email address.
//        if (TextUtils.isEmpty(email)) {
//            emailView.setError(getString(R.string.error_field_required));
//            focusView = emailView;
//            cancel = true;
//        } else if (!isEmailValid(email)) {
//            emailView.setError(getString(R.string.error_invalid_email));
//            focusView = emailView;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            presenter.attemptLogin(userName, password);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

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

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        emailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

