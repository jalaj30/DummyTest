    package trainedge.crawlmine.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import trainedge.crawlmine.R;
import trainedge.crawlmine.activity.ScanActivity;
import trainedge.crawlmine.holder.ScanHolder;
import trainedge.crawlmine.model.ScanModel;

/**
 * Created by Jalaj on 9/16/2017.
 */

public class ScanAdapter extends RecyclerView.Adapter<ScanHolder> {
    private final ScanActivity activity;
    private final List<EntityAnnotation> dataList;

    public ScanAdapter(ScanActivity activity, List<EntityAnnotation> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }


    @Override
    public ScanHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(activity).inflate(R.layout.simple_scan_card, parent, false);

        return new ScanHolder(v);

    }

    @Override
    public void onBindViewHolder(ScanHolder holder, int position) {

        EntityAnnotation model = dataList.get(position);
        try {
            holder.scanText.setText(model.getDescription() + model.getBoundingPoly().toPrettyString());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
