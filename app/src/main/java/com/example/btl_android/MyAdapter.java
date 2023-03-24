package com.example.btl_android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.viewHolder> {

    Context context;
    List<Diary> diaryLst;
    RealmResults<Diary> realmLst;

    public MyAdapter(Context context, List<Diary> diaryLst, RealmResults<Diary> realmLst) {
        this.context = context;
        this.diaryLst = diaryLst;
        this.realmLst = realmLst;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Diary diary = diaryLst.get(position);

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
            intent.putExtras(Diary.toBundle(diary));
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
        return diaryLst.size();
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
}
