package com.decawave.argomanager.scontroller.fragment.subfragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.adapter.TaskViewPagerAdapter;
import com.decawave.argomanager.scontroller.fragment.tasksfragment.CrowdsourcingTasksFragment;
import com.decawave.argomanager.scontroller.fragment.tasksfragment.MyTasksFragment;
import com.decawave.argomanager.scontroller.fragment.tasksfragment.SpecifyTasksFragment;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private View view;
    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private List<String> mDatas;
    private List<Fragment> fragments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sub_task, container, false);

        initView();
        initData();
        initFragments();
        //todo 放在这里可以吗？getActivity()有效果吗？
        initAdapter();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        mTablayout = (TabLayout) view.findViewById(R.id.tl_tasks);
        mViewPager = (ViewPager)view. findViewById(R.id.vp_tasks);
    }

    private void initData() {
        mDatas = new ArrayList<>();
        mDatas.add("我的");
        mDatas.add("众包");
        mDatas.add("请求");
    }

    private void initFragments() {
        fragments = new ArrayList<Fragment>();
        MyTasksFragment myTasksFragment = new MyTasksFragment();
        fragments.add(myTasksFragment);
        CrowdsourcingTasksFragment crowdsourcingTasksFragment = new CrowdsourcingTasksFragment();
        fragments.add(crowdsourcingTasksFragment);
        SpecifyTasksFragment specifyTasksFragment = new SpecifyTasksFragment();
        fragments.add(specifyTasksFragment);
    }

    private void initAdapter() {
        mViewPager.setAdapter(new TaskViewPagerAdapter(getActivity().getSupportFragmentManager(), mDatas, fragments));
        //todo    这个东西要不要加上
        mTablayout.setupWithViewPager(mViewPager);
        // 注意：我的布局文件中ViewPager和TabLayout 是嵌套的，所以不需要这一步
        //          如果没有嵌套，则需要在ViewPager设置Adapter之后加上这一步
        // 另外，只能是ViewPager嵌套Tablayout，反了会报错的，因为Tablayout中只能嵌套TabItem
    }
}
