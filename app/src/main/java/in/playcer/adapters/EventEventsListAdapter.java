package in.playcer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.playcer.R;
import in.playcer.model.EventsListItemData;

/**
 * Created by HARIKRISHNA on 02/20/2016. at CaretTech
 */

public class EventEventsListAdapter extends RecyclerView.Adapter<EventEventsListAdapter.ViewHolder> {
    Context mContext;
    OnItemClickListener mItemClickListener;
    private ArrayList<EventsListItemData> listData;

    public EventEventsListAdapter(Context context, ArrayList<EventsListItemData> listData) {
        this.mContext = context;
        this.listData = listData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public LinearLayout placeHolder;
        public LinearLayout placeNameHolder;
        public TextView placeName;
        public TextView dateTV;
        public ImageView placeImage;

        public ViewHolder(View itemView) {
            super(itemView);
            placeHolder = (LinearLayout) itemView.findViewById(R.id.mainHolder);
            placeName = (TextView) itemView.findViewById(R.id.placeName);
            dateTV = (TextView) itemView.findViewById(R.id.date);
            placeNameHolder = (LinearLayout) itemView.findViewById(R.id.placeNameHolder);
            placeImage = (ImageView) itemView.findViewById(R.id.placeImage);

            placeHolder.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public int getItemCount() {
        return listData.size();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row_places, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.placeName.setText("" + Html.fromHtml(listData.get(position).get_event_name()));
        holder.dateTV.setText(""+ Html.fromHtml(listData.get(position).get_event_date()));
        Picasso.with(mContext).load(listData.get(position).get_eventImageUtl()).placeholder(R.drawable.placeholder).into(holder.placeImage);
    }
}