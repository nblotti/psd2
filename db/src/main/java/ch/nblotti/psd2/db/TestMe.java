package ch.nblotti.psd2.db;

import android.accounts.AccountManager;

import javax.inject.Inject;

public class TestMe {

    @Inject
    public TestMe(AccountManager am) {
        int a = 2;
    }
}
