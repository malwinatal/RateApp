package com.example.mt.rateapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mt.rateapp.fragments.ItemFragment.OnListFragmentInteractionListener;
import com.example.mt.rateapp.models.Category;
import com.example.mt.rateapp.models.Item;
import com.maltaisn.icondialog.IconHelper;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyCategoryRecyclerViewAdapter extends RecyclerView.Adapter<MyCategoryRecyclerViewAdapter.ViewHolder> {

    private final List<Category> mValues;
    private final OnCategoryListInteractionListener mListener;
    private final Context mContext;

    public MyCategoryRecyclerViewAdapter(List<Category> categories, OnCategoryListInteractionListener listener, Context context) {
        mValues = categories;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mCategory = mValues.get(position);
        holder.name.setText(holder.mCategory.name);
        holder.nrItems.setText("" + holder.mCategory.numberOfItems);
        Drawable drawable = IconHelper.getInstance(mContext).getIcon(holder.mCategory.iconId).getDrawable(mContext);
        holder.icon.setImageDrawable(drawable);

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCategoryEditButtonInteraction(holder.mCategory);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCategoryRemoveButtonInteraction(holder.mCategory);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView icon;
        public final TextView name;
        public final TextView nrItems;
        public final ImageButton editButton;
        public final ImageButton deleteButton;

        public Category mCategory;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = view.findViewById(R.id.category_name);
            nrItems = view.findViewById(R.id.category_items);
            icon = view.findViewById(R.id.category_image);
            editButton = view.findViewById(R.id.button_edit);
            deleteButton = view.findViewById(R.id.button_delete);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }

    public interface OnCategoryListInteractionListener {
        void onCategoryEditButtonInteraction(Category category);
        void onCategoryRemoveButtonInteraction(Category category);
    }
}