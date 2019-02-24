/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.agrify_admin;

import android.text.TextUtils;

import com.example.agrify_admin.model.Store;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class Filters {

    private String category = null;

    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public Filters() {
    }

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(Store.class.getName());
        filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasCategory() {
        return !(TextUtils.isEmpty(category));
    }


    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }


}
