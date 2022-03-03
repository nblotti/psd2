package ch.nblotti.psd2.ui.bank;

import static com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.*;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.play.core.splitcompat.SplitCompat;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ModuleInstallerWorkerService extends Service {


    MODULE_SERVICE_STATUS mWorking = MODULE_SERVICE_STATUS.INSTALLED;

    private BigDecimal mDownloadProgress = BigDecimal.ZERO;


    @Inject
    SplitInstallManager splitInstallManager;

    @Inject
    MutableLiveData<List<Bank>> banks;


    private IBinder mBinder = new DeutscheBanksWorkerServiceBinder();


    public void startModuleUnInstall(String moduleName, String serviceName, Boolean restart) {


        if (ModuleInstallerWorkerService.this.mWorking == MODULE_SERVICE_STATUS.STARTED) {
            return;
        }

        if (moduleName == null || moduleName.isEmpty() || serviceName == null || serviceName.isEmpty()) {
            error(moduleName);
            return;
        } else {
            updateStatus(moduleName, MODULE_SERVICE_STATUS.STARTED, BigDecimal.ZERO);
        }
        if (!splitInstallManager.getInstalledModules().contains(moduleName)) {
            error(moduleName);

        } else {
            splitInstallManager.deferredUninstall(Arrays.asList(moduleName))
                    .addOnSuccessListener(sessionId -> {
                        updateStatus(moduleName, MODULE_SERVICE_STATUS.NOT_INSTALLED, BigDecimal.ZERO);
                        updateService(serviceName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                        if (restart)
                            restart();
                    })
                    .addOnFailureListener(exception -> {
                        error(moduleName);
                    });
        }
    }

    private void restart() {
        ProcessPhoenix.triggerRebirth(getApplicationContext());
    }


    public void startModuleInstall(String moduleName, String serviceName) {

        if (ModuleInstallerWorkerService.this.mWorking == MODULE_SERVICE_STATUS.STARTED) {
            return;
        }
        if (moduleName == null || moduleName.isEmpty() || serviceName == null || serviceName.isEmpty()) {
            error(moduleName);
            return;
        } else {
            updateStatus(moduleName, MODULE_SERVICE_STATUS.STARTED, BigDecimal.ZERO);

        }
        if (splitInstallManager.getInstalledModules().contains(moduleName)) {
            error(moduleName);
        } else {
            doStartInstall(moduleName, serviceName);
        }

    }

    private void error(String moduleName) {
        updateStatus(moduleName, MODULE_SERVICE_STATUS.ERROR, BigDecimal.valueOf(100));
    }


    private void doStartInstall(String moduleName, String serviceName) {

        SplitInstallRequest request = SplitInstallRequest.newBuilder()
                .addModule(moduleName)
                .build();

        registerModuleListener(moduleName, serviceName);

        splitInstallManager.startInstall(request)
                .addOnFailureListener(exception -> {
                    error(moduleName);
                });

        updateData(moduleName);
    }

    private void registerModuleListener(String moduleName, String serviceName) {
        splitInstallManager.registerListener(new SplitInstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(@NonNull SplitInstallSessionState splitInstallSessionState) {

                if (splitInstallSessionState.moduleNames().stream().anyMatch(s -> moduleName.equals(s))) {

                    switch (splitInstallSessionState.status()) {
                        case SplitInstallSessionStatus.DOWNLOADING:
                        case SplitInstallSessionStatus.INSTALLING:
                            BigDecimal downloaded = BigDecimal.valueOf(splitInstallSessionState.totalBytesToDownload());
                            BigDecimal toDownload = BigDecimal.valueOf(splitInstallSessionState.totalBytesToDownload());
                            if (!downloaded.equals(BigDecimal.ZERO)) {
                                BigDecimal currentStatus = downloaded.divide(toDownload, 4, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(100));
                                updateStatus(moduleName, MODULE_SERVICE_STATUS.STARTED, currentStatus);
                            }

                            break;
                        case SplitInstallSessionStatus.INSTALLED:
                            updateStatus(moduleName, MODULE_SERVICE_STATUS.INSTALLED, BigDecimal.valueOf(100));
                            updateService(serviceName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
                            break;


                        case REQUIRES_USER_CONFIRMATION:
                        case FAILED:
                        case CANCELING:
                        case CANCELED:
                            error(moduleName);
                            break;

                        case UNKNOWN:
                        case PENDING:
                            break;
                    }

                }

            }

        });
    }

    private void updateStatus(String moduleName, MODULE_SERVICE_STATUS status, BigDecimal mDownloadProgress) {
        this.mWorking = status;
        this.mDownloadProgress = mDownloadProgress;
        updateData(moduleName);
    }

    private void updateData(String moduleName) {


        List<Bank> updatedBanks = banks.getValue().stream().map(b -> {
            if (b.getModuleName().equals(moduleName)) {
                b.setInstalled(this.mWorking);
                b.setInstallationStatus(this.mDownloadProgress);
            }
            return b;
        }).collect(Collectors.toList());

        banks.postValue(updatedBanks);
    }


    private void updateService(String serviceName, int status) {

        final ComponentName componentName = new ComponentName(this, serviceName);

        getPackageManager().setComponentEnabledSetting(
                componentName,
                status,
                PackageManager.DONT_KILL_APP);

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        SplitCompat.install(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    public class DeutscheBanksWorkerServiceBinder extends Binder {

        ModuleInstallerWorkerService getService() {
            return ModuleInstallerWorkerService.this;
        }
    }


}
