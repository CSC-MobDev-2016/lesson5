package com.csc.shmakov.filemanager.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.csc.shmakov.filemanager.R;
import com.csc.shmakov.filemanager.models.NavigationModel;
import com.csc.shmakov.filemanager.models.SlideshowModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class SlideshowActivity extends FragmentActivity {
    public static final String POSITION_EXTRA = "position";

    private final NavigationModel navigationModel = NavigationModel.INSTANCE;

    private SlideshowModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        model = SlideshowModel.getInstance(navigationModel.getCurrentFolder());

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(
                model.getSlideshowPositionForFolderPosition(
                        getIntent().getIntExtra(POSITION_EXTRA, 0)));
        if (savedInstanceState == null) {
            Toast.makeText(this, R.string.swipe_tip, Toast.LENGTH_SHORT).show();
        }
    }

    private final FragmentStatePagerAdapter pagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return model.getNumberOfImages();
        }
    };

    public static class PageFragment extends Fragment {
        private SlideshowModel model = SlideshowModel.getInstance();
        private int position;

        private TextView nameTextView;
        private ImageView imageView;
        private ProgressBar loadingBar;

        public static PageFragment newInstance(int position) {
            PageFragment fragment = new PageFragment();
            Bundle args = new Bundle();
            args.putInt(POSITION_EXTRA, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.slideshow_page, container, false);
            nameTextView = (TextView) rootView.findViewById(R.id.image_name_textview);
            imageView = (ImageView) rootView.findViewById(R.id.slideshow_imageview);
            loadingBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

            position = getArguments().getInt(POSITION_EXTRA);
            updateViews();

            return rootView;
        }

        private void updateViews() {
            final String name = model.getImageName(position);
            nameTextView.setText(name);
            Picasso.with(getContext())
                    .load(model.getImageFileAtSlideshowPosition(position))
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadingBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(getContext(), getString(R.string.error_loading_image, name), Toast.LENGTH_LONG).show();
                            loadingBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

}
