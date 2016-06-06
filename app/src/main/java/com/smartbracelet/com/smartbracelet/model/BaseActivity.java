/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartbracelet.com.smartbracelet.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.smartbracelet.com.smartbracelet.activity.MainMenuActivity;

/**
 * A common superclass that keeps track of whether an {@link Activity} has saved its state yet or
 * not.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private boolean mIsSafeToCommitTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsSafeToCommitTransactions = true;
        setupActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsSafeToCommitTransactions = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsSafeToCommitTransactions = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mIsSafeToCommitTransactions = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this, MainMenuActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
