package ch.nblotti.psd2;

import android.accounts.AccountManager;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface CoreModuleDependencies {

    public AccountManager accountManager();
}
