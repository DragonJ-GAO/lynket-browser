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

package arun.com.chromer.data.website;

import android.app.Application;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import arun.com.chromer.data.website.model.WebColor;
import arun.com.chromer.data.website.model.WebSite;
import arun.com.chromer.util.RxUtils;
import arun.com.chromer.util.parser.RxParser;
import rx.Observable;

/**
 * Network store which freshly parses website data for a given URL.
 */
@Singleton
public class WebsiteNetworkStore implements WebsiteStore {
    private final Context context;

    @Inject
    WebsiteNetworkStore(@NonNull Application application) {
        this.context = application.getApplicationContext();
    }

    @NonNull
    @Override
    public Observable<WebSite> getWebsite(@NonNull String url) {
        return RxParser.parseUrl(url)
                .flatMap(urlArticlePair -> {
                    if (urlArticlePair.second != null) {
                        final WebSite extractedWebsite = WebSite.fromArticle(urlArticlePair.second);
                        // We preserve the original url, otherwise breaks cache.
                        extractedWebsite.url = urlArticlePair.first;
                        return Observable.just(extractedWebsite);
                    } else {
                        return Observable.just(new WebSite(urlArticlePair.first));
                    }
                }).compose(RxUtils.applySchedulers());
    }

    @NonNull
    @Override
    public Observable<Void> clearCache() {
        return Observable.empty();
    }

    @NonNull
    @Override
    public Observable<WebSite> saveWebsite(@NonNull WebSite webSite) {
        return Observable.empty();
    }

    @NonNull
    @Override
    public Observable<WebColor> getWebsiteColor(@NonNull String url) {
        return Observable.empty();
    }

    @Override
    public Observable<WebColor> saveWebsiteColor(@NonNull String host, @ColorInt int color) {
        return Observable.empty();
    }
}
