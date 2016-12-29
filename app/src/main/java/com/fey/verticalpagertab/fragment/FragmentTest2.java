package com.fey.verticalpagertab.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fey.verticalpagertab.R;

/**
 * Created by gaoyunfei on 2016-12-28 0028.
 */
public class FragmentTest2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2,container,false);
    }

    public void setText(String item){
        TextView txt = (TextView) getView().findViewById(R.id.fragment_tv);
        txt.setText(item);
    }
}
