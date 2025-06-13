package com.example.news_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<CardItem> itemList;
    private Context context;

    public CardAdapter(List<CardItem> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        CardItem item = itemList.get(position);

        holder.title.setText(item.getHeadline());

        String base64Image = item.getImage();
        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                if (base64Image.contains(",")) {
                    base64Image = base64Image.split(",")[1];
                }
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                holder.image.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                holder.image.setImageResource(R.drawable.news_image1);
            }
        } else {
            holder.image.setImageResource(R.drawable.news_image1);
        }

        holder.itemView.setOnClickListener(v -> openNewsDetail(item));
        holder.readMore.setOnClickListener(v -> openNewsDetail(item));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, readMore;

        public CardViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageCard);
            title = itemView.findViewById(R.id.titleCard);
            readMore = itemView.findViewById(R.id.readMoreText);
        }
    }

    private void openNewsDetail(CardItem item) {
        Intent intent = new Intent(context, NewsViewActivity.class);
        intent.putExtra("title", item.getHeadline());
        intent.putExtra("image", item.getImage());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("date", item.getDate());
        intent.putExtra("category", item.getCategory());
        context.startActivity(intent);
    }
}
