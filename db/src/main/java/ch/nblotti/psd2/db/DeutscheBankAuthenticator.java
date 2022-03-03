package ch.nblotti.psd2.db;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.preference.PreferenceManager;


public class DeutscheBankAuthenticator extends AbstractAccountAuthenticator {


    private final Context mContext;
    private final AccountManager accountManager;


    public DeutscheBankAuthenticator(Context context, AccountManager accountManager) {
        super(context);
        // I hate you! Google - set mContext as protected!
        this.mContext = context;
        this.accountManager = accountManager;

    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] strings, Bundle option) throws NetworkErrorException {


        final Intent intent = new Intent(mContext, DeutscheBankAuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountManager.KEY_AUTHENTICATOR_TYPES, mContext.getString(R.string.db_auth_token_type));
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }


    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account accountType, String authTokenType, Bundle options) throws NetworkErrorException {



        String authToken = accountManager.peekAuthToken(accountType, authTokenType);

        final Bundle result = new Bundle();

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, accountType.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }


        return result;
    }


    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }


    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }


    @Override
    public String getAuthTokenLabel(String s) {
        return s;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }


}
