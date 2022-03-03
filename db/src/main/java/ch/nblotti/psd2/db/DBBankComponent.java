package ch.nblotti.psd2.db;

import android.content.Context;

import ch.nblotti.psd2.CoreModuleDependencies;
import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {DBBankModule.class}, dependencies = CoreModuleDependencies.class)
public interface DBBankComponent {

    void inject(DeutscheBankAuthenticatorService deutscheBankAuthenticatorService);

    void inject(DeutscheBankAuthenticatorActivity deutscheBankAuthenticatorActivity);

    @Component.Builder
    interface Builder {
        Builder context(@BindsInstance Context context);

        Builder appDependencies(CoreModuleDependencies coreModuleDependencies);

        DBBankComponent build();
    }


}
