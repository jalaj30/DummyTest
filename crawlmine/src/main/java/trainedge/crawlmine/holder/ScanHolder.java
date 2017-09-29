package trainedge.crawlmine.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import trainedge.crawlmine.R;

/**
 * Created by Jalaj on 9/16/2017.
 */

public class ScanHolder extends RecyclerView.ViewHolder {

    public TextView scanText;
    public ScanHolder(View itemView) {
        super(itemView);

        scanText= (TextView) (itemView).findViewById(R.id.scantext);
    }


}