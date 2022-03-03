package ch.nblotti.psd2.db;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import javax.inject.Inject;

import ch.nblotti.psd2.CoreModuleDependencies;
import dagger.hilt.android.EntryPointAccessors;


public class DeutscheBankAuthenticatorService extends Service {

    @Inject
    DeutscheBankAuthenticator deutscheBankAuthenticator;

    @Override
    public void onCreate() {


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

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {


        return deutscheBankAuthenticator.getIBinder();
    }


}