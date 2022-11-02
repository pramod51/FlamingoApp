package com.app.flamingo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.app.flamingo.R;
import com.app.flamingo.application.AttendanceApplication;
import com.app.flamingo.model.PersonModel;
import com.app.flamingo.utils.CommonMethods;

public class UserListToTrackEmployeeCurrentLocationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {
    private final int EMPTY_VIEW = 77777;
    private final Activity mActivity;
    private final OnItemClickListener mListener;
    private ArrayList<PersonModel> mList;
    private List<PersonModel> mListFiltered;

    public interface OnItemClickListener {
        void onClick(PersonModel model, int position);
    }

    public UserListToTrackEmployeeCurrentLocationsAdapter(Activity activity, ArrayList<PersonModel> list,
                                                          OnItemClickListener listener) {
        mActivity = activity;
        mList = list;
        mListFiltered=list;
        mListener = listener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
        if (viewType == EMPTY_VIEW) {
            return new EmptyViewHolder(layoutInflater.inflate(R.layout.nothing_yet, parent, false));
        } else {
            return new MyViewHolder(layoutInflater.inflate(R.layout.row_user_list_to_track_current_location, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) != EMPTY_VIEW) {
            final MyViewHolder itemView = (MyViewHolder) holder;
            // Bind data to itemView
            final PersonModel personModel = mListFiltered.get(position);

            if (personModel.getProfileImage() != null
                    && personModel.getProfileImage().trim().length() > 0) {
                CommonMethods.loadImage(mActivity,personModel.getProfileImage(),itemView.ivUserProfileImage,
                        ContextCompat.getDrawable(mActivity, R.drawable.img_module_user_profile));
            }else{
                CommonMethods.loadDefaultImage(mActivity,itemView.ivUserProfileImage,
                        ContextCompat.getDrawable(mActivity, R.drawable.img_module_user_profile));
            }

            itemView.tvPersonName.setText(personModel.getName());
            itemView.tvPersonMobileNo.setText(personModel.getMobileDialerCode() != null ?
                    personModel.getMobileDialerCode().concat(personModel.getMobileNo()) : personModel.getMobileNo());
            itemView.ivNext.setVisibility(personModel.isTrackingEnable()?View.VISIBLE:View.INVISIBLE);
            itemView.tvTrackingStatus.setText(personModel.isTrackingEnable()?"Start":"Stop");
            itemView.switchToTrackEmployee.setChecked(personModel.isTrackingEnable());
            itemView.switchToTrackEmployee.setTag(position);
            itemView.switchToTrackEmployee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(CommonMethods.isNetworkConnected(mActivity)) {
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("trackingEnable", isChecked);

                        CommonMethods.showProgressDialog(mActivity);
                        AttendanceApplication.refCompanyUserDetails
                                .child(personModel.getFirebaseKey())
                                .updateChildren(result)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        CommonMethods.cancelProgressDialog();
                                        personModel.setTrackingEnable(isChecked);
                                        itemView.tvTrackingStatus.setText(isChecked?"Start":"Stop");
                                        itemView.switchToTrackEmployee.setOnCheckedChangeListener(null);
                                        //Notify employee about tracking
                                        CommonMethods.notifyTrackingStatusToEmployee(mActivity,personModel);
                                        notifyItemChanged((int)buttonView.getTag());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        CommonMethods.cancelProgressDialog();
                                        buttonView.setChecked(!isChecked);
                                        itemView.tvTrackingStatus.setText(!isChecked?"Start":"Stop");
                                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        buttonView.setChecked(!isChecked);
                        itemView.tvTrackingStatus.setText(!isChecked?"Start":"Stop");
                        CommonMethods.showConnectionAlert(mActivity);
                    }

                }
            });

            itemView.rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(personModel.isTrackingEnable()) {
                        mListener.onClick(personModel, position);
                    }
                }
            });
        }else{
            final EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            emptyViewHolder.tvAlertMessage.setText(mActivity.getString(R.string.label_no_employee_added_for_this_branch));
        }
    }

    @Override
    public int getItemCount() {
        return mListFiltered.size() > 0 ? mListFiltered.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mListFiltered.size() == 0) {
            return EMPTY_VIEW;
        }
        //return super.getItemViewType(position);
        return position;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mListFiltered = mList;
                } else {
                    List<PersonModel> filteredList = new ArrayList<>();
                    for (PersonModel row : mList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mListFiltered = (ArrayList<PersonModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlertMessage;

        EmptyViewHolder(View view) {
            super(view);
            tvAlertMessage =  view.findViewById(R.id.tvAlertMessage);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlRoot;
        ImageView ivUserProfileImage;
        TextView tvPersonName;
        TextView tvPersonMobileNo;
        SwitchCompat switchToTrackEmployee;
        ImageView ivNext;
        TextView tvTrackingStatus;

        public MyViewHolder(View view) {
            super(view);
            rlRoot =  itemView.findViewById(R.id.rl_root);
            ivUserProfileImage =  itemView.findViewById(R.id.iv_user_profile_image);
            tvPersonName =  itemView.findViewById(R.id.tv_person_name);
            tvPersonMobileNo =  itemView.findViewById(R.id.tv_person_mobile_no);
            switchToTrackEmployee =  itemView.findViewById(R.id.switch_track_employee);
            ivNext =  itemView.findViewById(R.id.iv_next);
            tvTrackingStatus =  itemView.findViewById(R.id.tv_tracking_status);
        }
    }

    public void deleteItem(int position){
        if(mList!=null){
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addData(ArrayList<PersonModel> list){
        if(mList!=null)
            mList.clear();
        else
            mList=new ArrayList<>();

        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear(){
        if(mList!=null)
            mList.clear();
        if(mListFiltered!=null)
            mListFiltered.clear();
    }
}

