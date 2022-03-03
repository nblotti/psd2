package ch.nblotti.psd2.db;

import android.accounts.AccountManager;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public  class DBBankModule {

    @Provides
    DeutscheBankAuthenticator provideDeutscheBankAuthenticator(Context context, AccountManager accountManager) {

        return new DeutscheBankAuthenticator(context, accountManager);
    }



}
