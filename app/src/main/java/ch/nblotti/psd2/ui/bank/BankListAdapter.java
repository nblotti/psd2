package ch.nblotti.psd2.ui.bank;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import ch.nblotti.psd2.R;
import lombok.Getter;
import lombok.Setter;


public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.ViewHolder> {

    private List<Bank> localBankDataSet = new ArrayList<>();

    @Setter
    private BankListItemClickListener onListItemClick;


    public BankListAdapter() {
        ;
    }


    @Getter
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView bankPicture;
        private final TextView bankName;
        private final TextView bankService;
        private final AppCompatCheckBox checkBox;
        private final ProgressBar progressBar;


        private String serviceName;
        private String moduleName;
        private Boolean installed = Boolean.FALSE;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            bankPicture = (ImageView) view.findViewById(R.id.imageViewBankPicture);
            bankName = (TextView) view.findViewById(R.id.textViewBankName);
            bankService = (TextView) view.findViewById(R.id.textViewBankService);
            checkBox = (AppCompatCheckBox) view.findViewById(R.id.checkboxBankConnect);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);


        }


        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }


        @Override
        public void onClick(View view) {
            BankListAdapter.this.onListItemClick.onListItemClick(getAdapterPosition());
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bank_list_item_layout, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(viewHolder);
        return viewHolder;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getBankPicture().setImageResource(localBankDataSet.get(position).getBankPictureId());
        viewHolder.getBankName().setText(localBankDataSet.get(position).getBankName());
        viewHolder.getBankService().setText(getServices(localBankDataSet.get(position).getBankservices()));
        viewHolder.setModuleName(localBankDataSet.get(position).getModuleName());
        viewHolder.setServiceName(localBankDataSet.get(position).getServiceName());
        switch (localBankDataSet.get(position).getInstalled()) {

            case NOT_INSTALLED:
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.getCheckBox().setChecked(Boolean.FALSE);
                break;
            case STARTED:
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.getCheckBox().setChecked(Boolean.FALSE);
                viewHolder.progressBar.setProgress(localBankDataSet.get(position).getInstallationStatus().intValue(), Boolean.TRUE);
                break;
            case INSTALLED:
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.getCheckBox().setChecked(Boolean.TRUE);
                break;
            case ERROR:
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                viewHolder.progressBar.setProgress(localBankDataSet.get(position).getInstallationStatus().intValue(), Boolean.TRUE);
                viewHolder.getCheckBox().setChecked(Boolean.FALSE);
                break;
        }


    }

    private String getServices(BANKSERVICE[] bankservice) {

        String services = "";
        for (BANKSERVICE current : bankservice) {
            if (!services.isEmpty())
                services = String.format("%s, %s", services, StringUtils.capitalize(current.toString()));
            else
                services = String.format("%s", StringUtils.capitalize(current.toString()));
        }
        return services;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localBankDataSet.size();
    }


    public void updateUserList(final List<Bank> banks) {
        this.localBankDataSet.clear();
        this.localBankDataSet = banks;
        notifyDataSetChanged();
    }

}