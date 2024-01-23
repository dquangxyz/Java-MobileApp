package com.example.umatchapp;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class AccountSingleton {
    private static final AccountSingleton ourInstance = new AccountSingleton();
    private GoogleSignInAccount googleAccount;

    public static AccountSingleton getInstance() {
        return ourInstance;
    }

    private AccountSingleton() {
    }

    public GoogleSignInAccount getGoogleAccount() {
        return googleAccount;
    }

    public void setGoogleAccount(GoogleSignInAccount googleAccount) {
        this.googleAccount = googleAccount;
    }
}
