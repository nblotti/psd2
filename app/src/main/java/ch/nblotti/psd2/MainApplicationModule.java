package ch.nblotti.psd2;

import android.accounts.AccountManager;
import android.content.Context;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;

import java.util.ArrayList;
import java.util.List;

import ch.nblotti.psd2.ui.bank.BANKSERVICE;
import ch.nblotti.psd2.ui.bank.Bank;
import ch.nblotti.psd2.ui.bank.BankListAdapter;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.android.scopes.FragmentScoped;

@Module
@InstallIn(ActivityComponent.class)
public class MainApplicationModule {





}
