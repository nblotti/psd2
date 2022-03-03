package ch.nblotti.psd2.db;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.play.core.splitcompat.SplitCompat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ch.nblotti.psd2.CoreModuleDependencies;
import dagger.hilt.android.EntryPointAccessors;


public class DeutscheBankAuthenticatorActivity extends AppCompatActivity {



    @Inject
    AccountManager am;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deutschebank);

        DaggerDBBankComponent.builder()
                .context(this)
                .appDependencies(
                        EntryPointAccessors.fromApplication(
                                getApplicationContext(),
                                CoreModuleDependencies.class
                        )
                )
                .build()
                .inject(this);

       String url = String.format(getString(R.string.deutscheBankAuthBaseUrl), "token", "513721ac-c63b-4de9-91bb-3aa51b75ea67", "https://psd2.nblotti.org/", "read_accounts", "0.21581183640296075");


        launchCustomTab(url);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Retour de la page d'authentification, on crée le compte
        createDBAccount(intent.getData().getEncodedFragment());
        finish();
    }

    private void createDBAccount(String query) {
        // retour de la page d'authentification de DB, on extrait le token et on met à jour le compte
        Uri uri = getIntent().getData();

        Bundle bundle = getIntent().getExtras();
        AccountAuthenticatorResponse response = bundle.getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        List<String> token = Arrays.stream(query.split("&")).filter(s -> {
            if (s.contains("access_token=")) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }).map(s -> s.substring("access_token=".length())).collect(Collectors.toList());


        if (!token.isEmpty()) {
            String authToken = token.get(0);
            Bundle result = new Bundle();
            Account account = new Account(getString(R.string.db_bank_name), getString(R.string.db_account_type));
            am.addAccountExplicitly(account, getString(R.string.am_account_password), null);
            am.setAuthToken(account, this.getString(R.string.db_auth_token_type), token.get(0));
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            response.onResult(result);

        } else {
            response.onError(1, "No token found");
        }
    }


    private void launchCustomTab(String url) {

        String PACKAGE_NAME = "com.android.chrome";

        Uri uri = Uri.parse(url);

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
        customTabsIntent.intent.setData(uri);


        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(customTabsIntent.intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo resolveInfo : resolveInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (TextUtils.equals(packageName, PACKAGE_NAME))
                customTabsIntent.intent.setPackage(PACKAGE_NAME);
        }

        customTabsIntent.launchUrl(this, uri);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        SplitCompat.install(this);
    }
}