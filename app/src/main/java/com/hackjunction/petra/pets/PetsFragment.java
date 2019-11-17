/*
 * Copyright 2016, The Android Open Source Project
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

package com.hackjunction.petra.pets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hackjunction.petra.R;
import com.hackjunction.petra.addeditpet.AddEditPetActivity;
import com.hackjunction.petra.components.ScrollChildSwipeRefreshLayout;
import com.hackjunction.petra.data.Pet;
import com.hackjunction.petra.devices.DevicesActivity;
import com.hackjunction.petra.di.ActivityScoped;
import com.hackjunction.petra.petdetail.PetDetailActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link Pet}s. User can choose to view all, active or completed pets.
 */
@ActivityScoped
public class PetsFragment extends DaggerFragment implements PetsContract.View {

    @Inject
    PetsContract.Presenter mPresenter;
    /**
     * Listener for clicks on pets in the ListView.
     */
    PetItemListener mItemListener = new PetItemListener() {
        @Override
        public void onPetClick(Pet clickedPet) {
            mPresenter.openPetDetails(clickedPet);
        }
    };
    private PetsAdapter mListAdapter;
    private View mNoPetsView;
    private ImageView mNoPetIcon;
    private TextView mNoPetMainView;
    private TextView mNoPetAddView;
    private LinearLayout mPetsView;
    private TextView mFilteringLabelView;

    @Inject
    public PetsFragment() {
        // Requires empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new PetsAdapter(new ArrayList<Pet>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.takeView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();  //prevent leaking activity in
        // case presenter is orchestrating a long running pet
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pets_frag, container, false);

        // Set up pets view
        ListView listView = root.findViewById(R.id.pets_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = root.findViewById(R.id.filteringLabel);
        mPetsView = root.findViewById(R.id.petsLL);

        // Set up  no pets view
        mNoPetsView = root.findViewById(R.id.noPets);
        mNoPetIcon = root.findViewById(R.id.noPetsIcon);
        mNoPetMainView = root.findViewById(R.id.noPetsMain);
        mNoPetAddView = root.findViewById(R.id.noPetsAdd);
        mNoPetAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddPet();
            }
        });

        // Set up floating action button
        FloatingActionButton fab = getActivity().findViewById(R.id.fab_add_pet);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewPet();
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(listView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPets(false);
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                // TODO remove
                break;
            case R.id.menu_refresh:
                mPresenter.loadPets(true);
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pets_fragment_menu, menu);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl = getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showPets(List<Pet> pets) {
        mListAdapter.replaceData(pets);

        mPetsView.setVisibility(View.VISIBLE);
        mNoPetsView.setVisibility(View.GONE);
    }

    @Override
    public void showNoPets() {
        showNoPetsViews(
                getResources().getString(R.string.no_pets_all),
                R.drawable.no_data,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_pet_message));
    }

    private void showNoPetsViews(String mainText, int iconRes, boolean showAddView) {
        mPetsView.setVisibility(View.GONE);
        mNoPetsView.setVisibility(View.VISIBLE);

        mNoPetMainView.setText(mainText);
        //noinspection deprecation
        mNoPetIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoPetAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showAddPet() {
//        Intent intent = new Intent(getContext(), AddEditPetActivity.class);
//        startActivityForResult(intent, AddEditPetActivity.REQUEST_ADD_PET);

        Intent intent = new Intent(getContext(), DevicesActivity.class);
        startActivity(intent);
    }

    @Override
    public void showPetDetailsUi(String petId) {
        //Shown in it's own Activity, since it makes more sense that way
        // and it gives us the flexibility to show some Intent stubbing.
        Intent intent = new Intent(getContext(), PetDetailActivity.class);
        intent.putExtra(PetDetailActivity.EXTRA_PET_ID, petId);
        startActivity(intent);
    }

    @Override
    public void showLoadingPetsError() {
        showMessage(getString(R.string.loading_pets_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    public interface PetItemListener {

        void onPetClick(Pet clickedPet);
    }

    private static class PetsAdapter extends BaseAdapter {

        private List<Pet> mPets;
        private PetItemListener mItemListener;

        public PetsAdapter(List<Pet> pets, PetItemListener itemListener) {
            setList(pets);
            mItemListener = itemListener;
        }

        public void replaceData(List<Pet> pets) {
            setList(pets);
            notifyDataSetChanged();
        }

        private void setList(List<Pet> pets) {
            mPets = checkNotNull(pets);
        }

        @Override
        public int getCount() {
            return mPets.size();
        }

        @Override
        public Pet getItem(int i) {
            return mPets.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.pet_item, viewGroup, false);
            }

            final Pet pet = getItem(i);

            TextView nameTV = rowView.findViewById(R.id.name);
            nameTV.setText(pet.getTitleForList());

            // TODO remove
//            CheckBox completeCB = rowView.findViewById(R.id.complete);
//            completeCB.setChecked(false);;
//            if (completeCB.isChecked()) {
//                //noinspection deprecation (api <16)
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
//            } else {
//                //noinspection deprecation (api <16)
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.touch_feedback));
//            }
//
//            completeCB.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // TODO remove
//                }
//            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onPetClick(pet);
                }
            });

            return rowView;
        }
    }

}
