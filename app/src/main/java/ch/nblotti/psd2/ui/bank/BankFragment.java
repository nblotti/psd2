package ch.nblotti.psd2.ui.bank;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import ch.nblotti.psd2.R;
import ch.nblotti.psd2.databinding.FragmentBankBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BankFragment extends Fragment {

    private BankViewModel bankViewModel;
    private FragmentBankBinding binding;


    private RecyclerView listView;
    private EditText description;
    private EditText status;

    @Inject
    BankListAdapter bankListAdapter;

    @Inject
    MutableLiveData<List<Bank>> banks;

    private ModuleInstallerWorkerService moduleInstallerWorkerService;
    private MutableLiveData<ModuleInstallerWorkerService.DeutscheBanksWorkerServiceBinder> mBinder = new MutableLiveData<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        bankViewModel =
                new ViewModelProvider(this).get(BankViewModel.class);

        binding = FragmentBankBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listView = binding.recyclerView;

        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.addItemDecoration(new DividerItemDecoration(listView.getContext(), DividerItemDecoration.VERTICAL));
        listView.setAdapter(bankListAdapter);


        bankListAdapter.setOnListItemClick(new BankListItemClickListener() {
            @Override
            public void onListItemClick(int itemPosition) {


                BankListAdapter.ViewHolder holder = (BankListAdapter.ViewHolder) listView.findViewHolderForAdapterPosition(itemPosition);

                String question = String.format(getString(R.string.confirm_disconnect), holder.getBankName().getText());
                if (!holder.getCheckBox().isChecked())
                    question = String.format(getString(R.string.confirm_connect), holder.getBankName().getText());


                new AlertDialog.Builder(getActivity()).setTitle("Confirmation")
                        .setMessage(question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                int a;
                                if (!holder.getCheckBox().isChecked())
                                    moduleInstallerWorkerService.startModuleInstall(holder.getModuleName(), holder.getServiceName());
                                else {

                                    new AlertDialog.Builder(getActivity()).setTitle("Confirmation")
                                            .setMessage(getString(R.string.confirm_restart))
                                            .setPositiveButton(R.string.restart_now, new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                    moduleInstallerWorkerService.startModuleUnInstall(holder.getModuleName(), holder.getServiceName(), Boolean.TRUE);

                                                }
                                            }).setNegativeButton(R.string.restart_later, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            moduleInstallerWorkerService.startModuleUnInstall(holder.getModuleName(), holder.getServiceName(), Boolean.FALSE);

                                        }
                                    }).show();
                                }
                            }
                        }).setNegativeButton(android.R.string.cancel, null).show();

            }
        });


        mBinder.observe(this.

                getActivity(), new Observer<ModuleInstallerWorkerService.DeutscheBanksWorkerServiceBinder>() {
            @Override
            public void onChanged(ModuleInstallerWorkerService.DeutscheBanksWorkerServiceBinder
                                          deutscheBanksWorkerServiceBinder) {
                if (deutscheBanksWorkerServiceBinder != null)
                    moduleInstallerWorkerService = deutscheBanksWorkerServiceBinder.getService();
                else
                    moduleInstallerWorkerService = null;
            }
        });

        banks.observe(getViewLifecycleOwner(), new Observer<List<Bank>>() {
            @Override
            public void onChanged(List<Bank> banks) {
                bankListAdapter.updateUserList(banks);
            }
        });

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startService();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        startService();
    }

    private void startService() {

        Intent serviceIntent = new Intent(getContext(), ModuleInstallerWorkerService.class);
        getActivity().startService(serviceIntent);
        bindService();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(serviceConnection);
    }

    private void bindService() {
        Intent serviceIntent = new Intent(getContext(), ModuleInstallerWorkerService.class);
        boolean started = getActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ModuleInstallerWorkerService.DeutscheBanksWorkerServiceBinder binder = (ModuleInstallerWorkerService.DeutscheBanksWorkerServiceBinder) iBinder;
            mBinder.postValue(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBinder.postValue(null);
        }
    };


    /*
    private void createAccount() {
        createAccountAndSetToken(getActivity().getString(R.string.db_account_type), getActivity().getString(R.string.db_auth_token_type));
    }



    private AccountManagerFuture<Bundle> createAccountIfNotExists(String accountType, String dbAuthType) {


        //create account
        return accountManager.addAccount(accountType, dbAuthType, null, null, getActivity(),
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {

                        createAccountAndSetToken(accountType, dbAuthType);
                    }

                }, null);

    }

    private void createAccountAndSetToken(String accountType, String dbAuthType) {
        List<Account> accounts = Arrays.asList(accountManager.getAccountsByType(accountType));

        if (accounts.isEmpty()) {
            createAccountIfNotExists(accountType, dbAuthType);
        } else {
            Account a = (accounts.get(0));
        }
    }
*/
}