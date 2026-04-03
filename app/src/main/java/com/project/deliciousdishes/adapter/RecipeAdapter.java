package com.project.deliciousdishes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.deliciousdishes.R;
import com.project.deliciousdishes.model.RecipeModel;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<RecipeModel> items;
    private RecipeAdapter.onSelectData onSelectData;
    private Context mContext;

    public interface onSelectData {
        void onSelected(RecipeModel modelMain);
    }

    public RecipeAdapter(Context context, List<RecipeModel> items, RecipeAdapter.onSelectData xSelectData) {
        this.mContext = context;
        this.items = items;
        this.onSelectData = xSelectData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RecipeModel data = items.get(position);

        Glide.with(mContext)
                .load(data.strMealThumb)
                .placeholder(R.drawable.ic_foodimage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgPreview);

        holder.tvFood.setText(data.strMeal);
        holder.cvSelectFood.setOnClickListener(v -> onSelectData.onSelected(data));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //Class Holder
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFood;
        public CardView cvSelectFood;
        public ImageView imgPreview;

        public ViewHolder(View itemView) {
            super(itemView);
            cvSelectFood = itemView.findViewById(R.id.cvSelectFood);
            tvFood       = itemView.findViewById(R.id.tvFood);
            imgPreview     = itemView.findViewById(R.id.imgPreview);
        }
    }

}
