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

package arun.com.chromer.activities.blacklist;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import arun.com.chromer.R;
import arun.com.chromer.data.common.App;
import arun.com.chromer.glide.GlideApp;
import arun.com.chromer.glide.GlideRequests;
import arun.com.chromer.glide.appicon.ApplicationIcon;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Arun on 24/01/2016.
 */
class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.BlackListItemViewHolder> {
    private final List<App> apps = new ArrayList<>();

    private final PublishSubject<App> clickSubject = PublishSubject.create();
    private final GlideRequests glideRequests;

    BlacklistAdapter(@NonNull Activity activity) {
        setHasStableIds(true);
        glideRequests = GlideApp.with(activity);
    }

    @Override
    public BlackListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BlackListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_blacklist_list_item_template, parent, false));
    }

    @Override
    public void onBindViewHolder(final BlackListItemViewHolder holder, int position) {
        final App currApp = apps.get(position);
        holder.appName.setText(currApp.appName);
        holder.appPackage.setText(currApp.packageName);
        holder.appCheckbox.setChecked(currApp.blackListed);

        glideRequests.load(ApplicationIcon.Companion.createUri(currApp.packageName)).into(holder.appIcon);
    }

    @Override
    public void onViewRecycled(BlackListItemViewHolder holder) {
        super.onViewRecycled(holder);
        glideRequests.clear(holder.appIcon);
    }

    public Observable<App> clicks() {
        return clickSubject.asObservable();
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    @Override
    public long getItemId(int position) {
        return apps.get(position).hashCode();
    }

    public void setApps(@NonNull List<App> apps) {
        this.apps.clear();
        this.apps.addAll(apps);
        notifyDataSetChanged();
    }

    void cleanUp() {
    }

    class BlackListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.app_list_icon)
        ImageView appIcon;
        @BindView(R.id.app_list_name)
        TextView appName;
        @BindView(R.id.app_list_package)
        TextView appPackage;
        @BindView(R.id.app_list_checkbox)
        AppCompatCheckBox appCheckbox;
        @BindView(R.id.blacklist_template_root)
        LinearLayout blacklistTemplateRoot;

        BlackListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            appPackage.setVisibility(View.GONE);
            appCheckbox.setOnClickListener(this);
            blacklistTemplateRoot.setOnClickListener(v -> appCheckbox.performClick());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                final App app = apps.get(position);
                app.blackListed = appCheckbox.isChecked();
                clickSubject.onNext(app);
            }
        }
    }
}