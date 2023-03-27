package com.example.btl_android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.viewHolder> {
    private DiaryContentFilter filter;
    private Context context;
    private RealmResults<Diary> realmLst;

    private List<Diary> showList;
    private List<Diary> originList;

    public DiaryAdapter(Context context, List<Diary> diaryLst, RealmResults<Diary> realmLst) {
        this.context = context;
        this.showList = diaryLst;
        this.realmLst = realmLst;
        originList = new ArrayList<>(diaryLst);
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Diary diary = showList.get(position);

        holder.content.setText(diary.getContent());
        holder.title.setText(diary.getTitle());
        holder.date.setText(diary.getDate());
        holder.time.setText(diary.getTime());

        try {
            if (diary.getImageUri() != null) {
                holder.image.setImageURI(Uri.parse(diary.getImageUri()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DiaryActivity.class);
            Bundle b = Diary.toBundle(diary);

            if(context instanceof CalendarActivity) {
                b.putBoolean("isCalendarActivity",true);
            }

            intent.putExtras(b);
            context.startActivity(intent);
        });

        // delete
        holder.itemView.setOnLongClickListener(v -> {

            PopupMenu menu = new PopupMenu(context, v);
            menu.getMenu().add("XÓA");
            menu.setOnMenuItemClickListener(item -> {
                if(item.getTitle().equals("XÓA")){
                    // remove image uri
                    holder.image.setImageURI(null);

                    Diary realmDiaryObject = null;
                    for(Diary t : realmLst) {
                        if (t.getId() == diary.getId()) {
                            realmDiaryObject = t;
                        }
                    }

                    //delete diary
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    Objects.requireNonNull(realmDiaryObject).deleteFromRealm();
                    realm.commitTransaction();

                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            menu.show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView content;
        TextView title;
        TextView date;
        TextView time;
        ImageView image;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            content = itemView.findViewById(R.id.item_content);
            date = itemView.findViewById(R.id.item_date);
            time = itemView.findViewById(R.id.item_time);
            image = itemView.findViewById(R.id.item_image);
        }
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new DiaryContentFilter();
        }
        return filter;
    }

    class DiaryContentFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toUpperCase();

                List<Diary> filters = new ArrayList<>();

                for (Diary d : showList) {
                    if (d.getContent().toUpperCase().contains(constraint)) {
                        filters.add(new Diary(d));
                    }
                }

                results.count = filters.size();
                results.values = filters;
            }
            else {
                results.count = originList.size();
                results.values = originList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            showList = (List<Diary>) results.values;
            notifyDataSetChanged();
        }
    }
}
