package com.anupbista.store;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class FragmentDashboard extends Fragment{


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Dashboard");
        View dashboardView =  inflater.inflate(R.layout.fragment_dashboard,container,false);

        return dashboardView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView  = view.findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.bottomFragmentContainer, new RecommendationFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selecteedFragment = null;

            switch (item.getItemId()){
                case R.id.home:
                    selecteedFragment = new RecommendationFragment();
                    break;
                case R.id.scan:
                    selecteedFragment = new ScanFragment();
                    break;
            }
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.bottomFragmentContainer, selecteedFragment).commit();
            return true;
        }
    };
}
