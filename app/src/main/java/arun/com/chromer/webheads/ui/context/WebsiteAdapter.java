/*
 * Chromer
 * Copyright (C) 2017 Arunkumar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package arun.com.chromer.webheads.ui.context;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.List;

import arun.com.chromer.R;
import arun.com.chromer.data.website.model.WebSite;
import arun.com.chromer.util.glide.GlideApp;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arun on 05/09/2016.
 */

class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.WebSiteHolder> {
    private final Context context;
    private final List<WebSite> webSites = new ArrayList<>();
    private final WebSiteAdapterListener listener;

    WebsiteAdapter(@NonNull Context context, @NonNull WebSiteAdapterListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public WebSiteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WebSiteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_web_head_context_item_template, parent, false));
    }

    @Override
    public void onBindViewHolder(WebSiteHolder holder, int position) {
        final WebSite webSite = webSites.get(position);
        holder.deleteIcon.setImageDrawable(new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_close)
                .color(ContextCompat.getColor(context, R.color.accent_icon_no_focus))
                .sizeDp(16));
        holder.shareIcon.setImageDrawable(new IconicsDrawable(context)
                .icon(CommunityMaterial.Icon.cmd_share_variant)
                .color(ContextCompat.getColor(context, R.color.accent_icon_no_focus))
                .sizeDp(16));
        holder.url.setText(webSite.preferredUrl());
        holder.title.setText(webSite.safeLabel());
        GlideApp.with(context)
                .load(webSite.faviconUrl)
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return webSites.size();
    }

    @Override
    public long getItemId(int position) {
        return webSites.get(position).hashCode();
    }

    void setWebsites(ArrayList<WebSite> webSites) {
        this.webSites.clear();
        this.webSites.addAll(webSites);
        notifyDataSetChanged();
    }

    List<WebSite> getWebSites() {
        return webSites;
    }

    void delete(@NonNull WebSite webSite) {
        final int index = webSites.indexOf(webSite);
        if (index != -1) {
            webSites.remove(index);
            notifyItemRemoved(index);
            listener.onWebSiteDelete(webSite);
        }
    }

    void update(@NonNull WebSite web) {
        final int index = webSites.indexOf(web);
        if (index != -1) {
            webSites.remove(index);
            webSites.add(index, web);
            notifyItemChanged(index);
        }
    }

    class WebSiteHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.web_site_icon)
        ImageView icon;
        @BindView(R.id.web_site_title)
        TextView title;
        @BindView(R.id.web_site_sub_title)
        TextView url;
        @BindView(R.id.delete_icon)
        ImageView deleteIcon;
        @BindView(R.id.share_icon)
        ImageView shareIcon;

        WebSiteHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                final WebSite webSite = getWebsite();
                if (webSite != null) {
                    listener.onWebSiteItemClicked(webSite);
                }
            });

            itemView.setOnLongClickListener(v -> {
                final WebSite webSite = getWebsite();
                if (webSite != null) {
                    listener.onWebSiteLongClicked(webSite);
                }
                return true;
            });

            deleteIcon.setOnClickListener(v -> {
                final WebSite webSite = getWebsite();
                if (webSite != null) {
                    webSites.remove(webSite);
                    listener.onWebSiteDelete(webSite);
                    notifyDataSetChanged();
                }
            });

            shareIcon.setOnClickListener(v -> {
                final WebSite webSite = getWebsite();
                if (webSite != null) {
                    listener.onWebSiteShare(webSite);
                }
            });
        }

        @Nullable
        private WebSite getWebsite() {
            final int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                return webSites.get(position);
            } else return null;
        }
    }

    interface WebSiteAdapterListener {
        void onWebSiteItemClicked(@NonNull WebSite webSite);

        void onWebSiteDelete(@NonNull WebSite webSite);

        void onWebSiteShare(@NonNull WebSite webSite);

        void onWebSiteLongClicked(@NonNull WebSite webSite);
    }
}
