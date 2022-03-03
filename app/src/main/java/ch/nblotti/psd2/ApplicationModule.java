package ch.nblotti.psd2;

import android.accounts.AccountManager;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import ch.nblotti.psd2.ui.bank.BANKSERVICE;
import ch.nblotti.psd2.ui.bank.Bank;
import ch.nblotti.psd2.ui.bank.MODULE_SERVICE_STATUS;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ApplicationModule {


    @Provides
    AccountManager provideAccountManager(@ApplicationContext Context context) {

        return AccountManager.get(context);
    }

    @Provides
    SplitInstallManager provideSplitInstallManager(@ApplicationContext Context context) {

        return SplitInstallManagerFactory.create(context);
    }


    @Provides
    @Singleton
    MutableLiveData<List<Bank>> provideBankLiveDataSet(@ApplicationContext Context context, List<Bank> banks) {

        MutableLiveData<List<Bank>> banksLiveData = new MutableLiveData<>();

        banksLiveData.postValue(banks);
        return banksLiveData;

    }

    @Provides
    @Singleton
    List<Bank> provideBankListDataSet(@ApplicationContext Context context, SplitInstallManager splitInstallManager) {



        List<Bank> list = new ArrayList<Bank>();
        Bank db = new Bank(context.getString(R.string.db_bank_name), getMipmapResIdByName(context, "deutschebanklogowithoutwordmark"), new BANKSERVICE[]{BANKSERVICE.AISP}, "db", "ch.nblotti.psd2.db.DeutscheBankAuthenticatorService",  splitInstallManager.getInstalledModules().contains("db")?MODULE_SERVICE_STATUS.INSTALLED :MODULE_SERVICE_STATUS.NOT_INSTALLED,BigDecimal.ZERO);
        Bank db1 = new Bank(context.getString(R.string.pictet_bank_name), getMipmapResIdByName(context, "pictet"), new BANKSERVICE[]{BANKSERVICE.AISP, BANKSERVICE.PISP}, "pictet", "",   splitInstallManager.getInstalledModules().contains("pictet")?MODULE_SERVICE_STATUS.INSTALLED :MODULE_SERVICE_STATUS.NOT_INSTALLED,BigDecimal.ZERO);

        list.add(db);
        list.add(db1);

        return list;

    }


    private int getMipmapResIdByName(Context context, String resName) {
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName, "mipmap", pkgName);
        return resID;

    }


}
