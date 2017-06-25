package com.anyasoft.es.surveyapp.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.anyasoft.es.surveyapp.SurveyCriteriaFragment;
import com.anyasoft.es.surveyapp.domains.SurveyCriteriaItem;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sai on 31-05-2017.
 */

public class ProfileCategAdapter extends FragmentPagerAdapter {

    ArrayList<SurveyCriteriaItem> statusCountsItems;
    Gson gson = new Gson();

    public ProfileCategAdapter(FragmentManager supportFragmentManager, ArrayList<SurveyCriteriaItem> statusCountsItems) {
        super(supportFragmentManager);
        this.statusCountsItems = statusCountsItems;
    }

    @Override
    public Fragment getItem(int position) {
        SurveyCriteriaItem surveyCriteriaItem = statusCountsItems.get(position);
        SurveyCriteriaFragment surveyCriteriaFragment = SurveyCriteriaFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString(SurveyCriteriaFragment.DATA, gson.toJson(surveyCriteriaItem));
        surveyCriteriaFragment.setArguments(bundle);
        return surveyCriteriaFragment;
    }

    @Override
    public int getCount() {
        return statusCountsItems.size();
    }

    /*@Override
    public ProfileCatgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        return new ProfileCatgViewHolder(layoutInflater.inflate(R.layout.row_categories, parent, false));
    }

    @Override
    public void onBindViewHolder(ProfileCatgViewHolder holder, int position) {
        SurveyCriteriaItem surveyCriteriaItem = statusCountsItems.get(position);
        if (surveyCriteriaItem.getCategory() != null)
            holder.tv_catg.setText(surveyCriteriaItem.getCategory());

        if (surveyCriteriaItem.getNoOfSurveys() != null)
            holder.tv_num_surveys_num.setText(surveyCriteriaItem.getNoOfSurveys());
        else
            holder.tv_num_surveys_num.setText("0");

        if (surveyCriteriaItem.getNoOfCanceled() != null)
            holder.tv_cancelled_surveys_num.setText(surveyCriteriaItem.getNoOfCanceled());
        else
            holder.tv_cancelled_surveys_num.setText("0");

        if (surveyCriteriaItem.getNoOfCompleted() != null)
            holder.tv_cmptd_surveys_num.setText(surveyCriteriaItem.getNoOfCompleted());
        else
            holder.tv_cmptd_surveys_num.setText("0");

        if (surveyCriteriaItem.getNoOfOnHold() != null)
            holder.tv_hold_surveys_num.setText(surveyCriteriaItem.getNoOfOnHold());
        else
            holder.tv_hold_surveys_num.setText("0");

        if (surveyCriteriaItem.getNoOfPending() != null)
            holder.tv_pending_surveys_num.setText(surveyCriteriaItem.getNoOfPending());
        else
            holder.tv_pending_surveys_num.setText("0");

    }

    @Override
    public int getItemCount() {
        return statusCountsItems.size();
    }
*/


   /* public class ProfileCatgViewHolder extends RecyclerView.ViewHolder {
        TextView tv_catg, tv_num_surveys_num, tv_cmptd_surveys_num, tv_pending_surveys_num, tv_hold_surveys_num, tv_cancelled_surveys_num;

        public ProfileCatgViewHolder(View itemView) {
            super(itemView);
            tv_catg = (TextView) itemView.findViewById(R.id.tv_catg);
            tv_num_surveys_num = (TextView) itemView.findViewById(R.id.tv_num_surveys_num);
            tv_cmptd_surveys_num = (TextView) itemView.findViewById(R.id.tv_cmptd_surveys_num);
            tv_pending_surveys_num = (TextView) itemView.findViewById(R.id.tv_pending_surveys_num);
            tv_hold_surveys_num = (TextView) itemView.findViewById(R.id.tv_hold_surveys_num);
            tv_cancelled_surveys_num = (TextView) itemView.findViewById(R.id.tv_cancelled_surveys_num);
        }
    }*/
}
